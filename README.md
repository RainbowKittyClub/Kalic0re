# kalic0re

Server-side Fabric mod library for Rainbow Kitty Club server. Targets MC 26.2. Fabric API + Polymer (core + resource pack) + BIL wired in.

## Layout

Each feature owns a top-level package, share raycast/label machinery in `common`:

- `club.rainbowkitty.kalic0re.Main` — `ModInitializer` entrypoint.
- `club.rainbowkitty.kalic0re.books` — chiseled-bookshelf hover titles.
- `club.rainbowkitty.kalic0re.frame` — item-frame hover labels.
- `club.rainbowkitty.kalic0re.armorstands` — armor-stand equipment hover labels.
- `club.rainbowkitty.kalic0re.common.label` — shared per-player raycast + label machinery, all three feature build on this.
- `club.rainbowkitty.kalic0re.common.item` — item registration + Polymer-model item/block-item helper, shared by sibling mod that shade it.
- `club.rainbowkitty.kalic0re.common.armor` — custom-armor framework: one declaration per set
  become registered items, their Polymer wiring, their generated assets. Not used by this mod own
  features; exist for sibling mods that shade it (see `docs/plan/custom-armor-models.md`).
- `club.rainbowkitty.kalic0re.common.effect` — player-effect framework, `common.armor` player-as-subject counterpart: declared effect get per-tick/per-event plumbing to keep firing without consumer write own dispatcher (see `docs/plan/player-effect-framework.md`).
- `club.rainbowkitty.kalic0re.common.enchant` — enchantment framework: declare enchantment cost, targets, effects, get generated datapack entry plus place to register custom effect code (see `docs/plan/enchant-framework.md`). `common.enchant.client` and `common.enchant.datagen` hold render half and asset-generation half.
- `club.rainbowkitty.kalic0re.common.vehicle` — cargo-minecart framework, shared by sibling mods that shade it.
- `club.rainbowkitty.kalic0re.common.display` — shared item-display pose + lid-tween helper (`ItemDisplayPose`, `LidTween`, `ItemDisplays`) for chest- and cart-display elements.
- `club.rainbowkitty.kalic0re.common.net` — `PolymerClientHandshake`, shared by sibling mods that shade it.
- `club.rainbowkitty.kalic0re.common.client` — `@Environment(CLIENT)` renderer code (`PolymerChestRenderer` and friends), load only by consumer own client entrypoint.
- `club.rainbowkitty.kalic0re.common.datagen` — shared datagen classes (`PngAssets`, `EntityModelBoxes`, `PackJson`, `AtlasSources`, `DatagenRun`); spit raw JSON since vanilla typed model builder no can load under `client = false` datagen.
- `club.rainbowkitty.kalic0re.common.death` — shared registry for themed death-message override; the `@Mixin` that redirect into it live in each consumer mod.
- `club.rainbowkitty.kalic0re.common.rule` — `ComponentGameRules`, register game rules scoped to one feature namespace instead of one flat `kalic0re:` prefix.
- `club.rainbowkitty.kalic0re.common.mixin` — mixin package, registered via `src/main/resources/kalic0re.mixins.json`.
- `docs/` — design notes, one file per feature.
