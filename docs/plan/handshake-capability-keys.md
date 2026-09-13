# Handshake capability keys

Extends `PolymerClientHandshake` from a single per-connection "this client runs my mod too" flag into
that flag plus a set of named capability keys, so a client-optional Polymer mod can decide *per piece
of content* whether a given connection is able to decode it. It exists to fix a decode-failure
regression in `planked-chests`, whose registered chest set varies with which wood-adding mods an
installation happens to have (`planked-chests/.../block/ChestBlocks.java:95-99`) — a divergence that
nothing on the wire currently detects.

Everything below about Polymer's networking, Fabric's registry sync and vanilla's chunk handling was
read out of `libs/polymer` (branch `dev/26.2`, matching live's `polymer-bundled-0.17.5+26.2.jar`),
the `fabric-registry-sync-v0-7.1.0` nested jar inside `fabric-api-0.159.0+26.2.jar`, and
`libs/decompiledjava/minecraft-26.2`, and is cited by file and line. It was then re-verified by an
independent review pass against the same sources, which corrected four substantive claims; those
corrections are folded in below rather than appended. None of it has been run: every stage is
`planned`.

S2 is the harness — it builds the asymmetric two-install setup and captures the pre-fix baseline, and
every later stage's "Done when" runs on it, so build it first or first-ish. S1 is foundational for
the code: nothing else compiles without it. S3 depends on S1 and unblocks S4 and S5, which are
independent of each other. S4 is the last stage that changes behaviour; the feature is not finished
until S4's observation, and S6 is the release tail.

Two cheaper alternatives were considered and rejected. Dropping the `isModLoaded` guard and
registering all 27 woods unconditionally everywhere closes the bug for far less work, but puts
uncraftable content in every server's registries. Keeping the guard on dedicated servers while
registering unconditionally on clients — raised during review, and enough to delete S1, S3, S4 and
S5 — was rejected because it moves the invariant into an environment check rather than putting it on
the wire, and leaves a client's creative tab listing woods its server never registered.

## Findings that reshape the work

**Nothing detects a `plankedchests` content mismatch today, which is why the symptom is a decode
failure rather than Fabric's readable "server requires…" screen.** Polymer marks every
`PolymerObject` as a registry "server entry" as it is registered
(`polymer-core/.../mixin/other/MappedRegistryMixin.java:39-47`), and `polymer-reg-sync-manipulator`
wraps `Registry.getKey` inside Fabric's `createAndPopulateRegistryMap` to return `null` for those
entries (`.../rsm/mixin/fabric/fabricSync_RegistrySyncManagerMixin.java:31-37`), so every
`plankedchests:*` block and item is omitted from the id map the server sends and
`ClientRegistrySyncHandler.checkRemoteRemap` never sees the gap. Those ids are resolved instead
through Polymer's own client tables, identifier-keyed
(`polymer-core/.../impl/client/InternalClientRegistry.java:176-192`); when the client never
registered the identifier the decoder returns null and the lookup falls through to
`instance.byIdOrThrow(i)` (`:193-201`), which throws. This is why the fix has to be an explicit
negotiation and cannot be left to the platform. Note that the whole chain hangs off a `@Pseudo`
mixin declared `require = 0` (`fabricSync_RegistrySyncManagerMixin.java:22,31`): if Fabric's internals
drift, the mixin silently stops applying, `plankedchests:*` lands back in the sync map, and the
symptom changes from a decode failure to a hard registry-sync kick. Confirmed applying against
`fabric-registry-sync-v0-7.1.0` this session.

**All four supported wood mods are Polymer-marked on the live server, so any of them can trigger the
bug.** Enderscape is marked by `enderscape-polymer-patch-3.0.1.1+26.2.jar`, and Terrestria,
Cinderscapes and Woods and Mires are marked by `polymer-patch-bundle-1.1.7+26.2.jar`, whose
`fabric.mod.json` description reads "Polymer Patch for blockus, terrestria, cinderscapes, traverse
and woods_and_mires" and whose classes carry `registerOverlay` calls plus references to each of those
namespaces. Scanning the content jars alone is misleading — `WoodsAndMires-2.1.3-rkc+26.2.jar`,
`terrestria-8.1.0-alpha.1.jar`, `cinderscapes-6.1.0-alpha.2.jar` and
`enderscape-fabric-3.0.2+mc26.2.jar` each contain zero `eu/pb4/polymer` references; the marking is
external. The practical consequence is that a client on live missing *any* of the four gets past
Fabric's registry sync and then fails on the 15 modded woods, which is the ordinary player
configuration rather than an edge case. Both patch jars are staged in
`rkc/mods/planked-chests/run/mods/disabled/`, and whichever wood mod S2 uses, its patch has to be
installed on the server side alongside it or the asymmetry produces a registry-sync kick instead.

**The capability set has to ride the handshake's own version map rather than Polymer's metadata
channel.** Polymer's `MetadataPayload` looks like the natural fit — `setClientMetadata` on the client
(`PolymerClientNetworking.java:59-65`), `getMetadata` on the server
(`PolymerServerNetworking.java:41-49`) — but the client only sends it from `handleHandshake`
(`impl/client/ClientPacketRegistry.java:110-124`), so it needs a full extra round trip after the
server's handshake reply, and nothing orders it against the login barrier. The support map does not
have that exposure, for a reason worth stating precisely: it is populated inside the same deferred
`server.execute` block as the handshake reply (`ServerPacketRegistry.java:92-94`), and the join
continuation runs through `server.execute` as well (`EarlyConfigurationNetworkHandler.java:92-101`),
so FIFO ordering puts the map in place before configuration, registry sync and play. The barrier is
that keepalive continuation, not the pong at `PolymerHandshakeHandlerImplLogin.java:131-138`, which
only triggers it. Meanwhile Ping#2 leaves on the Netty thread as soon as Pong#1 arrives (`:134`), so
Pong#2 and the continuation can all be queued before a metadata reply lands. The race is real.

**The block-entity sync hook cannot see which block it is deciding for, but the cost of that is
nothing.** Polymer filters chunk block entities through
`PolymerSyncedObject.getPolymerReplacement(BlockEntityType, PacketContext)`
(`PolymerSyncedObject.java:21`), called from
`polymer-core/.../mixin/block/packet/ClientboundLevelChunkPacketDataMixin.java:18-27` with only the
type and the connection — never a position or a block state. Both of `planked-chests`' families share
one type each (`ChestBlocks.java:115-123`), so that decision stays connection-wide even after the
block state decision becomes per-wood. The consequence was expected to be client-side log spam; it is
not. A block entity arriving for a barrier is dropped in silence: chunk load goes
`replaceWithPacketData` → `getBlockEntity(pos, IMMEDIATE)` → `createBlockEntity`
(`libs/decompiledjava/minecraft-26.2/.../LevelChunk.java:363-366`), which returns `null` when
`!state.hasBlockEntity()`, and the tag is then ignored — neither `setBlockEntity` (`:426-437`) nor
`promotePendingBlockEntity` (`:610-628`) is reached, so neither warning fires. The residual cost is
about five bytes per fallback chest per chunk load
(`ClientboundLevelChunkPacketData$BlockEntityInfo.create`, `:153-157`). S5 is therefore a comment,
not a measurement.

## How these stages get tested

Compilation and style are local: `./gradlew build` in each mod, and
`cd rkc/lint && ./gradlew checkstyleTarget -PtargetDir=../mods/<mod>/src/main/java` for the two mods
here, neither of which is in `lintAll`'s wired-in map. After any edit under `kalic0re`'s `common/net`,
`cd rkc/mods/kalic0re && ./gradlew publishToMavenLocal` has to run before `planked-chests` rebuilds, or
it compiles against a stale copy.

Behaviour needs the S2 harness: one dev server and one dev client with deliberately different mod
sets, which is the only way to exercise a negotiation whose entire job is to notice a difference.
That is not the default arrangement — `run/client/mods` is a **symlink to `run/mods`**, recreated
before every `runClient` by `ensureClientModsSymlink` (`gradle/devserver.gradle:226-243`, wired at
`:254-258` and `:262`), precisely so the two installs normally match. S2 has to break it deliberately;
the guard leaves a non-empty real directory alone with a warning (`:232-239`), which is the seam.

The client opens a real window — this machine has a desktop session on `DISPLAY=:0` and no Xvfb, so
every in-world "Done when" below is a manual observation, not something a script can assert.

Dev mode runs against unrelocated classes off the compile classpath, so it never exercises Shadow's
relocation of `club.rainbowkitty.kalic0re.common`; only S6's real jar install does.

A stage is not finished until its "Done when" is observed, so a failure at stage N implicates N and
not the stages before it.

## S1 — Capability keys in `PolymerClientHandshake` — **done**

**Build.** The class today builds one `CustomPacketPayload.Type` from
`PolymerNetworking.id(modId, "handshake")`, registers it as a never-sent clientbound versioned packet
(`PolymerClientHandshake.java:26-39`), and reads the negotiated version back off the connection to
answer `has` (`:42-50`). Add a keyed form beside it, same trick: `register(String key)` registering
`<modId>:handshake/<key>`, and `supports(ServerGamePacketListenerImpl, String key)` plus
`supports(PacketContext, String key)` answering true only when both the base marker and that key
negotiated a version `>= 0`. Hold the per-key `Type` in a `Map<String, Type<Payload>>` on the
instance so an id is built once and `supports` is a map lookup plus the existing version read.

Both `register` overloads are called from `onInitialize`, which runs on both sides, and a side
registers only the keys it actually has content for — that asymmetry is the entire signal, so the
Javadoc has to say it outright.

Two behaviours need pinning down rather than falling out: `supports` must return false for a null
`PacketContext`, mirroring `has` (`:47-50`), and must return false rather than throw for a key this
side never registered.

Two constraints belong in the Javadoc as well. The payload id is built from the runtime mod id
string, so Shadow relocating `club.rainbowkitty.kalic0re.common` into
`club.rainbowkitty.<mod>.shaded.kalic0re` (`planked-chests/build.gradle:86`) does not change what goes
on the wire. And each key costs one entry in `HandshakePayload`'s `Map<Identifier, int[]>`
(`polymer-networking/.../impl/packets/HandshakePayload.java:13-24`), which the client sends inside a
`ServerboundCustomPayloadPacket` whose `MAX_PAYLOAD_SIZE` is 32767 bytes — 28 keys is roughly 1 KB
alongside Polymer's own ids, so this is comfortable here and wrong for thousands.

**Done when.** With both sides carrying the same mods, a temporary log on join reports `supports()`
true for every registered key and false for an invented one, and `has()` still true — i.e. the keyed
form agrees with the flag it extends rather than silently returning false everywhere. Build and
`checkstyleTarget` pass as a precondition, not as the criterion.

### As built

Built as planned. `keyTypes` is a `ConcurrentHashMap` rather than a plain map: it is written only
from `onInitialize` but read from network threads, and there is no "registration has finished" hook
to hand a frozen copy off at. `supports` is a strict refinement of `has` — it checks the base marker
too, which is redundant in practice (a client that declared a key must be running the mod) but makes
the two methods one mental model instead of two.

The "Done when" as written was not observable from `kalic0re` alone: it needs a consumer to register a
key, which is S3. A throwaway probe stood in — a temporary `HANDSHAKE.register("oak")` plus a
`ServerPlayConnectionEvents.JOIN` log in `PlankedChests.onInitialize` — and was removed afterwards
(`git diff` over `planked-chests/src` is empty). Anyone re-running this stage needs the same
scaffold; that is a flaw in the criterion, not in the code.

Observed on a dev client joining the dev server at `localhost:25604` in offline mode:
`S1 probe: has=true supports(oak)=true supports(unregistered)=false`.

One thing the plan only argued for and this stage actually saw: the probe logged on the **login**
tick, between `Player417 … logged in` and `Player417 joined the game`, and after Polymer's
`Full sync for Player417 took 51.96 ms`. That is direct evidence for the Findings claim that the
support map is populated before any play content is built, which until now rested only on reading
`server.execute` FIFO ordering. It does not yet prove the same for the *first chunk* specifically —
S4's observation is what closes that.

## S2 — Asymmetric dev harness and pre-fix baseline — **done**

**Build.** No mod code, but real setup. Break the `run/client/mods` symlink into a populated real
directory: `rm run/client/mods && mkdir run/client/mods` then copy every jar from `run/mods` into it
*except* the wood mod under test and its Polymer patch. `ensureClientModsSymlink` will leave that
directory alone because it is non-empty (`gradle/devserver.gradle:232-239`); note that `rm -rf
run/client` silently restores symmetry, so a confusing "it works now" result means checking the
symlink first.

Pick the wood mod from `run/mods/disabled/`, server side only, together with the patch that
Polymer-marks it — `polymer-patch-bundle-1.1.7+26.2.jar` for Woods and Mires, Terrestria or
Cinderscapes; `enderscape-polymer-patch-3.0.1.1+26.2.jar` for Enderscape, which additionally needs
`lithostitched`, `TrimPatcher` and `yet_another_config_lib_v3`, all already in `run/mods`. Boot on
the current, unmodified `planked-chests`, place one of that mod's chests somewhere reachable, then
join with `./gradlew runClient`.

Expect one of two symptom shapes, because they arrive by different paths. A chunk carrying the chest
fails on the client main thread: palette reads are redirected to Polymer's decoder
(`polymer-core/.../mixin/block/packet/BlockPaletteMixin.java:29-33`) and surface as an
`IllegalArgumentException` while the chunk is applied, not as a Netty error. A codec-level packet —
block update, section update, entity data, or the block event that drives the chest lid
(`ClientboundBlockEventPacketMixin`) — fails inside `ByteBufCodecs.idMapper` and surfaces as
`io.netty.handler.codec.DecoderException: Failed to decode packet '…'`. The block event is the
likeliest live trigger for the reported intermittency, so open the chest as well as standing near it.

Item stacks are *not* a trigger by default: `InternalClientRegistry.decodeItem` (`:204-217`) hands
back a virtual item rather than null when `USE_UNSAFE_ITEMS_CLIENT` is set, which it is
(`impl/client/ClientConfig.java:13` → `PolymerImpl.java:53`).

**Done when.** The failure reproduces and both its shape and the packet or path it names are recorded
in this stage's `As built`. A vanilla-wood chest placed alongside it should render correctly up until
the failing packet arrives; note whether it does.

### As built

Harness built with Woods and Mires, the mod originally reported: `run/client/mods` replaced with a
real directory holding the six shared jars, `WoodsAndMires-2.1.3-rkc+26.2.jar` and
`polymer-patch-bundle-1.1.7+26.2.jar` added to `run/mods` only. Three blocks placed near spawn for
this and later stages — `plankedchests:pine_chest` at `8 -53 -5`, `plankedchests:oak_chest` at
`10 -53 -5`, `woods_and_mires:pine_planks` at `6 -53 -5`.

**Neither predicted symptom occurred, because the client never reaches world content.** It
disconnects during login, before spawning:

```
[Netty Epoll IO #0/ERROR] (Polymer) Failed to parse 'plankedchests:oak_chest' item! Invalid stack data!
[Render thread/WARN] Client disconnected with reason: Internal Exception:
io.netty.handler.codec.DecoderException: Failed to decode packet 'clientbound/minecraft:custom_payload'
```

Two things about that are worth more than the stack trace. The failing packet is **Polymer's own
item sync**, not a chunk, block update or block event — `PolymerServerProtocol.sendSyncPackets:120`
sending `S2CPackets.SYNC_ITEM_ID` over every server-side `ITEM` entry, with the throw at
`impl/networking/entry/PolymerItemEntry.java:22-30`. And it names **`oak_chest`, a wood both sides
have**, not the withheld pine — so the visible casualty of a mismatch is not the missing content, it
is whatever entry the misalignment happens to land on. Anyone debugging this from the client log
alone would chase the wrong wood.

That also settles the S2 build text's expectation about Fabric's registry sync: the client was not
rejected, which confirms `polymer-patch-bundle` really does mark Woods and Mires' content as
server-side, and confirms the corrected second Finding against the original claim that only
Enderscape could do this.

Why the fix should still work, traced afterwards: `PolymerItemEntry.of` (`:18-20`) puts
`item.getDefaultInstance()` in each entry, and every `ItemStack` encode carrying a `PacketContext`
is rerouted through `PolymerItemUtils.getPolymerItemStack` by
`polymer-core/.../mixin/item/packet/ItemStackPacketCodecMixin.java:23-29`, which calls
`getPolymerItem(stack, context)`. So this packet *is* governed by `PolymerModelBlockItem`'s
`useRealItem` predicate, and S4 narrowing that predicate per wood reaches it. It is also why an
ordinary Polymer client without `planked-chests` is unaffected: `has(context)` is false, every
representation encodes as its vanilla fallback, and nothing needs resolving.

**Flagged for S4.** Each entry's representation is its own item, so at decode time that entry is not
yet in `InternalClientRegistry.ITEMS` and resolution falls back to the client's own registry by raw
id — which only works when server and client agree on that id. For a wood the client *does* have,
that holds only if the polymer tail base and ordering match on both sides, which the withheld mod's
own server-marked items may perturb. S4's observation is what settles whether per-wood negotiation
is sufficient here or whether the supported woods trip the same entry-ordering problem.

## S3 — `planked-chests` registers one key per wood — **planned**

**Build.** `PlankedChests.onInitialize` (`PlankedChests.java:27-34`) already calls
`HANDSHAKE.register()` before `ChestBlocks.init()`. After `init()` returns, register one key per wood
that actually got registered — `ChestBlocks.chests().keySet()` is exactly that set
(`ChestBlocks.java:109`) — keyed by `WoodType.id()`.

Leave the `isModLoaded` guard at `ChestBlocks.java:95-99` and the `DATAGEN` escape at `:52-58` alone.
The guard is not the defect; the defect is that its answer was never communicated. From this stage on
it is the thing being advertised.

**Done when.** With a temporary `LOGGER.info` in `onInitialize` listing the registered keys,
`scripts/devserver.sh rkc/mods/planked-chests logs` shows 12 keys with no wood mod in `run/mods` and
the right larger count with S2's wood mod present — 13 for Woods and Mires (1 wood), 14 for
Cinderscapes (2), 15 for Enderscape (3), 21 for Terrestria (9). Remove the log line before S6.

## S4 — Per-wood block state, item and display entity — **done**

**Build.** Three decisions move from the connection-wide `has` to the per-wood `supports`.
`PlankedChestBlock.getPolymerBlockState` (`PlankedChestBlock.java:125-128`) already holds its
`WoodType`, and `PlankedTrappedChestBlock` inherits it, so both families are covered by that one
edit. `ChestBlocks.registerItem` (`:155-160`) passes `PlankedChests.HANDSHAKE::has` as
`PolymerModelBlockItem`'s `useRealItem` predicate and needs the `WoodType` threaded in from the
registration loop at `:107-108`. `ChestElementHolder.startWatching` (`ChestElementHolder.java:99-101`)
needs the wood id, which it should take from the `PlankedChestBlock` already handed to its
constructor (`:72-76`) rather than parsing it back out of the `chestId` field at `:55`.

Those three edits reach further than they look, and the plan should not chase the rest separately.
`PolymerBlock.getPolymerReplacement` (`PolymerBlock.java:69-71`) derives the block from
`getPolymerBlockState` via `PolymerBlockUtils.getPolymerBlock` (`:145-147`), so block events,
holder-codec encoding (`mixin/other/RegistryMixin.java:24-39`) and recipe displays
(`SlotDisplayMixin.java:32-34`, which routes through the item-stack path) all follow the per-wood
decision automatically. Tags need nothing either way: `ClientboundUpdateTagsPacketMixin.java:45`
strips any `PolymerSyncedObject` whose `canSyncRawToClient` is false, and neither `PolymerBlock` nor
`PolymerItem` overrides it.

The guard's direction is what makes this safe: an unsupported wood falls back to the barrier plus
display entity that every non-mod client already gets, so a wrong answer costs a cosmetic downgrade
rather than a dropped connection.

**Done when.** On the S2 harness with S3 in place, in this order, because each step is only reachable
if the one before it passed. The client survives Polymer's item sync and reaches the world at all —
that alone is the regression fixed, and it is what S2's baseline failed. Then an oak chest renders
through the client's `BlockEntityRenderer` while the withheld wood's chest renders as a Polymer
display entity with a correct lid animation; opening both produces no error; and the creative tab
lists every wood the *server* registered.

If the item sync still fails on a *supported* wood, the entry-ordering problem flagged in S2's
`As built` is real and per-wood negotiation is not sufficient on its own — record the failing item
and stop rather than working around it here.

### As built

**Per-wood granularity does not work, and the reason kills the idea rather than complicating it.**
All three sites were converted as planned and the client failed byte-identically to S2 — same
`oak_chest`, same `custom_payload`. The item sync sends a representation `ItemStack` per server-side
item, so for any wood answered "supported" the representation is the real item, encoded by the
**server's** raw id. The client resolves that id while decoding the sync payload itself, when
`InternalClientRegistry.ITEMS` is still empty, so it falls through to its own registry by raw id —
which agrees only when both sides' Polymer tails line up. Woods and Mires' own Polymer-marked
content shifts the server's tail, so even a wood both installations have resolves wrongly. No
per-wood answer can fix that, because the id is not ours to choose.

**The granularity that works is per connection.** Real content only when the client's key set covers
every wood the server registered, implemented for now as a temporary `PlankedChests.fullyDecodable`
over the three sites. With it the client joins and stays connected — the S2 regression, fixed.

**And it costs nothing visually, which was the whole reason per-wood looked worth having.** With
every chest sent as its fallback, `oak_chest` still renders as a real chest with a chest-shaped
hitbox while `pine_chest` shows as a full block. Per-block fidelity is restored by the *client*, not
chosen by the server: Polymer hands `PolymerClientDecoded` content back to a client that can resolve
it — `InternalClientRegistry.getRealBlockState:163-173` for the palette decoder, and
`PolymerClientProtocolHandler:288-350` replacing the substitute outright via
`chunk.setBlockState(pos, block.blockState())` on its own block-update payloads. A wood the client
cannot resolve simply stays the barrier. So the server's gate only ever needed to decide whether
this connection is safe to talk to at all.

Two consequences for the rest of the document. S1 and S3 are unaffected and still required — the
per-wood keys are exactly what the set comparison is computed from; only S4's use of them collapses.
And the temporary helper belongs in `kalic0re` beside `supports`, as a `supportsAll(context)`, since
any client-optional Polymer mod with installation-varying content needs the same predicate. That
promotion is the remaining work in this stage.

**Done when (revised).** Unchanged in substance — the client joins, oak renders real, pine renders as
a display entity — but the gate is `supportsAll`, in `kalic0re`, not a temporary in `planked-chests`.

### As built, part two — the promotion and its verification

`supportsAll(PacketContext)` and `supportsAll(ServerGamePacketListenerImpl)` now sit beside
`supports` in `PolymerClientHandshake`, answering true when the client registered every key this
side did. They iterate `keyTypes.values()` directly rather than re-entering `supports`, which would
re-check the base marker per key, and reduce to `has` when no keys are registered. All four
`planked-chests` sites use them; the temporary helper and the per-wood call sites are gone, and the
now-unused `WoodType` parameter on `registerItem` and `woodId` field on `ChestElementHolder` went
with them.

Verified across three client configurations against one unchanged server (WoodsAndMires and
`polymer-patch-bundle` installed, 13 woods, 16 pine and 4 oak chests in one chunk):

| Client install | `supportsAll` | Result |
|---|---|---|
| No WoodsAndMires | false | Joins; every chest a display entity; oak restored to a real block client-side; no errors |
| WoodsAndMires + patch bundle | true | Joins; pine and oak both real chests through the client's own renderer |
| WoodsAndMires, no patch bundle | true | Joins; pine and oak both real chests; no decode error |

**The third row retires the risk flagged in S2's `As built`.** The worry was that a client whose
registry *shape* differs — W&M's content being Polymer server entries on the server but ordinary
modded entries on that client — would diverge on raw ids even with `supportsAll` true. It does not:
Polymer's tables are keyed by the server's ids and carry identifiers, so an identifier the client
holds resolves regardless of where its own registry put it. What broke the per-wood attempt was
never tail ordering as such, it was sending a real item the client had *no entry for at all*.
`supportsAll` is therefore sufficient as written, and does not need to reflect registry shape.

## S5 — Record why block-entity sync stays connection-wide — **done**

**Build.** `ChestBlocks.registerSyncedBlockEntity` (`:126-129`) moves from `has` to the same
connection-wide `supportsAll` the other three sites now use — edited here after S4 found that
per-wood granularity does not work, so this stage no longer has a granularity mismatch to justify.
Polymer's filter still never learns which block it is deciding for (see Findings), but that no longer
matters when every site agrees on one per-connection answer. Add a comment at the call site recording
what the connection-wide answer costs when it is false for a client that does run the mod: it
receives no block entity, so its `BlockEntityRenderer` never fires and every chest falls back to the
display entity — which is the correct outcome, not a compromise.

Do not build the per-wood block-entity split. There is no defect to fix here — the earlier plan
assumed a client-side warning per fallback chest, and that warning does not exist.

**Done when.** The comment is in place, and an S2-harness join with at least sixteen withheld-wood
chests in one chunk produces no new client-side warning or error attributable to the block entity.
That absence is the observation. A matched client — same mods both sides — must still get real
chests rendered by its own `BlockEntityRenderer`, which is the case `supportsAll` has to keep working
and the one this harness does not exercise.

### As built

`registerSyncedBlockEntity` moved to `supportsAll` with the other three sites, so the granularity
mismatch this stage existed to document never arose and there is nothing to justify at the call site
beyond the gate itself.

The predicted absence was observed: a mismatched client with 16 pine chests and 4 oak chests in one
chunk produced zero block-entity warnings and zero decode errors in its log — the only entries were
Realms authentication noise from the offline dev client. Every chest rendered through the display
entity, which is the correct outcome for a connection the server will not send real content to.

The matched-client case that this stage flagged as unexercised was then covered by S4's second and
third configurations: with `supportsAll` true the real block entities arrive and chests render
through `PolymerChestRenderer`.

## Reference: what a Polymer patch cannot do for a client that has the real mod

Observed while testing S4's third configuration, and unrelated to this feature, but easy to
misattribute to it. A client running WoodsAndMires natively, against this server, sees W&M's own
blocks doubled — the real block colliding with an untextured display entity — while its inventory
icons look correct.

That is `polymer-patch-bundle` doing what it is built to do. The patch converts W&M's content into
Polymer blocks so that clients *without* the mod can see it, but it contains zero references to
`PolymerClientDecoded` (checked against the jar's classes), which is the marker that tells Polymer to
hand the real object back to a client that can decode it. `planked-chests` does implement it, which
is why its chests are restored client-side in exactly the same session. So the patch substitutes
unconditionally, the client draws the substitute's display entity from the server-generated resource
pack it does not have applied, and the real block shows through underneath.

It is pre-existing and independent of this work: it affects any client that installs one of the
patched content mods, with or without `planked-chests`. The live server's arrangement assumes clients
install none of them, which is also why S2's first configuration — no content mods, `planked-chests`
only — is the one that matches the original bug report.

## S6 — Version bump, docs and deploy — **done**

**Build.** Bump `mod_version` in both `rkc/mods/kalic0re/gradle.properties` and
`rkc/mods/planked-chests/gradle.properties` off `0.1.0`, and update the `kalic0re_version` pin in
`planked-chests`. Two identically named `plankedchests-0.1.0.jar` builds with different content is
exactly how a client/server mismatch stays invisible, and this fix is worth much less if the jars
cannot be told apart. `companions` shades only `common/item` and `common/datagen`, not `common/net`,
so it needs no rebuild; `publishToMavenLocal` adds a version without removing `0.1.0`, so its pin
keeps resolving either way. Bump it for hygiene or leave it, but decide rather than drift.

Then `publishToMavenLocal` for kalic0re, `clean build` for `planked-chests`, install the unclassified
`shadowJar` output (not `-unshaded`) and deploy per CLAUDE.md's swap-never-stack rule. Finally, edit
`planked-chests/docs/plan/modded-wood-types.md` — S1's `As built` describes the `isModLoaded` guard
and the `DATAGEN` escape as the whole story, and needs a sentence pointing here.

**Done when.** The live server restarts clean (`docker compose logs mc | grep -icE 'Mixin apply
failed|InvalidInjectionException'` returns 0), a real client running the installed jar sees correct
chests, and the same client with all four wood mods removed joins and stays connected — no decode
failure, every modded wood falling back to a display entity.

### As built

Both mods went to `0.2.0` and `planked-chests`' `kalic0re_version` pin with them. `companions` was
deliberately left pinned at `0.1.0`: it shades only `common/item` and `common/datagen`, neither of
which changed, and `publishToMavenLocal` adds a version rather than replacing one, so the old pin
still resolves. That is a decision, not drift — revisit it the next time `companions` is rebuilt for
its own reasons.

The shipped jar's relocation was checked before deploy, since dev runs never exercise it:
`club/rainbowkitty/plankedchests/shaded/kalic0re/net/PolymerClientHandshake.class` carries
`supportsAll`, and the jar contains zero unrelocated `club/rainbowkitty/kalic0re/` classes.

Deployed both jars to live by swap (`plankedchests-0.1.0.jar` and `kalic0re-0.1.0.jar` deleted in the
same step), after `save-all`, with one player connected and the operator's go-ahead. On restart:
zero `Mixin apply failed`/`InvalidInjectionException`, zero `DecoderException`/registry-sync errors,
`Planked Chests loaded` once, and `plankedchests:pine_chest` accepted as a known block id — so the
modded woods still register against the live server's WoodsAndMires.

One thing rode along that this plan did not put there: the same build carries a
`plankedchests:disable_vanilla_recipes` built-in data pack from separate in-flight work, registered
`DEFAULT_ENABLED`, which overrides the vanilla chest and trapped-chest recipes so the plank ring
yields this mod's chests instead. It is live and confirmed enabled in `datapack list`. Reversible
with `/datapack disable "file/plankedchests:disable_vanilla_recipes"` — see
`BuiltinDataPacks.java:15-22`.

## Open decisions

**Whether `kalic0re` should offer a compact encoding for large key sets.** One packet id per key is
readable and reuses a proven path, but it scales against the 32767-byte payload cap (S1). A consumer
needing hundreds of keys would want an ordered bitmask in a single id's version int, or Polymer's
metadata channel with the timing caveat from Findings. Settled by the first consumer that exceeds the
budget; `planked-chests`' 27 does not.

## Reference: verified Polymer handshake surface

Read out of `libs/polymer` on branch `dev/26.2`, matching `polymer-bundled-0.17.5+26.2.jar` on live.

| What | Where |
|---|---|
| `registerClientboundVersioned(Type<T>, int, codec)` | `PolymerNetworking.java:117-119` |
| `registerServerboundVersioned(...)`, same shapes | `PolymerNetworking.java:141-147` |
| `getSupportedVersion(ServerGamePacketListenerImpl, …)` | `PolymerServerNetworking.java:71-73` |
| `getSupportedVersion(Connection, Identifier)` | `PolymerNetworking.java:180-182` |
| Returns `-1` for an id the client never declared | `mixin/ConnectionMixin.java:53-55` |
| Returns `-1` for an id the server never registered | `impl/ServerPackets.java:21-34` |
| Client sends its id map on the server's hello | `impl/client/ClientPacketRegistry.java:45-47`, `:139-141` |
| Client receives the server's map | `impl/client/ClientPacketRegistry.java:110-124` |
| Server folds it into the per-connection support map | `impl/ServerPacketRegistry.java:85-99` |
| Join continuation, the real ordering barrier | `api/server/EarlyConfigurationNetworkHandler.java:92-101` |
| Handshake id map is `Map<Identifier, int[]>` | `impl/packets/HandshakePayload.java:13-24` |
| Serverbound payload cap, 32767 bytes | `ServerboundCustomPayloadPacket.MAX_PAYLOAD_SIZE` |

`PolymerClientDecoded` (`polymer-core/.../api/utils/PolymerClientDecoded.java`) is the marker that
lets a client with the companion mod keep the real object rather than the substitute;
`PlankedChestBlock` already implements it, and this work does not change that — it changes only which
connections are told the real object in the first place.

## Reference: how a content mismatch reaches `byIdOrThrow`

The chain, for the record, since it is not obvious from any single file:

1. `PolymerObject.is(value)` on registration marks the block or item a registry server entry —
   `polymer-core/.../mixin/other/MappedRegistryMixin.java:39-47`.
2. Polymer's reorder-on-freeze moves every server entry to the tail of the registry —
   `polymer-reg-sync-manipulator/.../mixin/MappedRegistryMixin.java:45-76`.
3. Fabric's sync map is built by iterating the registry and skipping null keys
   (`RegistrySyncManager.createAndPopulateRegistryMap`); Polymer wraps `Registry.getKey` to return
   null for server entries — `.../rsm/mixin/fabric/fabricSync_RegistrySyncManagerMixin.java:31-37`.
   So `plankedchests:*` never reaches `ClientRegistrySyncHandler.checkRemoteRemap`, and the client is
   not rejected.
4. The client decodes those ids through Polymer's identifier-keyed tables —
   `InternalClientRegistry.setDecoders()` at `:176-192`, hooking `Block.BLOCK_STATE_REGISTRY`,
   `ITEM`, `BLOCK` and `BLOCK_ENTITY_TYPE`.
5. An identifier the client never registered yields a null `registryEntry()`, the decoder returns
   null, and `decodeRegistry` falls through to `instance.byIdOrThrow(i)` — `:193-201`. The server's
   tail id is past the end of the client's registry, so it throws.

Where that throw surfaces depends on the path, and they look nothing alike. S2 observed the first
one; the other two are reachable only by a client that survives login:

- **Polymer's own item sync**, which is what actually fires first and kills the connection before
  the player spawns. `PolymerServerProtocol.sendSyncPackets:120` sends one entry per server-side
  `ITEM`, each carrying a representation `ItemStack` whose item resolves through the same decoder;
  the throw is caught, logged and rethrown at `PolymerItemEntry.java:22-30`, and surfaces as
  `DecoderException: Failed to decode packet 'clientbound/minecraft:custom_payload'`. The item it
  names is the entry the misalignment lands on, not the content that is actually missing.

- **Chunk section palettes** are not decoded by Netty; the bytes are carried raw and read on the
  client main thread, with `byIdOrThrow` redirected into `decodeRegistry` by
  `polymer-core/.../mixin/block/packet/BlockPaletteMixin.java:29-33`. The result is an
  `IllegalArgumentException` during chunk apply.
- **Codec-level packets** — block update, section update, entity data, block event — go through
  `ByteBufCodecs.idMapper`, and the throw is wrapped as
  `io.netty.handler.codec.DecoderException: Failed to decode packet '…'`.

Intermittent because either path only runs when a packet actually carries one of the missing woods: a
chunk containing one placed, a block update from one being placed or broken, or the block event that
animates its lid. Item stacks are not a trigger while `USE_UNSAFE_ITEMS_CLIENT` is set, since
`InternalClientRegistry.decodeItem` (`:204-217`) substitutes a virtual item instead of returning null.
