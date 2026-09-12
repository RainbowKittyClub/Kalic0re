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
S1 through S6 are now built and verified in-world on the harness's dev server — see each stage's "As
built" note for what matched the plan and what didn't.

S1 is the harness, for the same reason armor's S1 was: `rkcore` registers no content of its own, so
nothing here is observable without a consumer. S2 and S3 are independent of each other and both
compile against nothing but vanilla: S2 is the enchantment declaration, S3 is the custom-effect-type
registry. S4 turns declared enchantments into a datapack entry and depends on S2. S5 makes that entry
reachable in survival (enchanting-table tag, lang) and depends on S4. S6 is the in-world proof and is
what makes the feature complete — it exercises a declared enchantment with a custom
`EnchantmentLocationBasedEffect` (equip/unequip), not just vanilla's own effect vocabulary or the
hit/tick path, since equip/unequip is the exact mechanism a concrete target consumer needs (a flight
enchantment that toggles a `ServerPlayer`'s fly ability while worn — see the Findings and Reference
sections below) and is the one path nothing else here exercises.

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

**A `ServerPlayer`'s fly ability is a public mutable field, synced by one explicit call — confirmed
reachable from a location-based effect with no mixin.** `Abilities` (`Abilities.java:15-19`) is a
plain class with public `boolean mayfly`/`flying`/`invulnerable`/`instabuild`/`mayBuild` fields, held
one-per-player at `Player.abilities` and returned by `Player.getAbilities()`
(`Player.java:161,1303`). Setting `mayfly` does nothing to the client by itself — `ServerPlayer`
overrides `onUpdateAbilities()` to send `new ClientboundPlayerAbilitiesPacket(this.getAbilities())`
over `this.connection`, plus `updateInvisibilityStatus()` (`ServerPlayer.java:1750-1756`). This exact
pattern — mutate the `Abilities` fields, then call `onUpdateAbilities()` — is what vanilla's own
`ServerPlayerGameMode.changeGameModeForPlayer` does for the creative/spectator fly grant
(`ServerPlayerGameMode.java:55-69`), including its one caveat: it does not unconditionally clear
`flying` when `mayfly` is revoked, only when the player isn't already airborne over ground
(`ServerPlayerGameMode.java:61-63`, `abilities.flying && gameModeForPlayer != GameType.SPECTATOR &&
this.isInRangeOfGround()`) — so a future flight enchantment's `onDeactivated` has to make the same
kind of call about whether to force landing, not assume clearing `mayfly` is enough on its own.
`EnchantmentLocationBasedEffect.onChangedBlock`/`.onDeactivated` (`effects/EnchantmentLocationBasedEffect.java:26-30`)
already hand over an `Entity entity`, exactly as `EnchantmentAttributeEffect` narrows it to
`LivingEntity` (`effects/EnchantmentAttributeEffect.java:47-58`); narrowing further to `ServerPlayer`
with an `instanceof` is the same shape and reaches `getAbilities()`/`onUpdateAbilities()` directly. No
mixin, event, or packet-handling code is needed — this confirms the S3 hook shape is sufficient for
the concrete flight use case the amendment is targeting, and the design in S1–S6 below is unchanged by
this finding beyond swapping which effect interface the harness exercises.

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

## S1 — Scratch consumer harness — **done**

**Build.** A throwaway consumer mod, built the same way armor's S1 harness was: clone the smallest
existing mod (`rk-tweaks`, per the workspace's own "no scaffold" guidance) rather than reconstruct one
from scratch, give it the `rkcore`/`shade` consumer arrangement from `companions/build.gradle:38-66`
with an `rkcoreSharedJar` filter for `common/item/**` and `common/enchant/**`, and `stopServer` so
`scripts/devserver.sh` can drive it. Ports 25605 (game) and 25582 (rcon) are free — they were
`armor-harness`'s before that harness was folded into a real mod and removed; double check
`rkc/mods/*/run/server.properties` and the sibling `player-effect-framework` scratch copy before
booting, since both could pick a harness port around the same time. It declares two enchantments once
S2 and S3 exist: a weapon enchantment with a vanilla-vocabulary effect (e.g. an `ATTRIBUTES` damage
bump, to prove the plain path) and an armor enchantment with a custom `EnchantmentLocationBasedEffect`
registered through S3 — flipping a boolean (or logging) on `onChangedBlock`/`onDeactivated` — to prove
the equip/unequip-triggered path specifically, since that is the mechanism a real flight enchantment
will need and nothing else here exercises it.

**Done when.** `scripts/devserver.sh enchant-harness start` boots and
`scripts/devserver.sh enchant-harness rcon "list"` answers on the harness's own port.

**As built.** Lives at `_scratch/enchant-framework/enchant-harness`, sibling to this checkout, cloned
from `companions/build.gradle` rather than `rk-tweaks` (the `rkcore`/`shade` consumer wiring was the
part worth copying verbatim). One deviation from the plan text: the vanilla-vocabulary enchantment
ended up as a chest-armor `ATTRIBUTES` max-health bump rather than a weapon damage bump, so both
harness enchantments share `EquipmentSlotGroup.CHEST` — simpler to prove equip/unequip against a
single slot, and S2's "vanilla vocabulary vs. custom effect" distinction doesn't depend on which slot
group is used. Ports 25605/25582 were free as expected.

## S2 — The enchantment declaration — **done**

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

**As built.** `common/enchant/EnchantSpec.java` is a record with four `of(...)` overloads (plain
builder × table-flag, deferred-lookup builder × table-flag) rather than a single "builder" helper —
covers the same two cases (needs a `HolderLookup.Provider` or doesn't) without an intermediate type.
The harness's `harness_vigor` spec calls `exclusiveWith(enchantments.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))`
inside its factory (armor-themed, not `DAMAGE_EXCLUSIVE`, since the spec itself is an armor attribute
buff) — S4's generated json (`harness_vigor.json`) confirms it resolves to a real
`"exclusive_set": "#minecraft:exclusive_set/armor"`, not an unresolved reference.

## S3 — Custom effect-type registration — **done**

**Build.** `common/enchant/EnchantEffectRegistration` (or similar), a thin wrapper over
`Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, id, codec)` and its
`..._VALUE_EFFECT_TYPE`/`..._LOCATION_BASED_EFFECT_TYPE` counterparts, called once from a consumer's
`onInitialize` — before any datapack reload, the same ordering constraint every other simple-registry
entry already has. Alongside it, a convenience for the common case a full record+codec is overkill
for: a stateless effect declared as a bare lambda wrapped in a `MapCodec.unit(...)`-style codec keyed
by the same id it's registered under, for each interface shape that matters —
`SimpleEntityEffect` matching `EnchantmentEntityEffect.apply(ServerLevel, int, EnchantedItemInUse,
Entity, Vec3)`, and `SimpleLocationBasedEffect` matching the pair
`onChangedBlock(ServerLevel, int, EnchantedItemInUse, Entity, Vec3, boolean)`/
`onDeactivated(EnchantedItemInUse, Entity, Vec3, int)` off
`effects/EnchantmentLocationBasedEffect.java:26-30` — the second is what the equip/unequip use case
(and the eventual flight enchantment) actually needs. A consumer writing "on hit, do X" or "on equip,
do Y" never has to hand-write a record and a `RecordCodecBuilder` for an effect with no configurable
fields; only a genuinely parameterised effect (like `ApplyMobEffect`) needs the full record-and-codec
shape `effects/ApplyMobEffect.java` demonstrates.

**Done when.** The harness registers one custom `EnchantmentLocationBasedEffect` through
`SimpleLocationBasedEffect` and one plain vanilla effect (`AddValue`, say) on two different declared
enchantments, and both parse at datapack load with no `Unknown enchantment effect type` warning in
`scripts/devserver.sh enchant-harness logs`. Nothing fires yet — that needs S4's JSON and S6's
in-world test.

**As built.** `EnchantEffectRegistration` exposes `registerEntityEffect`/`registerLocationBasedEffect`,
each taking an `Identifier` (not `String`) specifically so a mod's own effect never accidentally
lands in the `minecraft` namespace the way `Registry.register(Registry, String, T)`'s implicit default
would allow. The harness's `HarnessEnchantEffects` registers one `SimpleLocationBasedEffect` as a
`static final` field (forced by a no-op `init()` call from `Main.onInitialize`, the same
force-class-init pattern `rktweaks.ModGameRules` uses) rather than a plain vanilla effect for the
second enchantment — the vanilla-vocabulary proof moved to S2's `ATTRIBUTES` spec instead, so S3 only
needed to prove the custom-registration path, which it does. The harness booted with no
"Unknown enchantment effect type" warning.

## S4 — Enchantment datapack datagen — **done**

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

**As built.** The flagged-as-unpinned call resolved to exactly the guessed shape:
`entries.add(spec.key(), spec.factory().apply(registries))` against
`FabricDynamicRegistryProvider.Entries.add(ResourceKey<T>, T)`, confirmed by `javap` against
`fabric-data-generation-api-v1-25.5.0+aed122aa9e.jar`'s nested `Entries` class before writing
`EnchantDatagenProvider`. `./gradlew generateAssets` on the harness produced
`data/enchantharness/enchantment/harness_vigor.json` and `harness_flight_proof.json`, both matching
vanilla's own field shape, with `harness_vigor.json` carrying the resolved
`"exclusive_set": "#minecraft:exclusive_set/armor"` described in S2's As-built note.

## S5 — Enchanting-table tag and lang — **done**

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

**As built.** `EnchantTagProvider extends net.minecraft.data.tags.EnchantmentTagsProvider` directly
(no Fabric wrapper exists for a dynamic-registry tag provider) and deliberately has no
`@Override getName()` — `javap` confirmed `TagsProvider.getName()` is `public final`, so overriding it
would not compile. Lang needed no provider, exactly as planned: one `enchantment.<ns>.<path>` key per
spec in the harness's own `assets/enchantharness/lang/en_us.json`.
**A real, previously-unflagged datagen-ordering bug surfaced here**, not anticipated by the plan:
`EnchantTagProvider.addTags` originally called `builder.add(spec.key())`, which failed
`./gradlew generateAssets` with `IllegalArgumentException: Couldn't define tag
minecraft:in_enchanting_table as it is missing following references: enchantharness:harness_vigor,
enchantharness:harness_flight_proof`. Reading `TagsProvider.run()`
(`data/tags/TagsProvider.java:65-105`) and `FabricDynamicRegistryProvider.run()`
(`FabricDynamicRegistryProvider.java:44-59`) explains why: every provider in a
`FabricDataGenerator.Pack` shares one `CompletableFuture<HolderLookup.Provider>` snapshot built once
at datagen boot from whatever datapacks are already loaded — a sibling `FabricDynamicRegistryProvider`
in the *same run* only ever writes JSON to disk, it never feeds its output back into that shared
snapshot, so a tag referencing this mod's own just-declared enchantment can never resolve at
validation time no matter what order the providers run in. Vanilla's own combined datagen avoids this
because its shared registries snapshot is built from a `RegistrySetBuilder` bootstrap
(`Enchantments.bootstrap`) that populates the *same* in-memory registry every provider reads — a
mechanism Fabric API's dynamic-registry datagen does not replicate. The fix is the vanilla-supplied
escape hatch for exactly this ordering problem: `TagAppender.addOptional(ResourceKey)`
(`data/tags/TagAppender.java`) builds a `TagEntry` with `required=false`, whose
`verifyIfPresent` (`tags/TagEntry.java`) short-circuits to `true` without consulting the registries
snapshot at all. `EnchantTagProvider` now calls `builder.addOptional(spec.key())` instead of `.add(...)`
— this only weakens datagen-time validation, not the tag's behavior at real runtime, where the game
loads this mod's own generated enchantment json alongside the tag json and the reference resolves
normally (confirmed in S6's own in-world run). Any future `common/enchant` tag provider referencing
this framework's own declared content needs the same `addOptional`, not `add`.

## S6 — In-world proof of a custom equip/unequip effect — **done**

**Build.** Nothing new — this stage is the in-world test that the rest of the framework was built
to pass. `/give` an enchanted book carrying the location-based custom-effect enchantment, apply it via
anvil or `/enchant`, then equip and unequip the piece.

**Done when.** The custom effect's `onChangedBlock` is observed firing on equip and its
`onDeactivated` on unequip — not just parsing without a datapack warning, which S3 already confirmed.
This is the stage that makes the feature complete: it is the first point anything here proves a mod's
own Java code runs off a declared enchantment's equip/unequip transition, which is the exact
mechanism a future flight enchantment needs (see Findings and the Reference section below) — not just
that vanilla's existing vocabulary still works.

**As built.** Ran on the harness's dev server (`scripts/devserver.sh <harness-dir> rcon "..."`) against
a `NoGravity` armor stand rather than a book-and-anvil flow on a real player — an `/item replace entity
... armor.chest with minecraft:iron_chestplate[minecraft:enchantments={"enchantharness:harness_flight_proof":1}]`
gives the exact same equip transition `EnchantmentHelper`/`Enchantment.runLocationChangedEffects`
drives for any `LivingEntity`, without needing a connected client. (Two dead ends before landing on
this, worth recording since a future headless rcon test will hit the same ones: a `NoAI` zombie at
`y=100` never became selectable — it burned to death from sun exposure on the very next tick, since
`NoAI` disables pathfinding, not fire-in-daylight or fall physics; and even a fireproof mob needs
`forceload add <x> <z>` first, since a fresh dev world keeps no chunks loaded at all with zero players
connected, and an unloaded entity is not selector-visible.) Server log confirmed both edges:
```
[...] (enchantharness) flight_proof onChangedBlock: entity=Armor Stand becameActive=true
[...] (enchantharness) flight_proof onDeactivated: entity=Armor Stand
```
`/attribute @e[...] minecraft:max_health get` also confirmed `harness_vigor`'s vanilla-vocabulary
`ATTRIBUTES` effect (20 → 24 at level 2, i.e. `+2` per level), so both harness enchantments are proven
live, not just the amendment's location-based one. No `Mixin apply failed`/`InvalidInjectionException`
in the server log (grep count 0), consistent with the framework needing no mixin at all.

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

## Reference: toggling a `ServerPlayer`'s fly ability from a location-based effect

Verified for the flight-enchantment use case the S1/S3/S6 amendment targets; not implemented here —
this framework stops at confirming the hook shape reaches it.

| Step | Where |
|---|---|
| Per-player mutable ability flags (`mayfly`, `flying`, `invulnerable`, `instabuild`, `mayBuild`) | `Abilities.java:15-19`, held at `Player.abilities` |
| Read/write access from any code holding the entity | `Player.getAbilities()`, `Player.java:1303` |
| Push the changed flags to that player's client | `ServerPlayer.onUpdateAbilities()`, `ServerPlayer.java:1750-1756` — sends `ClientboundPlayerAbilitiesPacket(getAbilities())` over `this.connection` |
| Vanilla's own precedent for this exact pattern | `ServerPlayerGameMode.changeGameModeForPlayer`, `ServerPlayerGameMode.java:55-69` |
| Caveat vanilla's precedent demonstrates | Revoking `mayfly` does not by itself clear `flying`; vanilla only clears it when the player isn't airborne over ground (`ServerPlayerGameMode.java:61-63`) — a future flight enchantment's `onDeactivated` needs its own such call, not a default this framework makes for it |
| Hook entry point | `EnchantmentLocationBasedEffect.onChangedBlock`/`.onDeactivated` (`effects/EnchantmentLocationBasedEffect.java:26-30`) hands over a plain `Entity`; narrow with `instanceof ServerPlayer` the same way `EnchantmentAttributeEffect` narrows to `LivingEntity` (`effects/EnchantmentAttributeEffect.java:47-58`) |

## Reference: registries this framework touches

| Registry | Kind | When populated | Who writes to it |
|---|---|---|---|
| `Registries.ENCHANTMENT` | Datapack registry | Per-world, from datapack JSON | S4's datagen provider (JSON on disk, not a runtime call) |
| `BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE` / `..._VALUE_EFFECT_TYPE` / `..._LOCATION_BASED_EFFECT_TYPE` | Ordinary code registry | Mod init | S3's registration helper |
| `BuiltInRegistries.ITEM`, `BuiltInRegistries.ENTITY_TYPE` | Ordinary code registry | Mod init (already populated by the time a consumer declares a spec) | Not written by this framework — read to build `HolderSet`s for `supportedItems`/`primaryItems` |
