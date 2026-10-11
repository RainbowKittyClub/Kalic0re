# kalic0re

Shared Java library for the Rainbow Kitty Club mods. Targets MC 26.2, Fabric API + Polymer.

## Packages

- `common.aim` — `Crosshair`, what one player's crosshair is on, block and entity both,
  resolved server-side against their own reach.
- `common.armor` — custom-armor framework, one declaration per set; needs `item` and `datagen`
  alongside it.
- `common.client` — `@Environment(CLIENT)` renderer code (`PolymerChestRenderer`,
  `PolymerChestRenderState`, `ChestSpriteSelector`), loaded only by a consumer's own client
  entrypoint and shipped unused on a dedicated server.
- `common.datagen` — `PngAssets`, `EntityModelBoxes`, `PackJson`, `AtlasSources`, `DatagenRun`. Spit
  raw JSON, since vanilla's typed model builders cannot load under `client = false` datagen. Also
  `SpecialModelExtentsProvider`, which needs `client = true` datagen and `common.display`: it bakes
  vanilla's special item renderers and writes the box each draws in, for `ItemModelExtents`.
- `common.death` — registry for themed death-message overrides, for players (`register`) or any
  living entity such as a pet (`registerLiving`); the consumer owns the mixin at each
  `getDeathMessage()` call it wants routed through `resolve`.
- `common.claim` — `ClaimExemptions`, one probe per claim mod (GOML, Flan, Open Parties and Claims,
  FTB Chunks, the Common Protection API) answering what it would say without its by-id exemptions,
  and a listener that lets a modded block stand in for a vanilla one. Compiled against those mods'
  jars `compileOnly` (`build.mod.gradle`); each probe loads only when its mod does.
- `common.click` — `HandClicks`, which drops the rest of a right-click once the server has taken
  it with one hand, where the client predicted a pass and went on to offer it to the other. The
  consumer owns the mixin on `ServerGamePacketListenerImpl` that routes through it.
- `common.command` — `CommandPermissions`, gating one mod's commands on `<modId>.command.<path>`
  nodes through fabric-permissions-api when some mod provides it (LuckPerms nests it), else on the
  vanilla operator level. The API is `compileOnly` (`build.mod.gradle`) and named by one nested
  class only, so a consumer without it still loads the gate. `PermissionCondition` is the same
  question as a datapack predicate: a consumer registers it under its own id, and
  `execute as <player> if predicate <id>` or `@a[predicate=<id>]` asks it, failing closed without
  a player.
- `common.craft` — a 3×3 crafting grid that keeps its contents (`CraftingGridBlockEntity`), the
  shared menu over it (`PersistentCraftingMenu`) and vanilla's per-player one at any block
  (`BlockCraftingMenu`). The consumer owns the mixin that reads `CraftingGridHandoff`.
- `common.display` — `ItemDisplayPose`, `LidTween`, `ItemDisplays`, `RenderTrail`, for chest- and
  cart-display elements; `ContainerContentsHolder` and `ContainerContentsVisual`, a container's
  slots shown as items on a block; `ItemModelExtents`, the box an item's model occupies, read from
  the client jar Polymer keeps and the loaded mods' assets, with special models' boxes from the
  file a consumer's datagen writes with `SpecialModelExtentsProvider`.
- `common.effect` — player-effect framework, the player-as-subject counterpart to `armor`: a declared
  effect gets per-tick and per-event plumbing without the consumer writing its own dispatcher.
- `common.enchant` — enchantment framework: declare cost, targets and effects, get the generated
  datapack entry plus somewhere to register custom effect code. `common.enchant.client` and
  `common.enchant.datagen` hold the render and asset-generation halves.
- `common.glow` — `PrivateGlow`, one entity outlined in one colour for one player, by telling
  that client something the server does not hold. No server state and no access widener.
- `common.item` — `ItemRegistration`, `PolymerModelItem`, `PolymerModelBlockItem`.
- `common.label` — per-player raycast and hover-label machinery (`HoverScanner`, `HoverLabel`,
  `BlockLabels`, `EntityLabels`, `ItemText` and friends). A runtime package: it ticks per player, and
  `runServer` runs the un-relocated classes, so only an installed jar tests the relocation.
- `common.net` — `PolymerClientHandshake`, the per-connection "does this client run the mod too" flag.
- `common.piston` — `PistonMovable`, rules letting pistons move a block with a block entity; the
  consumer owns the mixin that consults them.
- `common.rule` — `ComponentGameRules`, registering rules as `<modId>.<component>:<path>` instead of
  one flat per-mod prefix.
- `common.text` — `FallbackText`, translatable text carrying the mod's English from its own
  `en_us.json` as fallback, since a vanilla client holds no server-side mod's lang file. English
  lives only in that file; a key it lacks is logged once and shown as itself.
- `common.vehicle` — cargo-minecart framework (`CargoMinecarts`, `CartCargo` and friends); needs
  `display` alongside it.

## Datapack

`kalic0re.zip` is a helper datapack for adventure maps and the server, built from
`src/main/resources/resourcepacks/kalic0re/`. `./gradlew datapackZip` writes it to
`build/datapacks/`, for a world's `datapacks/` folder. Its English lives only in `lang/en_us.json`,
which never ships: the build gives each `kalic0re.*` translate key its English as `fallback`, and
builds each dialog `.json` among the functions into a function. Function ids below are under
`kalic0re:`.

### Displays — `effect/display/`

A display is a block, item or text display (`#kalic0re:effect/displays`).

- `create_from_blocks {from:"<x y z>",to:"<x y z>",name:"<name>",tags:[<tag>,…],scale:[<x>,<y>,<z>]}`
  makes one display of the blocks in the cuboid from–to and leaves the blocks in place: an
  invisible root, named and tagged as given (`name:""`, `tags:[]` for neither), at the centre of
  the top layer, with a block display riding it for each block. `scale` sizes the blocks along each
  axis and spaces them so they still meet, on the cuboid's ground and about its centre across;
  `[1,1,1]` is full size. Returns the block count, and fails, making nothing, on no blocks or a
  scale under 1/1024. Every part takes the light at the root, so a solid block left there draws the
  display dark; clear the blocks with `fill … strict`, as a plain fill drops lanterns and torches.
  Each block costs about 45 commands of `max_command_sequence_length`: some 1450 blocks a call at
  the default 65536.
- `execute as <display> run function kalic0re:effect/display/animate_move_display
  {ramp:"linear"|"smooth",dx:<x>,dy:<y>,dz:<z>,ticks:<n>}` moves the display and its riders by
  dx dy dz over `ticks`, a teleport each tick that clients glide over. A new move or bounce
  replaces the one under way.
- `animate_bounce_display {from:"<x y z>",to:"<x y z>",speed:<blocks a second>}`, run as a display,
  bounces it around the room from–to like the DVD logo until stopped, and keeps what it draws
  inside. On a tick it turns off a wall, its score `kalic0re.effect.displayBounceWallHits` is the
  number of walls (3 at a corner), and 0 otherwise.
- `animate_stop_display`, run as a display, stops its move or bounce where it is.
- `remove_display`, run as a display, removes it and every display riding it, and lets any other
  rider off.

A move or bounce carries on after `/reload`, a restart, or its chunk loading again.

### Sound effects — `effect/sound/`

- `play {effect:"<function>",targets:"<players>",location:"<x y z>"}` plays a sound-effect
  function's notes from `location` to the players `targets` picks out now; `~`, `^`, `@s` and
  `distance` read as in your own `playsound`. Later notes play on later ticks, and survive a reload.
- A sound-effect function sets storage `kalic0re:effect sound.notes` to a list of
  `{sound,source,volume,pitch,delay}`, `delay` being seconds after the previous note. The pack
  ships `kalic0re:effect/sound/secret_discovery_01`.

### Fireworks — `firework/`

`launch`, run as and at whatever sets it off, launches the particle-drawn firework in storage
`kalic0re:firework launch`. It bursts at its `position` (`~` counts from the launch point), its
rocket rising from the launch point. A setting it leaves out takes the default:

| Setting | Default |
| --- | --- |
| `pattern` | `"large_ball"`; also `small_ball`, `star`, `burst`, `creeper` |
| `colors`, `fadeColors` | RGB int arrays: gold and pale yellow, fading to white |
| `radius`, `position` | `4.0`, `"~ ~10 ~"` |
| `riseTicks`, `riseColor`, `burstTicks` | `16`, white, `6` |
| `dustScale`, `hasTrail` | `2.0`, on |
| `sparkSpeed`, `sparkParticle` | `0.35`, `"minecraft:firework"` |
| `hasTwinkle`, `twinkleDelayTicks`, `twinkleSparks`, `twinkleSpread` | on, `12`, `40`, `2.0` |
| `playLaunchSound`, `launchSound` | on, `"minecraft:entity.firework_rocket.launch"` |
| `playBlastSound`, `blastSound` | on, `""` (vanilla's sound for the pattern) |
| `playTwinkleSound`, `twinkleSound` | on, `"minecraft:entity.firework_rocket.twinkle"` |
| `soundSource`, `soundVolume` | `"ambient"`, `3.0` |
| `particleMode` | `"force"`, or `"normal"` |

The Create Firework dialog edits one. Set storage `kalic0re:firework editor` to
`{firework:{…},save:"<function>",context:"<text>"}`, run `firework/editor/open` as the player, set
the dialog's exit in `kalic0re:dialog built.create_firework`, then run
`dialog/show {name:"create_firework"}`. Save writes the firework to storage
`kalic0re:firework edited` and runs `save` with `{context:"<context>"}`; Preview sets it off where
the player stands.

### Parkour — `parkour/`

Timed courses. A run saves and clears the runner's inventory and effects, gives them run items,
holds them to the course's boundaries and ground rules, and records their times.

Run as the player, from a pressure plate's command block, say:

- `start {course:"<name>"}` starts or restarts a run and forgets any checkpoint. A missing course or
  start is explained in chat, with a link that fixes it.
- `checkpoint/set` makes where they stand their checkpoint. `execute positioned <x y z> as
  @p[distance=..2] facing <x y z> run function kalic0re:parkour/checkpoint/set_at` sets one at a
  given place and facing. Both refuse outside a run.
- `finish` stops the timer, records the time and sets off the course's finish firework; the runner
  stays on to restart or quit. Fails unless their timer is running.

A runner holds three items: Checkpoint (back to it, the timer running on), Restart (back to the
start, the timer reset) and Quit. They pick up nothing else, and the timer shows above the hotbar.
Leaving the course's boundaries, standing on a forbidden block, or catching fire where burning is
denied sends them back to their checkpoint. A run ends when their `kalic0re.parkour.isActive`
score goes to 0, as Quit or an op does; their inventory and effects come back, and they go to the
lobby. A course's best times are on the scoreboard `kalic0re.parkour.bestTicks.<courseId>`.

`/trigger kalic0re.parkour.leaderboard` opens the Parkour Leaderboards dialog, and `set <courseId>`
one course's board; a player's first finish enables it for them. A text display tagged
`kalic0re.parkour.directionMarker` bobs along the way it points; run `direction_marker/stop` as it
before changing its translation, then tag it again.

Course admin needs op. `admin/courses` opens the Parkour Courses dialog: New Course, then one Manage
dialog per course, to rename it, move its start or lobby, delete it, clear times, place a
leaderboard display, edit its finish firework, get the editor wand or edit its ground blocks.
`/trigger kalic0re.parkour.admin` opens the same dialog for a player an op has enabled
(`scoreboard players enable <player> kalic0re.parkour.admin`), though its buttons still need op.
The same work as functions:

- `admin/new_course` opens the New Course dialog, and `admin/new_course_named {course:"<name>"}`
  fills in its name. `admin/create {course:"<name>"}` makes a course starting where you stand, and
  `admin/set {course:"<name>",point:"start"|"lobby"}` moves a point to you.
- `admin/course_boundary/add {course,dim,x1,y1,z1,x2,y2,z2}` adds a box the runner's feet must stay
  in; together the boxes must hold the start, checkpoints, finish and the air between.
  `add_exclusion` with the same arguments adds a box they must stay out of.
- `admin/course_boundary/give_wand {course:"<name>"}` gives the Parkour Editor Wand. Left- and
  right-click mark the corners; sneaking, right-click clears them and left-click changes the wand's
  course. Right-click looking straight up adds a boundary from the corners, looking straight down
  removes the one you stand in, and at a leaderboard display edits it. Held, it outlines the
  selection and the course's boundaries.
- `admin/standing_block/add {course,block:"<id or #tag>"}` and `remove` list ground blocks, and
  `mode {course,mode:"allow"|"deny"}` makes them the only ones a runner may stand on, or (the
  default) the ones they may not. `admin/standing_block/chest {course}` edits the list as a chest,
  and `admin/course/burning {course,mode:"allow"|"deny"}` sets whether fire sends a runner back.
- `admin/displays/leaderboard {course,position:"<x y z>",rotation:"<yaw pitch>"}`, or
  `leaderboard_sized` with a `size`, builds a board of the ten fastest times. `edit_nearest`,
  `resize_nearest {size}` and `remove_nearest` act on the nearest board within 8 blocks.

### Utilities

- The scoreboard `kalic0re.timer` holds `#2ticks`, `#4ticks` … `#1024ticks`. Each counts 0 to n−1
  and starts over, all in step, so `matches 0` holds once every n ticks.
- `block_state/read` reads the block at the execution position into storage
  `kalic0re:block_state state`, in the form a block display's `block_state` takes. It fails on a
  block this Minecraft version lacks, such as a modded one.
- `redstone/push_button {position:"<x y z>"}` pushes the button there as a player would, for as long
  as their push holds it: 30 ticks a wooden button, 20 a stone one. `push_button_for
  {position:"<x y z>",ticks:<n>}` holds it down for `ticks` instead. Either way it clicks, and what
  it powers, through the block it's on too, follows it. Both return the ticks, and fail on anything
  but an unpressed vanilla button in a loaded chunk, a pressed one included. The chunk stays loaded
  until the button is let up; after a restart, the wait carries on once the chunk loads again.
- `text/only_coordinates`, `only_digits`, `only_identifier`, `only_resource_id` and
  `only_color_list`, for `execute if function`, pass when storage `kalic0re:text check` holds only
  that kind of character, so it can go into a command and do nothing else there.
  `text/place_readable` checks `kalic0re:text place`'s `position` and `rotation` the same way.
