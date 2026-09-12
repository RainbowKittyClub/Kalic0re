# Player-effect framework

A `common/effect` package in `rkcore`, the player-as-subject counterpart to `common/armor`: a
sibling mod declares a **player effect** — a set of hook points that fire on a `ServerPlayer` while
some condition holds — and gets the per-tick and per-event plumbing that keeps them firing without
writing its own dispatcher. Where `common/armor` turns a declaration into registered items and
Polymer wiring, `common/effect` turns a declaration into a running per-player state machine; the
item that grants the effect is still the consumer's own, built from `common/item` (and `common/armor`
where the grant is a wearable piece) exactly as today.

Every API claim below was checked against `libs/decompiledjava/minecraft-26.2` (the real 26.2
deobfuscated source) and against the Fabric API jar pinned in this checkout's
`gradle.properties` (`fabric_version=0.157.0+26.2`), decompiled with `scripts/decompile.sh` where the
static tree doesn't cover it — both cited by file and line in "Reference" below. Nothing here was
taken from memory of 1.21.x/Yarn shapes; two things that would have been wrong from memory are
called out in "Findings that reshape the work". Unverified: nothing in the source claims below, but
every stage's "Done when" is still unobserved, since this plan has not been implemented.

S1 is the harness, for the same reason `custom-armor-models.md`'s S1 was one: `rkcore` registers no
content of its own, so nothing here can be watched happen without a throwaway consumer. S2 (the
declaration types) is what every later stage compiles against. S3 (the per-tick dispatcher) is what
makes `onActivate`/`onTick`/`onDeactivate` real; S4 (damage/death) is independent of S3 and could be
built first, but is sequenced after it because both read the same per-player active-state map. S5
(the attribute-id helper) is optional polish and can be dropped without losing the feature. S6 is
consumer-wiring and docs. The feature is complete at S4: activation, ticking and the three combat
hooks all fire on a live player.

## Findings that reshape the work

**Vanilla already applies and removes an item's attribute modifiers on every equipment-slot change,
including both hands — so the framework carries no attribute machinery at all.**
`LivingEntity.collectEquipmentChanges` walks every `EquipmentSlot` each time it's called, and for any
slot whose stack changed, re-adds that stack's `ItemAttributeModifiers` as transient modifiers
(`LivingEntity.java:2939-2966`); the converse removal runs from `stopLocationBasedEffects`
(`LivingEntity.java:3838-3846`), called on unequip and on the item breaking. `EquipmentSlot.VALUES`
includes `MAINHAND` and `OFFHAND`, so a held item's modifiers are applied exactly the same way a worn
one's are — nothing here is player-specific or armor-specific either, it's `LivingEntity`. A
consumer that wants "while worn/held, +N to some attribute" declares it with ordinary
`Item.Properties.attributes(ItemAttributeModifiers)` targeting an `EquipmentSlotGroup`
(`EquipmentSlotGroup.java:14-25`, `ItemAttributeModifiers.java:36`) and gets it for free, the same way
vanilla's own speed-boosting boots do. `common/effect` does not touch `AttributeMap` anywhere; S5
only helps a consumer avoid a hand-rolled `AttributeModifier` id colliding with another effect's.

**No Fabric API event fires on an equipment change, and the one precise vanilla hook for it can't be
shared through this codebase's shading convention — so activation/deactivation is detected by a
per-tick scan, the same shape `common/label/HoverScanner` already uses.** Checked the three Fabric
API modules that could plausibly carry such an event — `fabric-entity-events-v1-5.0.5+06488ac19e`,
`fabric-item-api-v1-14.4.0+49a69a7b9e`, `fabric-events-interaction-v0-5.2.7+515ac5339e`, all bundled
inside `fabric-api-0.157.0+26.2.jar` at `META-INF/jars/`, matching this checkout's pinned
`fabric_version` — and none declares one; the closest matches are attack/use/block-break callbacks,
none of which fire on "a stack now sits in this slot". The vanilla hook that does is
`LivingEntity.onEquipItem(EquipmentSlot, ItemStack old, ItemStack new)`, public, called from every
`setItemSlot` (`LivingEntity.java:2274-2276`, the hook itself at `:689-713`) — a clean `@Inject`
target on its face. But a mixin can't be the mechanism here: `common/mixin` is the one package this
workspace's `CLAUDE.md` says must never be moved into a consumer's shaded jar, and a *new* mixin
package under `common/effect` would need each consumer to hand-register it in their own
`<modid>.mixins.json` against the post-relocation class name — an untested, per-consumer wiring step
that reintroduces exactly the "does this actually work once shaded" risk the armor framework's S8
spent a whole stage proving away for ordinary classes. A per-tick scan over
`EquipmentSlotGroup.slots()` (`EquipmentSlotGroup.java:68-70`) needs no mixin, no per-consumer config,
and is proven in this exact codebase already: `HoverScanner.onEndTick` does the equivalent
per-player, per-tick work today (`HoverScanner.java:53-57`). The cost is up to one tick of latency
recognizing a change, which nothing here needs to avoid.

**26.2's player class hierarchy gained a level that doesn't exist in 1.21.x/Yarn muscle memory.**
`ServerPlayer extends Player` (`ServerPlayer.java:209`), but `Player extends Avatar`
(`Player.java:127`), and only `Avatar extends LivingEntity` (`Avatar.java:13`) — there is no direct
`Player`-to-`LivingEntity` edge the way Yarn's `PlayerEntity` had. Nothing in this plan needs `Avatar`
by name; hooks are typed at `ServerPlayer` (the framework's whole subject) or `LivingEntity` (where a
vanilla or Fabric API signature already says so), and both resolve up through `Avatar` transparently.
Worth recording so a future reader doesn't go looking for a `PlayerEntity`-shaped ancestor that isn't
there.

**`MobEffect` is a legitimate heavier alternative this plan deliberately doesn't reach for.**
`net.minecraft.world.effect.MobEffect` is an ordinary registry entry
(`BuiltInRegistries.MOB_EFFECT`, `MobEffect.java:40`), supports `INFINITE_DURATION = -1`
(`MobEffectInstance.java:27`), and already gets a `applyEffectTick` call from `LivingEntity` for
anything holding an instance, plus a synced HUD icon and particle for free. It would suit a
consumer that wants a potion-shaped effect, at the cost of every effect looking like a potion
(HUD icon, `/effect` visibility, the vanilla milk-clears-you interaction) whether that fits or not.
This plan's hooks don't require it — a consumer that wants that presentation can give a matching
`MobEffectInstance` from inside its own `onActivate`, independent of whether `common/effect` exists.
Left as an open decision below rather than built now, since nothing here needs it to reach S4's
"feature complete".

## How these stages get tested

There's no consumer mod in this checkout — `rkcore` registers no content — so S1 builds one first, a
throwaway harness in the shape `custom-armor-models.md`'s `armor-harness` used: outside `rkc/mods/`,
companions'/planked-chests' `rkcore`/`shade` configuration and `rkcoreSharedJar` filter verbatim,
`stopServer` wired so `scripts/devserver.sh` drives its lifecycle. Every later stage is watched on
that harness's dev server via `scripts/devserver.sh <harness-dir> rcon "..."` and its logs — `/give`
the effect item, equip or hold it, hit something, take a hit, die, and read what the harness's own
log lines say each hook fired with.

Two traps to watch for, both learned from the armor framework's own build:

Dev mode never exercises the shade relocation — `runServer` runs a consumer's own unrelocated
classes off the compile classpath. Nothing about a mixin-free, event-only package makes this less
true; S6's "no mixin, no mod dependency" proof still needs a real installed jar with `rkcore` absent.

A silent one-tick gap between "the stack changed" and "the scan notices" is expected, not a bug —
see the second finding above. If a stage's "Done when" seems to lag by a tick, that is this
mechanism working as designed, not a regression to chase.

A stage is not finished until its "Done when" is observed. A failure at stage N implicates stage N,
not the ones before it.

## S1 — Scratch harness — **planned**

**Build.** A throwaway consumer mod, outside `rkc/mods/`, carrying `companions`' consumer
arrangement (`companions/build.gradle:33-66`) verbatim: the `rkcore`/`shade` configurations, the
`rkcoreSharedJar` filter (listing `common/item/**` and, once it exists, `common/effect/**` — no
`common/datagen` is needed unless the harness's own item wants generated art), and `stopServer` so
`scripts/devserver.sh` can drive it. Declares one item through `common/item/ItemRegistration` — a
plain `PolymerModelItem` boot or trinket, no datagen required if it borrows a vanilla texture — with
an `ItemAttributeModifiers` component targeting `EquipmentSlotGroup.FEET` or `.MAINHAND`, so S2's
declaration has a real item to point its `PlayerEffectSource` at before any framework code exists.

**Done when.** `scripts/devserver.sh <harness-dir> start` boots, `rcon "list"` answers, and `/give`
of the declared item shows the attribute bump in its tooltip and in
`/attribute <player> <attribute> base_value get` once worn — proving the item side works before
`common/effect` is asked to do anything.

## S2 — The declaration types — **planned**

**Build.** `common/effect/PlayerEffect`, a record carrying `namespace`, `name`, a `PlayerEffectSource`
and a `PlayerEffectHooks`, validated in its compact constructor the way `ArmorSet` validates
(`ArmorSet.java:49-60`): blank namespace or name rejected there rather than at registration.
`PlayerEffectSource` is a functional interface, `boolean isActive(ServerPlayer player)`, with static
factories: `equipped(EquipmentSlotGroup slot, Predicate<ItemStack> matches)` (iterating
`slot.slots()` and testing `player.getItemBySlot(...)` against the predicate — both public,
`LivingEntity.java:2270`, `EquipmentSlotGroup.java:68-70`), a convenience `equipped(EquipmentSlotGroup,
Item)` overload, `held(Predicate<ItemStack>)` as `equipped(EquipmentSlotGroup.HAND, ...)` under the
hood, and `custom(Predicate<ServerPlayer>)` as the escape hatch for a condition the framework
doesn't anticipate (a curio slot from a third-party API, a game-rule gate, a timed grant a consumer
tracks itself). `PlayerEffectHooks` is an interface of default no-op methods —
`onActivate`/`onDeactivate`/`onTick(ServerPlayer)`,
`onDamageTaken(ServerPlayer, DamageSource, float amount)`,
`onDamageDealt(ServerPlayer, LivingEntity target, DamageSource, float amount)`,
`onDeath(ServerPlayer, DamageSource)` — mirroring `Consumer<Equippable.Builder> equippableExtras`'s
"override only what changes" shape (`ArmorSet.java:150`) rather than forcing every consumer to
implement every hook.

**Done when.** The harness declares a `PlayerEffect` over S1's item and its `onInitialize` logs it;
constructing one with a blank `name` fails boot with the record's own message, visible in
`scripts/devserver.sh <harness-dir> logs`.

## S3 — The per-tick dispatcher — **planned**

**Build.** `common/effect/PlayerEffectDispatcher`, shaped directly on `HoverScanner`
(`HoverScanner.java:29-57`): a static `List<PlayerEffect>` from `register`, a static
`Map<UUID, Set<PlayerEffect>>` of what's currently active per player, and `init()` wiring
`ServerTickEvents.END_SERVER_TICK` to walk online players and, per effect, compare
`source.isActive(player)` against last tick's membership — firing `onActivate` then `onTick` on the
tick a source turns active, `onTick` alone on every following tick, and `onDeactivate` the tick it
turns inactive. `ServerPlayerEvents.LEAVE` (already proven in `HoverScanner.java:40,145-150`) fires
`onDeactivate` for anything still active and drops the player's map entry, so a consumer's cleanup
isn't skipped by a disconnect mid-effect.

**Done when.** On the harness, equipping S1's item logs exactly one `onActivate` and then one
`onTick` per tick while worn; unequipping logs exactly one `onDeactivate`; disconnecting while still
wearing it also logs `onDeactivate`, observed over `scripts/devserver.sh <harness-dir> logs -f` across
a join/equip/unequip/leave cycle.

## S4 — Damage and death hooks — **planned**

**Build.** In `PlayerEffectDispatcher.init()`, register against
`ServerLivingEntityEvents.AFTER_DAMAGE` — signature
`afterDamage(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
boolean blocked)`, confirmed by decompiling `fabric-entity-events-v1-5.0.5+06488ac19e.jar`'s
`ServerLivingEntityEvents` (see Reference). When `entity` is a `ServerPlayer` with active effects,
call each one's `onDamageTaken` with `source` and `damageTaken`. Separately, when
`source.getEntity()` (`DamageSource.java:63`, the *credited* attacker — resolves through a projectile
to its shooter, `DamageSource.java:16-18` distinguishes it from `getDirectEntity()`) is a
`ServerPlayer` with active effects, call each one's `onDamageDealt` with `entity` as the target.
Register against `ServerLivingEntityEvents.AFTER_DEATH` — `afterDeath(LivingEntity entity,
DamageSource source)` — and call `onDeath` when `entity` is a `ServerPlayer` with active effects at
the moment of death.

**Done when.** On the harness, wearing S1's item and taking a hit from a zombie logs `onDamageTaken`
once per hit; hitting the zombie back (melee) and shooting it (bow, to confirm `getEntity()` resolves
through the arrow to the player) both log `onDamageDealt` once per hit; dying while wearing it logs
`onDeath` exactly once.

## S5 — Attribute-modifier id helper — **planned**

**Build.** A small `common/effect/PlayerEffectAttributes` (or a static method on `PlayerEffect`
itself) deriving a stable `Identifier` for an `AttributeModifier`'s required id
(`AttributeModifier.java:14`, `net.minecraft.world.entity.ai.attributes.AttributeModifier` — not the
unrelated `net.minecraft.world.attribute.modifier.AttributeModifier` interface used for environment
blending, a false lead ruled out this session, see Reference) from an effect's own namespace/name
plus the attribute — `<namespace>:effect/<name>/<attribute path>` — the same reasoning
`ArmorSet.assetKey()` gives for one owned asset id rather than ad hoc strings
(`ArmorSet.java:72-75`). Purely a naming convenience: nothing stops a consumer writing the id by
hand, but two effects that both bump `minecraft:generic.movement_speed` with the same hand-picked id
would silently overwrite each other's modifier.

**Done when.** The harness's item builds its `ItemAttributeModifiers` through the helper, and
`/attribute <player> minecraft:generic.movement_speed base_value get` reflects the boost while worn
and reverts on unequip — proving the derived id round-trips through vanilla's own add/remove path
from the first finding, not a new one.

## S6 — Consumer wiring and docs — **planned**

**Build.** Confirm `common/effect` needs no sibling `common/<pkg>` shaded alongside it — unlike
`common/armor`, it registers no items and writes no assets, so a consumer's `rkcoreSharedJar` filter
needs only `include 'club/rainbowkitty/rkcore/common/effect/**'` plus whatever it already has for its
own item (`common/item`, and `common/datagen`/`common/armor` if the grant is a wearable piece).
Install the harness's shaded jar on a scratch server with `rkcore` absent, since dev mode never
exercises the relocation (see "How these stages get tested"). This scratch checkout's own
`docs/plan/custom-armor-models.md`-style documentation trail lives here; the workspace-root
`CLAUDE.md` row for `rkcore` and its "Sharing code between mods" section are outside this checkout
and are the real implementation's job to update once this plan lands in the actual `rkcore` checkout,
not this scratch copy's.

**Done when.** `unzip -l <harness>-<version>.jar | grep shaded/rkcore/common/effect` lists the
relocated classes, and that jar alone, on a server with no `rkcore` installed, still fires
activate/tick/deactivate/damage/death exactly as S3 and S4 observed them.

## Open decisions

**Whether a timed/consumable effect (drink this, effect lasts 30s) needs a dedicated mechanism, or
whether `PlayerEffectSource.custom(...)` is expressive enough via a consumer-owned expiry map.** The
generic escape hatch can express it today — a consumer stores its own `Map<UUID, Long>` expiry and
hands `custom(player -> expiry.getOrDefault(...) > gameTime)` to `PlayerEffect` — so this plan
doesn't build a second source kind for it. Settled by the first real consumer that wants one; if it
turns out every consumer wants the same expiry-map shape, that's the extraction, not a new
`PlayerEffectSource` variant.

**Whether `common/effect` should offer a `MobEffect`-backed presentation (HUD icon, particle, native
sync) as an alternative or addition to the hook-only model.** Deliberately not built now — see the
fourth finding. A consumer that wants potion-shaped presentation can already give a matching
`MobEffectInstance` from `onActivate` without any framework support for it; whether that's common
enough to deserve a helper is unknown until a consumer tries it.

**Whether `PlayerEffectSource` needs an entity-type restriction the way `ArmorSet.allowedWearers`
has one.** Not built here, since every hook in `PlayerEffectHooks` is typed at `ServerPlayer`
specifically — a non-player wearer is `common/armor`'s concern (it already has `allowedWearers`), not
this framework's. Revisit only if a hook ever needs to fire for a non-player subject.

**Whether the per-tick `O(players × effects)` scan needs coalescing as the effect count grows.**
`HoverScanner` runs the equivalent shape today (`O(players × providers)`) with no measured cost at
this server's population; revisit only if profiling says otherwise, not preemptively.

**Whether `common/effect` and the (separately developed, unseen from this checkout) enchant
framework should eventually share one hook-dispatch base.** Both frameworks fire a declared set of
callbacks against per-instance state — an item instance's enchantments there, a player's active
effects here — which suggests a shared "register callbacks, dispatch on a tick/event" trait could
serve both. Left an open question rather than a guess: this checkout has no visibility into the
enchant framework's actual shape, and building toward an imagined one risks designing against a
shape that doesn't match what actually landed. Worth a conversation between whoever finishes each
framework, not a unilateral abstraction from this side.

## Reference: verified this session

| Claim | How checked |
| --- | --- |
| `LivingEntity.collectEquipmentChanges`/`stopLocationBasedEffects` apply and remove an item's `ItemAttributeModifiers` on any `EquipmentSlot` change, hands included | Read `LivingEntity.java:2936-2973,3838-3846` in `libs/decompiledjava/minecraft-26.2` |
| `EquipmentSlotGroup.slots()`/`.test()` give the concrete `EquipmentSlot`s a group covers | Read `EquipmentSlotGroup.java:14-70` |
| `LivingEntity.onEquipItem` is public and fires from every `setItemSlot`, hands included | Read `LivingEntity.java:689-713,2274-2276` |
| `common/mixin` is excluded from every consumer's `rkcoreSharedJar` filter today | Read `companions/build.gradle:38-46`, `planked-chests/build.gradle` equivalent, and the workspace `CLAUDE.md`'s "Sharing code between mods" |
| No equipment-change event exists in Fabric API 0.157.0+26.2 | `unzip -l` on `fabric-api-0.157.0+26.2.jar`'s `META-INF/jars/{fabric-entity-events-v1-5.0.5+06488ac19e,fabric-item-api-v1-14.4.0+49a69a7b9e,fabric-events-interaction-v0-5.2.7+515ac5339e}.jar`, cross-checked with `javap` against each extracted jar |
| `ServerLivingEntityEvents`'s four callback shapes and exact parameter names | `scripts/decompile.sh net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents <extracted fabric-entity-events-v1 jar>` |
| `ServerPlayer extends Player extends Avatar extends LivingEntity` | Read `ServerPlayer.java:209`, `Player.java:127`, `Avatar.java:13` |
| `DamageSource.getEntity()` returns the credited attacker (resolves through a projectile), distinct from `getDirectEntity()` | Read `DamageSource.java:16-18,58-64` |
| The real `AttributeModifier` is `net.minecraft.world.entity.ai.attributes.AttributeModifier` (record: id, amount, operation); `net.minecraft.world.attribute.modifier.AttributeModifier` is an unrelated interface for environment/render value blending | Read both files; confirmed the first is what `LivingEntity`/`AttributeMap`/`ArmorMaterial` actually use via `grep` across `libs/decompiledjava/minecraft-26.2` |
| `MobEffect` is a `BuiltInRegistries.MOB_EFFECT` entry with a protected constructor (subclassable) and `MobEffectInstance.INFINITE_DURATION = -1` | Read `MobEffect.java:39-58`, `MobEffectInstance.java:27` |
| `HoverScanner`'s per-tick, per-player shape is what `PlayerEffectDispatcher` mirrors | Read `common/label/HoverScanner.java:29-150` in this checkout |
