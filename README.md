# kalic0re

Shared Java library for the Rainbow Kitty Club mods. Targets MC 26.2, Fabric API + Polymer.

**Never installed, never run.** There is no entrypoint and no mixin config. A consumer relocates the
packages it wants into its own jar under `<its maven_group>.shaded.kalic0re`, so each mod still
installs standalone and two consumers side by side never define the same class name. Fabric API and
Polymer are `compileOnly` here (`library_only=true`), so a consumer inherits no runtime claim this
jar cannot honour. See "Sharing code between mods" in the workspace `CLAUDE.md` for the arrangement.

## Consuming it

Two keys in the consumer's `gradle.properties` — the shared `build.gradle` does the rest:

```properties
kalic0re_version=0.13.0
kalic0re_shared_packages=item,datagen,net,client,vehicle,display,label
```

Only the `common/` subpackages named there come across, so importing any other one fails to compile
rather than silently bundling it. Run `./gradlew publishToMavenLocal` here after changing any of
them, or consumers build against the stale copy.

**Mixins are never shared.** A relocated mixin package name can be right in the shipped jar or right
in dev, never both. So a framework keeps its registry here (`death`, `enchant`, `label`) and the thin
`@Mixin` class lives in the consumer, with that consumer's own `$`-prefix.

## Packages

- `common.armor` — custom-armor framework, one declaration per set; needs `item` and `datagen`
  alongside it (`docs/plan/custom-armor-models.md`).
- `common.client` — `@Environment(CLIENT)` renderer code (`PolymerChestRenderer`,
  `PolymerChestRenderState`, `ChestSpriteSelector`), loaded only by a consumer's own client
  entrypoint and shipped unused on a dedicated server.
- `common.datagen` — `PngAssets`, `EntityModelBoxes`, `PackJson`, `AtlasSources`, `DatagenRun`. Spit
  raw JSON, since vanilla's typed model builders cannot load under `client = false` datagen.
- `common.death` — registry for themed death-message overrides.
- `common.display` — `ItemDisplayPose`, `LidTween`, `ItemDisplays`, for chest- and cart-display
  elements.
- `common.effect` — player-effect framework, the player-as-subject counterpart to `armor`: a declared
  effect gets per-tick and per-event plumbing without the consumer writing its own dispatcher
  (`docs/plan/player-effect-framework.md`).
- `common.enchant` — enchantment framework: declare cost, targets and effects, get the generated
  datapack entry plus somewhere to register custom effect code. `common.enchant.client` and
  `common.enchant.datagen` hold the render and asset-generation halves
  (`docs/plan/enchant-framework.md`).
- `common.item` — `ItemRegistration`, `PolymerModelItem`, `PolymerModelBlockItem`.
- `common.label` — per-player raycast and hover-label machinery (`HoverScanner`, `HoverLabel`,
  `BlockLabels`, `EntityLabels`, `ItemText` and friends). A runtime package: it ticks per player, and
  `runServer` runs the un-relocated classes, so only an installed jar tests the relocation.
- `common.net` — `PolymerClientHandshake`, the per-connection "does this client run the mod too" flag.
- `common.rule` — `ComponentGameRules`, registering rules as `<modId>.<component>:<path>` instead of
  one flat per-mod prefix.
- `common.vehicle` — cargo-minecart framework (`CargoMinecarts`, `CartCargo` and friends).

`docs/plan/` holds the design notes.
