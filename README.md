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
- `common.death` — registry for themed death-message overrides.
- `common.claim` — `ClaimExemptions`, one probe per claim mod (GOML, Flan, Open Parties and Claims,
  FTB Chunks, the Common Protection API) answering what it would say without its by-id exemptions,
  and a listener that lets a modded block stand in for a vanilla one. Compiled against those mods'
  jars `compileOnly` (`build.mod.gradle`); each probe loads only when its mod does.
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
- `common.vehicle` — cargo-minecart framework (`CargoMinecarts`, `CartCargo` and friends); needs
  `display` alongside it.
