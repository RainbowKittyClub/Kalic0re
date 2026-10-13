# Portal framework

A shared `common/portal` package that lets a mod declare a kind of portal, frame material,
look, sounds and all, and hands it the moments that matter: a frame lit, a portal faded, an
entity entering or arriving. Everything else, from how two portals pair to who may link them, stays
in the consumer. Claims about vanilla and Polymer were checked against
`libs/decompiledjava/minecraft-26.2/` and `libs/polymer/` (branch `dev/26.2`) while
`rkc/mods/player-portals` was built; that mod is the first consumer and the proof.

The framework was lifted out of player-portals in one move (S1), then that mod was rebuilt on it
(S2); S3 added the centre-crossing rule and ambient emitters. S1 is usable alone; S2 is what
makes the move complete.

---

## Findings that reshape the work

**The framework owns no tick.** Its block implements vanilla `Portal`, so `PortalProcessor` does
the per-entity work (Entity.java:2614-2635); Polymer attaches one display holder per interior
block on chunk load and block set and never ticks it (WorldChunkMixin.java, ChunkAttachment.java:
89-95); lit frames are indexed by chunk, so a crossing or a block break never reads the world to
find its portal.

**One element can serve two kinds of client.** Polymer asks each element for its spawn packets
per watcher (VirtualElement.java:48), so `MaterialElement` sends an item display of the material's
item to a watcher with the server's resource pack and a block display of its block state to any
other, and remembers which, so the right one is removed later. Both lie as the X plane's slab and
are turned together for the Z and flat planes; an item model is drawn about the entity's position
and a block model from it as the model's origin corner, so only the block display is moved back
by the turned half-block.

**The consumer's links live beside the framework's frames, not in them.** `LitFrames` holds lit
frames and the dead ones waiting for their chunks; a consumer keeps its own saved data for where
each leads. `light` fades a frame it already has before relighting, and `fade` calls
`PortalKind.onFaded` before the blocks clear, so a consumer that unlinks there and fades partners
left without a link keeps the two in step by construction.

---

## How these stages get tested

kalic0re has no runtime of its own; `./gradlew publishToMavenLocal` and the Checkstyle lint are
its checks. Behaviour is observed through player-portals: its dev server (port 25624) booting with
zero mixin failures, and its mineflayer harness `scripts/loadtest/player-portals.js`, every leg of
which runs on the framework's block, finder, registry and exit. A stage is done when those pass.

## S1 — `common/portal` — **done**

**Build.** `PortalKind` (frame predicate, `PortalMaterial`, `onLit`, `onFaded`, `onEnter`,
`onExit`, `transitionTime`), `Portals` (the handle: registers the block, finder and saved data,
`light`, `fade`, `portalAt`, `portalOwning`, `has`, `transition`), `PortalBlock`, `PortalPlane`,
`PortalFrame`, `FrameFinder`, `PortalExit`, `LitFrames`, `PortalHolder`, `MaterialElement`.
Published as kalic0re 0.27.0; consumers list `portal` in `kalic0re_shared_packages`.

**Done when.** kalic0re builds and lints clean, and a consumer that shades `portal` alone
compiles against it.

### As built

As planned. `PortalBlock.properties` had to become `defaultProperties`: `BlockBehaviour` has an
instance method of that name. The block's codec is `MapCodec.unit(this)`, since a block that
carries its `Portals` cannot be built from a codec. Every client is sent a light block of the
block's own light level (capped at 15) as the stand-in, so a consumer may change the light in
its properties and the client's lighting still agrees.

An adversarial review of the first draft changed four things. `light` refuses, with nothing
changed, a frame that is not complete by the time its turn comes, so two frames that cross each
other cannot overwrite one another's interior; it returns that answer. `Portals.transition`
runs the kind's `onExit` once, for the entity that entered: vanilla copies the post-transition
step onto every rider's transition and runs those first. A player whose pack status changes
mid-session is shown every portal display in loaded chunks of their level afresh
(`PolymerCommonUtils.ON_RESOURCE_PACK_STATUS_CHANGE`), since Polymer's own chunk resend never
re-asks a display that is already watched. And `Portals.lit` hands a consumer the lit frames,
so it can reconcile its own records after a crash that saved one file and not the other. The
material is now asked once and its stack copied; the saved data is `modId:path_frames`.

## S2 — player-portals on the framework — **done**

**Build.** Replace that mod's `frame/PortalPlane`, `frame/PortalFrame`, `frame/FrameFinder`,
`portal/PortalBlock`, `portal/PortalBlocks`, `portal/PortalRegistry`, `portal/PortalTravel`,
`portal/PortalExit` and `display/FlatPortalHolder` with a `PlayerPortalKind`, a `PortalLinks`
saved data for destinations, and a `portal_material` item whose pack model is the nether portal
texture tinted `#0a4646` (its block-state fallback `nether_portal[axis=x]`).

**Done when.** The harness passes with a bot that holds the pack seeing one `item_display` per
interior block over upright and flat portals alike, and `light` as the block under it.

### As built

Recorded in `rkc/mods/player-portals/docs/plan/player-portals.md`.

## S3 — Centre crossing and ambient emitters — **done**

**Build.** Take an entity only once the centre of its bounding box is inside a portal block, or
passed through one during the tick, instead of on any overlap; vanilla visits every block along
the tick's movement, so the test is the segment from the centre's start-of-tick position to its
current one against the block's cube. Add `PortalEmitter` (a particle or a series played in
turn, an offset from the portal's centre and a gaussian spread per axis in the portal's own frame,
optionally scaled to the frame's half-size, a count, a speed and a period) and
`PortalKind.emitters`; `Portals` bursts every due emitter for every lit frame of a level at the
end of that level's tick, and returns at once for a level with no lit frames or no players.

**Done when.** A bot standing beside a portal, its box touching the plane and its centre a
quarter block outside, stays put; moved to the centre, it goes. A bot within 32 blocks of a lit
portal receives particle packets of each kind in the series inside the portal's box, and none
from an unlit frame.

### As built

As planned. The first consumer's series is `effect`, `instant_effect` and `entity_effect` in
three teal shades, two particles every two ticks over the plane and one pale spark every ten
along the bottom edge; the material tint became `#159c9c`. This is the framework's one per-tick
walk: one map lookup per level, then one `due` test per emitter and one `sendParticles` per lit
frame per due emitter, which vanilla delivers only to players within 32 blocks.

## Reference: the contract

- The client-side stand-in is `light`, which the vanilla client treats as replaceable and
  waterloggable: a block placed or water poured into a portal is predicted and then corrected by
  the server. Nothing short of a different stand-in removes that, and none matches the light.

- `Portals.register(modId, path, kind, properties)` once at init; the block is
  `modId:path`, its saved data `modId:path` in the overworld.
- `light` requires a complete frame (`finder().isComplete`); `fade` on an unlit frame is a no-op.
- `onEnter` runs only for a portal the framework has just found whole; a portal found holed
  fades silently, a stray portal block is destroyed.
- `Portals.transition` keeps velocity and facing, turns a quarter between the X and Z planes,
  places the arrival in the far frame, gives riders a portal cooldown and then calls `onExit`.
