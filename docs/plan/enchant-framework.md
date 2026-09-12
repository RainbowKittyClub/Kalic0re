# Enchantment framework

A `common/enchant` package in `rkcore` that sibling mods use to declare an enchantment — its cost,
targets and effects — and get back a generated datapack entry, enchanting-table availability, and a
place to register custom Java code that runs when the effect vocabulary vanilla ships isn't enough.
Unlike `common/armor`, there is no render backend to choose and no mixin layer: an enchantment is
data from end to end, and vanilla's own combat/tick/equip code already calls into it.

Everything below about the 26.2 enchantment system was read out of
`libs/decompiledjava/minecraft-26.2` (cited by file and line) and, for the datagen mechanism, out of
a decompile of the exact Fabric API jar this repo depends on
(`fabric_version=0.157.0+26.2`, resolved to `fabric-data-generation-api-v1-25.5.0+aed122aa9e.jar`).
Nothing here has been run — no stage is `done` yet, and the harness in S1 is what the rest verifies
against.

S1 is the harness, for the same reason armor's S1 was: `rkcore` registers no content of its own, so
nothing here is observable without a consumer. S2 and S3 are independent of each other and both
compile against nothing but vanilla: S2 is the enchantment declaration, S3 is the custom-effect-type
registry. S4 turns declared enchantments into a datapack entry and depends on S2. S5 makes that entry
reachable in survival (enchanting-table tag, lang) and depends on S4. S6 is the in-world proof and is
what makes the feature complete — it exercises a declared enchantment with at least one custom effect
from S3, not just vanilla's own effect vocabulary, since that's the part nothing else has tested.

## How these stages get tested

In-world, on the S1 harness's dev server. Every stage that changes what can be observed gets a plain
"what you'll see" before it runs, per house convention. `rkcore` cannot be tested alone — it
registers no content — so S1 is a prerequisite, not a convenience.

A stage is not finished until its "Done when" is observed. A failure at stage N then implicates N
rather than anything before it.

## Findings that reshape the work

**Enchantments are a datapack registry, not code-registered content.** `Enchantment` is a plain
record with a `DIRECT_CODEC` (`Enchantment.java:60-70`), registered into `Registries.ENCHANTMENT` —
the same shape as loot tables or recipes, not the same shape as an `Item`. Vanilla's own entries are
produced by a `bootstrap(BootstrapContext<Enchantment>)` method (`Enchantments.java:132-1266`) that
is run once, at *datagen* time, to emit the `data/minecraft/enchantment/*.json` files this repo
already ships in `libs/decompiledjava/minecraft-26.2/data/minecraft/enchantment/` — confirmed by
reading `sharpness.json`, `fire_aspect.json` and `soul_speed.json` directly. There is no runtime
registration call for an enchantment the way `Registry.register` works for an `Item`; the framework's
job is entirely to get JSON onto disk in the right shape, mirroring how rk-tweaks treats recipes and
tags as built-in datapack content rather than code.

**Vanilla's own declare-a-thing API is already good — the framework should not wrap it.** Unlike
`EquipmentAssetProvider`/`ItemModelGenerators`, which armor's Findings established cannot be
extended, `Enchantment.Builder` (`Enchantment.java:582-655`) is a plain public builder with no
hardcoded bootstrap to work around. A consumer can call `Enchantment.enchantment(definition)
.withEffect(...).build(id)` directly, exactly as `Enchantments.bootstrap` does for every vanilla
enchantment. Re-deriving that builder as an `EnchantSpec`-side fluent API would be a second, worse
copy of it. So `EnchantSpec` (S2) does not wrap the effect-building calls; it only carries what the
builder itself cannot supply — the registry key, and a factory that produces the built `Enchantment`
once a `HolderLookup.Provider` exists.

**The effect vocabulary is itself an extensible registry — this is the real hook point.** Every
`DataComponentType` in `EnchantmentEffectComponents` holds either a fixed value type or a
dispatch-coded interface — `EnchantmentEntityEffect`, `EnchantmentValueEffect`,
`EnchantmentLocationBasedEffect` — and all three are ordinary `BuiltInRegistries` populated by a
`bootstrap` method exactly like `Item` or `SoundEvent` (`BuiltInRegistries.java:311-324`,
`EnchantmentEntityEffect.java:14-34`, `EnchantmentValueEffect.java:11-22`). A mod can implement one of
these interfaces (a record with a `MapCodec`, the exact shape `ApplyMobEffect` uses at
`effects/ApplyMobEffect.java:20-49`) and `Registry.register` it into the matching
`BuiltInRegistries.ENCHANTMENT_*_EFFECT_TYPE` at mod init, the same way any other simple registry
entry is added. Once registered, any enchantment's generated JSON can reference it by id in an
`effects` component, and vanilla's own code calls it — this is the "hook into the stack and grant a
special effect" surface the task asked for, and it needs no mixin.

**Vanilla's call sites already cover tick, on-hit, on-equip and on-block-hit — nothing here needs a
mixin.** `EnchantmentHelper` calls `tickEffects` from the entity tick loop, `doPostAttackEffects` from
combat, `runLocationChangedEffects` on equipment change, and `onHitBlock`/`onProjectileSpawned` from
projectile code (`EnchantmentHelper.java:210-281,316-335`), each of which walks every enchantment on
the relevant `ItemStack` and calls into `Enchantment.tick`/`doPostAttack`/`runLocationChangedEffects`
(`Enchantment.java:384-390,316-329,528-564`), which in turn calls the registered effect object. A
consumer's custom effect fires automatically once it is on the item and referenced from the
enchantment's JSON; the framework registers the effect *type*, not a call site.

**"On equip" is the location-changed effect, not a separate hook.** There is no
`EnchantmentEquipEffect`; equip and unequip are `EnchantmentLocationBasedEffect.onChangedBlock` and
`.onDeactivated`, fired by `runLocationChangedEffects` exactly when the item starts or stops occupying
a slot the enchantment's `slots` matches (`Enchantment.java:528-564`). `EnchantmentAttributeEffect` —
vanilla's own attribute-modifier effect, used for soul speed's movement speed and respiration's oxygen
bonus — is built on exactly this (`effects/EnchantmentAttributeEffect.java:39-58`), so "modify
attributes on equip" needs no custom Java at all: it is `EnchantmentEffectComponents.ATTRIBUTES` (a
location-based effect applied whenever the slot matches, since attributes are meant to be always-on
while worn) or an `EnchantmentAttributeEffect` wrapped in `LOCATION_CHANGED` with a condition, the
same as `soul_speed.json`'s pattern.

**There is no generic "on use" (right-click) component.** The full effect-component list
(`EnchantmentEffectComponents.java:26-119`) covers damage, protection, knockback, post-attack,
piercing, ammo, projectiles, fishing, tridents, crossbows, mining/repair XP and location-changed/tick
— nothing fires from a plain item-use action. A consumer wanting "on use" behaviour has to bridge in
from a Fabric event (e.g. `UseItemCallback`) and read the enchantment level off the stack with
`EnchantmentHelper.getItemEnchantmentLevel`, entirely outside the effect-component system. This is a
real gap in what the data-driven system offers, not an oversight in this framework — flagged as an
open decision below rather than worked around, since a generic use-hook needs its own design.

**Enchanting a plain book bypasses the item-match check, but table availability is a separate tag.**
`EnchantmentHelper.selectEnchantment` treats `Items.BOOK` as satisfying `isPrimaryItem` unconditionally
(`EnchantmentHelper.java:599-600`), and `EnchantmentMenu.clickMenuButton` transmutes an enchanted book
in place (`EnchantmentMenu.java:165-166`) — so "an enchantment book" needs no separate item or recipe;
`minecraft:enchanted_book` already carries any enchantment via `stored_enchantments`. But the
candidate pool itself is gated by membership in the `#minecraft:in_enchanting_table` tag
(`EnchantmentMenu.java:200`, `EnchantmentTags.java:18`) — an enchantment with no tag membership never
appears at the table regardless of weight or supported items. "Register... so it's discoverable" is
therefore a tag-datagen requirement (S5), not an item-registration one.

**Exclusivity is the one field that needs a deferred `HolderLookup.Provider`, unlike item or entity
references.** `Enchantment.EnchantmentDefinition.supportedItems`/`primaryItems` are `HolderSet<Item>`,
and `Item` is an ordinary `BuiltInRegistries` entry fully populated at mod init — a consumer can build
these directly (`HolderSet.direct(...)` or a `TagKey<Item>` its own datagen already writes) with no
special plumbing, same as `ArmorSet.Builder.allowedWearers` does for entity types. `exclusiveWith`,
however, takes `HolderSet<Enchantment>` (`Enchantment.java:60`,`592-595`), and `Enchantment` is the
datapack registry this whole framework is populating — it does not exist yet at mod init, only at
datagen time via the `HolderLookup.Provider` a `FabricDynamicRegistryProvider` is handed
(`Enchantments.java:134` does the same thing with `context.lookup(Registries.ENCHANTMENT)` during its
own bootstrap). So `EnchantSpec`'s factory, not its constructor, is where exclusivity gets resolved.

**Fabric API ships the exact datagen base class this needs.** Decompiling
`net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider` out of
`fabric-data-generation-api-v1-25.5.0+aed122aa9e.jar` (the version bundled by this repo's
`fabric_version=0.157.0+26.2`) shows an abstract `configure(HolderLookup.Provider, Entries)` called
once per generation, with entries written to `Target.DATA_PACK` under a path derived from the target
registry's own resource path — `"enchantment"` for `Registries.ENCHANTMENT` — with the mod's own
namespace, since the registry's identifier namespace is `minecraft`
(`FabricDynamicRegistryProvider.java:61-77`, decompiled to
`/tmp/claude-1000/decompile/net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider/out/FabricDynamicRegistryProvider.java`).
This is a public, documented extension point — nothing here is a workaround the way armor's raw
`DataProvider`s over `EquipmentClientInfo.builder` were.

## S1 — Scratch consumer harness — **planned**

**Build.** A throwaway consumer mod, built the same way armor's S1 harness was: clone the smallest
existing mod (`rk-tweaks`, per the workspace's own "no scaffold" guidance) rather than reconstruct one
from scratch, give it the `rkcore`/`shade` consumer arrangement from `companions/build.gradle:38-66`
with an `rkcoreSharedJar` filter for `common/item/**` and `common/enchant/**`, and `stopServer` so
`scripts/devserver.sh` can drive it. Ports 25605 (game) and 25582 (rcon) are free — they were
`armor-harness`'s before that harness was folded into a real mod and removed; double check
`rkc/mods/*/run/server.properties` and the sibling `player-effect-framework` scratch copy before
booting, since both could pick a harness port around the same time. It declares one enchantment once
S2 and S3 exist: a weapon enchantment with a vanilla-vocabulary effect (e.g. an `ATTRIBUTES` damage
bump, to prove the plain path) and a second with a custom effect type registered through S3 (to prove
the hook actually fires).

**Done when.** `scripts/devserver.sh enchant-harness start` boots and
`scripts/devserver.sh enchant-harness rcon "list"` answers on the harness's own port.

## S2 — The enchantment declaration — **planned**

**Build.** `common/enchant/EnchantSpec`: a `ResourceKey<Enchantment>` and a
`Function<HolderLookup.Provider, Enchantment>` factory, per the second and seventh findings above — a
thin wrapper around calling `Enchantment.enchantment(...).build(...)` directly, not a re-implementation
of that builder. A static helper (`EnchantSpec.of(namespace, path, definition, effects...)` or similar)
should exist for the common case that needs no exclusivity, so most declarations never touch
`HolderLookup.Provider` at all; only a spec that calls `exclusiveWith` needs the full factory form.
Carry one more field the definition itself has no room for: whether the enchantment belongs in
`#minecraft:in_enchanting_table` (default `true` — see S5), since a loot-only or villager-only
enchantment is a legitimate thing to declare.

**Done when.** The harness compiles two `EnchantSpec`s — one plain, one with `exclusiveWith` against a
vanilla tag (`EnchantmentTags.DAMAGE_EXCLUSIVE`, say) — proving the deferred-resolution factory shape
actually type-checks against a real `HolderLookup.Provider` once S4 exists to supply one. Until S4
this stage has nothing to run, only to compile against.

## S3 — Custom effect-type registration — **planned**

**Build.** `common/enchant/EnchantEffectRegistration` (or similar), a thin wrapper over
`Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, id, codec)` and its
`..._VALUE_EFFECT_TYPE`/`..._LOCATION_BASED_EFFECT_TYPE` counterparts, called once from a consumer's
`onInitialize` — before any datapack reload, the same ordering constraint every other simple-registry
entry already has. Alongside it, a convenience for the common case a full record+codec is overkill
for: a stateless effect declared as a bare lambda (`SimpleEntityEffect`,
matching `EnchantmentEntityEffect.apply(ServerLevel, int, EnchantedItemInUse, Entity, Vec3)`'s
signature) wrapped in a `MapCodec.unit(...)`-style codec keyed by the same id it's registered under,
so a consumer writing "on hit, do X" never has to hand-write a record and a `RecordCodecBuilder` for
an effect with no configurable fields — only a genuinely parameterised effect (like `ApplyMobEffect`)
needs the full record-and-codec shape `effects/ApplyMobEffect.java` demonstrates.

**Done when.** The harness registers one custom `EnchantmentEntityEffect` through this facility and
one plain vanilla effect (`AddValue`, say) on two different declared enchantments, and both parse at
datapack load with no `Unknown enchantment effect type` warning in `scripts/devserver.sh
enchant-harness logs`. Nothing fires yet — that needs S4's JSON and S6's in-world test.

## S4 — Enchantment datapack datagen — **planned**

**Build.** `common/enchant/datagen/EnchantDatagenProvider extends FabricDynamicRegistryProvider`,
taking the harness's declared `List<EnchantSpec>` and, per spec, calling `entries.add(spec.key(),
spec.factory().apply(registries))` inside `configure` — the exact call on `Entries` needs pinning down
against the decompiled class's nested `Entries` type when this stage is actually built; the outer
shape (`FabricDynamicRegistryProvider.java:32-59`) is confirmed but that inner method signature is not
yet read and is flagged here rather than guessed.

**Done when.** `./gradlew generateAssets` (or the harness's datagen entrypoint, whatever it ends up
named) writes `data/enchantharness/enchantment/<path>.json` for both harness specs, matching the shape
of `sharpness.json`/`fire_aspect.json` field-for-field except for the mod's own namespace and content.
The exclusivity-bearing spec's JSON carries a resolved `exclusive_set` rather than an unresolved
reference — proof the deferred `HolderLookup.Provider` factory actually ran against real registry
data, not just compiled.

## S5 — Enchanting-table tag and lang — **planned**

**Build.** A tag provider over `net.minecraft.data.tags.EnchantmentTagsProvider` (the vanilla base
class; confirm during this stage whether Fabric API wraps it the way `FabricTagsProvider` wraps item
and block tags, or whether a consumer extends the vanilla class directly the way
`VanillaEnchantmentTagsProvider` does) adding every spec whose `inEnchantingTable` flag is set to
`#minecraft:in_enchanting_table`. Lang needs no separate provider: `Enchantment.Builder.build(Identifier)`
already sets the description to `Component.translatable(Util.makeDescriptionId("enchantment", id))`
(`Enchantment.java:650-653`), so a spec only needs its `enchantment.<ns>.<path>` key added wherever the
harness already writes lang — one line per spec, not a generalised provider the way armor's naming
needed one.

**Done when.** On the harness, both declared enchantments appear as candidates at an enchanting table
(bookshelves present) and their names are readable rather than showing the raw translation key.

## S6 — In-world proof of a custom effect — **planned**

**Build.** Nothing new — this stage is the in-world test that the rest of the framework was built
to pass. `/give` an enchanted book carrying the custom-effect enchantment, apply it via anvil or
`/enchant`, and trigger whatever the custom effect does (an on-hit effect needs a hit; a tick effect
needs equipping and waiting).

**Done when.** The custom effect is observed firing in-world — not just parsing without a datapack
warning, which S3 already confirmed. This is the stage that makes the feature complete: it is the
first point anything here proves a mod's own Java code runs off a declared enchantment, rather than
proving vanilla's existing vocabulary still works.

## Open decisions

**Whether "on use" gets a bridge at all.** The effect-component vocabulary has no right-click hook
(see Findings). If a consumer wants one, the shape is a Fabric event (`UseItemCallback` or similar)
reading `EnchantmentHelper.getItemEnchantmentLevel` against a `Holder<Enchantment>` — outside
`common/enchant` entirely, or a thin `common/enchant` helper that just wraps that lookup for
convenience. Not designed here; settled by the first consumer that actually wants on-use behaviour.

**Whether `EnchantSpec` should offer more than the bare factory form for exclusivity.** S2's plan
leaves the deferred-resolution case as "call `context.lookup(Registries.ENCHANTMENT)` yourself inside
the factory," matching what `Enchantments.bootstrap` does. Whether that is ergonomic enough once a
real consumer tries it, or whether it wants a small helper (e.g. accepting a `List<TagKey<Enchantment>>`
resolved for you) is unknown until S2 has a real caller past the harness.

**Anvil combining, curses and villager trades are untouched.** Anvil enchantment-combining and level
capping read `ItemEnchantments`/`Enchantment.getMaxLevel` generically and need nothing
enchantment-specific from this framework; whether a curse (no `IN_ENCHANTING_TABLE` membership, found
only via loot/trades) or a villager-trade-obtainable enchantment needs framework support beyond what
`EnchantSpec.inEnchantingTable = false` already allows is unverified, since nothing here has tried it.

**Coordination with `player-effect-framework`.** A sibling agent is building a separate framework in
the same rkcore codebase on another branch. The two are not designed to interact — an enchantment
effect and a status-effect framework are different subsystems — but if that framework also touches
`BuiltInRegistries` registration timing or `common/` package boundaries, the two branches should be
diffed against each other before either merges, not assumed compatible.

## Reference: effect components by hook shape

Read out of `EnchantmentEffectComponents.java:26-119` and grouped by what a consumer is actually
trying to do, since the vanilla names don't sort that way:

| What a consumer wants | Component(s) | Effect interface |
|---|---|---|
| Change an attribute while equipped | `ATTRIBUTES` | `EnchantmentAttributeEffect` (built in, no custom code) |
| React to being equipped/unequipped, or a per-tick location check | `LOCATION_CHANGED` | `EnchantmentLocationBasedEffect` |
| Run every tick while equipped | `TICK` | `EnchantmentEntityEffect` |
| React to landing a melee hit | `POST_ATTACK` | `EnchantmentEntityEffect`, targeted (attacker/victim) |
| React to a piercing (multi-hit) attack | `POST_PIERCING_ATTACK` | `EnchantmentEntityEffect` |
| React to a projectile hitting a block | `HIT_BLOCK` | `EnchantmentEntityEffect` |
| React to a projectile spawning | `PROJECTILE_SPAWNED` | `EnchantmentEntityEffect` |
| Modify a numeric outcome (damage, knockback, durability loss, XP, fishing luck, ...) | `DAMAGE`, `DAMAGE_PROTECTION`, `KNOCKBACK`, `ITEM_DAMAGE`, `BLOCK_EXPERIENCE`, `MOB_EXPERIENCE`, etc. | `EnchantmentValueEffect` |
| Grant outright immunity to a damage type | `DAMAGE_IMMUNITY` | fixed `DamageImmunity` type |
| Nothing data-driven exists for this | (right-click use) | bridge via a Fabric event instead |

## Reference: registries this framework touches

| Registry | Kind | When populated | Who writes to it |
|---|---|---|---|
| `Registries.ENCHANTMENT` | Datapack registry | Per-world, from datapack JSON | S4's datagen provider (JSON on disk, not a runtime call) |
| `BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE` / `..._VALUE_EFFECT_TYPE` / `..._LOCATION_BASED_EFFECT_TYPE` | Ordinary code registry | Mod init | S3's registration helper |
| `BuiltInRegistries.ITEM`, `BuiltInRegistries.ENTITY_TYPE` | Ordinary code registry | Mod init (already populated by the time a consumer declares a spec) | Not written by this framework — read to build `HolderSet`s for `supportedItems`/`primaryItems` |
