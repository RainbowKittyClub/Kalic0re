# Custom armor models

A `common/armor` package in `rkcore` that sibling mods shade, turning one declaration of an armor set
into its registered items, the Polymer wiring that makes them visible to vanilla clients, and the
datagen that writes their equipment assets, item models and textures. Each slot picks its own render
backend: a vanilla-shaped **layer** (a custom equipment asset — the path `companions`' wolf sweaters
already take) or a real **item model** on the head, the one slot where a vanilla client will draw
arbitrary geometry. Wearers are not limited to players: a set names the layer targets it draws on, so
wolves, horses, llamas and happy ghasts are declarable the same way humanoids are.

Everything below about vanilla's render path and Polymer's item handling was read out of
`libs/decompiledjava/minecraft-26.2` and `libs/polymer` (branch `dev/26.2`), and out of the working
sweater implementation under `rkc/mods/companions`; it is cited by file and line. None of it has been
run as a framework — every stage is `planned`, and the head-slot backend has no precedent anywhere in
this workspace, so treat S6 as the stage most likely to move.

S1 is the harness, and comes first because `rkcore` registers no content of its own and nothing here
can be seen without a consumer. S2 is the declaration every later stage compiles against. S3 makes
the items exist; S4 and S5 together make a layer-backed piece visible; S5's item-model half plus S6
make a head-model piece visible. The S4/S5 layer work and S6 are independent of each other. The
feature is complete when S6's observation lands — both backends drawn on a vanilla client. S7
migrates `companions` onto the framework, which is what proves the API against real content rather
than against the harness, and S8 is the consumer-wiring and documentation tail.

## Findings that reshape the work

**The head is the only slot where a vanilla client draws custom geometry, and only for entities whose
renderer carries a `CustomHeadLayer`.** `LivingEntityRenderer` sends the head stack to
`itemModelResolver.updateForLiving(..., ItemDisplayContext.HEAD, ...)` only when
`HumanoidArmorLayer.shouldRender` is false, and clears it otherwise
(`LivingEntityRenderer.java:285-296`); `shouldRender` is true exactly when the stack's `equippable`
component names an asset for that slot (`HumanoidArmorLayer.java:40-47`). So an equippable head item
with *no* asset renders its own item model — vanilla's own instance of this is the carved pumpkin,
equippable to `HEAD` with no `setAsset` call (`Items.java:449-455`). The layer exists only on
`AvatarRenderer`, `HumanoidMobRenderer`, `VillagerRenderer`, `WanderingTraderRenderer`,
`IllagerRenderer`, `PiglinRenderer`, `CopperGolemRenderer` and `ArmorStandRenderer`. Three
consequences: the backend choice is per slot rather than per set; the builder rejects `ITEM_MODEL`
outside `HEAD` at construction, rather than shipping a piece that is silently invisible; and an armor
stand is a legitimate test rig, since it draws the same layer a player does.

**Armor rendering follows the `equippable` component, not item identity, and Polymer copies that
component verbatim.** `HumanoidArmorLayer` reads `DataComponents.EQUIPPABLE` off the stack and
renders whatever asset it names (`HumanoidArmorLayer.java:41,67`). Polymer's copied-component list
includes `EQUIPPABLE` and `DYED_COLOR` (`PolymerItemUtils.java:164,167`), it writes the model from
`getPolymerItemModel` into `ITEM_MODEL` on the client stack (`:421`), and equipment packets go
through the same patcher (`PacketPatcher.java:65-98`). A custom asset therefore renders on a stock
client whatever item the piece is presented as. The fallback is not cosmetic either, though: it is
what a pack-less connection degrades to (next finding), so the framework defaults it per `ArmorType`
— the leather set, wolf armor for `BODY` — and expects a consumer with closer art to override.

**A pack-less client falls back to the item the piece declares — but only because the framework
makes it.** Nothing degrades on its own: `EquipmentAssetManager.get` resolves an unknown asset to
`MISSING`, an empty layer map (`EquipmentAssetManager.java:19,33-34`), so a layer piece on a client
without the pack would otherwise be invisible while still carrying its attributes. Each backend has
its own hook. An `ITEM_MODEL` piece returns `null` from `getPolymerItemModel`, and Polymer then uses
the fallback item's own `item_model` (`PolymerItemUtils.java:407`). A `LAYER` piece has to *replace*
the copied `equippable` rather than drop it — `EQUIPPABLE` is in `COMPONENTS_TO_COPY`
(`PolymerItemUtils.java:164`) and no mixin makes it context-transforming — which is what
`modifyBasePolymerItemStack` (`PolymerItem.java:77`) is for: it runs at `PolymerItemUtils.java:437`,
after the copy loop at `:424`, so the fallback item's own `equippable` set there wins and the client
draws plain leather armor instead of nothing. Both gate on
`PolymerResourcePackUtils.hasMainPack(context)` (`PolymerResourcePackUtils.java:112-124`).

Requiring the pack outright stays the consumer's decision — `markAsRequired` is a statement about a
server's players, not about a piece of armor — so the framework's init helper does not call it and
`companions` keeps its own call (`Main.java:19`).

**`EquipmentClientInfo.LayerType` is client-only, so it cannot appear in the declaration.** The enum
is annotated `@Environment(EnvType.CLIENT)` (`EquipmentClientInfo.java`), and a dedicated server has
no `net.minecraft.client` classes to load it from. The declaration is read by the registration path,
which runs on exactly that server, so layer targets are expressed as rkcore's own `ArmorLayerTarget`
enum mirroring the serialized names (`humanoid`, `humanoid_leggings`, `humanoid_baby`, `wolf_body`,
`horse_body`, `llama_body`, `happy_ghast_body`, the saddles, `wings`), and mapped onto the vanilla
enum inside the datagen subpackage only. This is the same split `common/client` already lives with.

**Vanilla's own asset and model generators cannot be extended.** `EquipmentAssetProvider` bootstraps
a hardcoded list with no hook (`EquipmentAssetProvider.java:29`), and `ItemModelGenerators` exposes
nothing past `run()`. `companions` worked around both by writing raw `DataProvider`s over the public
building blocks — `EquipmentClientInfo.builder` plus `DataProvider.saveAll`
(`SweaterEquipmentProvider.java:38-51`), and `ModelTemplates`/`TextureMapping`/`ItemModelUtils`
(`SweaterModelProvider.java:49-75`). The framework's providers are that code parameterised by the
declaration, not subclasses of anything vanilla, and S7 deletes the originals rather than keeping two
copies.

## How these stages get tested

In-world, on the S1 harness's dev server, watched from the user's client — so every stage that
changes what is drawn gets a plain-language "what you will see" before it runs. Nothing here has a
test suite, and `rkcore` cannot be tested alone: it registers no content, so the harness is a
prerequisite and not a convenience.

Three traps specific to this work:

Dev mode never exercises the relocation. `runServer` in a consumer runs against rkcore's unrelocated
classes off the compile classpath; only installing a real `shadowJar` output does. S8 is where a
built jar goes into a server's `mods/` and the framework is confirmed to work under its shaded name.

The resource pack has to be tested in both answers. A vanilla client that accepts Polymer's pack is
the happy path; a client that declines it is the case where a layer piece goes invisible (third
finding above) and where the item-model backend's behaviour is still unknown. Both get looked at.

The dev client runs Fresh Animations / EMF. A humanoid layer or head model that looks wrong is as
likely to be the animation pack reposing the model as it is to be the asset, so check with the pack
off before chasing the code.

A stage is not finished until its "Done when" is observed. A failure at stage N then implicates N
rather than anything before it.

## S1 — Scratch consumer harness — **planned**

**Build.** Copy `fabric_mod_skeleton/` to `armor-harness/` at the workspace root — outside
`rkc/mods/`, since this is a throwaway and the workspace root is not a repo — with mod id
`armorharness`. Give it companions' consumer arrangement verbatim: the `rkcore`/`shade`
configurations and `rkcoreSharedJar` filter (`companions/build.gradle:38-66`), the
`fabricApi.configureDataGeneration { client = true; createSourceSet = true }` block (`:22-28`), and
`stopServer` so `scripts/devserver.sh` can drive it. It declares one armor set per backend once S2
exists: a four-piece humanoid layer set, a single wolf-body piece, and a head-slot item-model piece.

**Done when.** `scripts/devserver.sh armor-harness start` boots and
`scripts/devserver.sh armor-harness rcon "list"` answers on the harness's own port, with the user
able to join it from their client and Polymer offering its generated pack on join.

## S2 — The armor set declaration — **planned**

**Build.** `common/armor`, server-safe throughout: `ArmorSet` and its `Builder`, `ArmorPiece`,
`ArmorBackend` (`LAYER`, `ITEM_MODEL`) and `ArmorLayerTarget` (fourth finding). A set carries its
namespace and base name, an `ArmorMaterial`'s worth of stats (defense per `ArmorType`, toughness,
knockback resistance, enchantment value, equip sound, repair tag), the `HolderSet<EntityType<?>>` of
allowed wearers, and its pieces. A piece carries its `ArmorType`, backend, the fallback item a
pack-less client sees (defaulted per `ArmorType`, overridable), the layer targets it draws on with a
dyeable flag per layer, and — for `ITEM_MODEL` — the model identifier the consumer authored.

The builder validates at construction, where the message is cheap and unambiguous: `ITEM_MODEL` only
on `HELMET`; at least one layer target on a `LAYER` piece; `BODY` pieces get a non-humanoid target;
no piece declared twice. Durability is the one stat to leave out for now — see Open decisions.

**Done when.** The harness declares all three sets and its `onInitialize` logs them; flipping its
chestplate to `ITEM_MODEL` fails the boot with the builder's own message, visible in
`scripts/devserver.sh armor-harness logs`.

## S3 — Registration and Polymer wiring — **planned**

**Build.** `ArmorRegistration.register(ArmorSet)`, walking the pieces through
`common/item/ItemRegistration` and building each `Item.Properties` the way `WolfSweater` does by hand
(`WolfSweater.java:76-94`): `material.createAttributes(type)`, an `EQUIPPABLE` component built for
the piece's slot with the allowed-entity set and — for a `LAYER` piece only — `setAsset` naming
`ResourceKey.create(EquipmentAssets.ROOT_ID, <ns>:<piece name>)`, plus `UNBREAKABLE` and
`stacksTo(1)`. Each item is a `common/item/PolymerModelItem` subclass over the piece's fallback,
adding the pack-less degradation from the third finding: `getPolymerItemModel` returns `null` and
`modifyBasePolymerItemStack` swaps in the fallback's own `equippable` whenever
`PolymerResourcePackUtils.hasMainPack(context)` is false.

Alongside it `ArmorPolymerSupport.init(modId)`, collecting the boilerplate every consumer otherwise
repeats: `PolymerResourcePackUtils.addModAssets` and
`PolymerItemUtils.addCopiedComponent(DataComponents.UNBREAKABLE)`, the latter being what stops a
vanilla client drawing the fallback's durability bar on an item that has none
(`companions/Main.java:17-23`). Not `markAsRequired`: that is the consuming mod's call, and a
consumer that wants it keeps calling it itself.

**Done when.** On the harness, `/give` of each declared piece yields an item that equips to its slot,
shows its armor points in the inventory, and shows no durability bar. Nothing is visible on the body
yet — that is S4 — and the item is expected to hold the fallback's name until S5 writes lang.

## S4 — Equipment-asset datagen — **planned**

**Build.** `common/armor/datagen/ArmorEquipmentProvider`, taking the declared sets and writing
`assets/<ns>/equipment/<piece>.json` for every `LAYER` piece: `SweaterEquipmentProvider.java:38-51`
generalised, with `ArmorLayerTarget` mapped to `EquipmentClientInfo.LayerType` here and nowhere else.
A dyeable layer uses `Layer.onlyIfDyed` when the piece declares a trim overlay and
`Layer.leatherDyeable` when the whole layer tints; an undyed layer is the plain `Layer` constructor.
Humanoid pieces emit `HUMANOID` plus `HUMANOID_BABY`, and leggings additionally `HUMANOID_LEGGINGS`,
since vanilla picks the inner model by slot (`HumanoidArmorLayer.java:69-71`).

**Done when.** `./gradlew generateAssets` in the harness writes one equipment JSON per layer piece,
and — with S5's textures in place — a player wearing the humanoid set is drawn wearing it, seen from
a second client. Until S5 the asset resolves to a texture that does not exist, so expect the piece to
"nothing" means the asset did not resolve at all, so expect missing-texture rather than nothing
until S5 writes the PNGs — that distinction is the actual check here. A second client that declines
the pack on join wears plain leather armor, not nothing, which is S3's degradation observed for the
first time.

## S5 — Item models, textures and lang — **planned**

**Build.** Three providers in `common/armor/datagen`, all parameterised by the declared sets.
`ArmorModelProvider` writes the item definition and model for each piece:
`SweaterModelProvider.java:49-75` generalised, `ModelTemplates.FLAT_ITEM` for a plain piece and the
`hasComponent(DYED_COLOR)` conditional pair for a dyeable one, while an `ITEM_MODEL` piece gets a
definition pointing at the consumer's authored model and no generated geometry.
`ArmorTextureProvider` writes each layer's PNG to `textures/entity/equipment/<layer target>/<piece>`
— the path `EquipmentClientInfo.Layer.getTextureLocation` builds — plus the item texture, tinting
from supplied base art where the set asks for it. `ArmorLanguageProvider` writes `item.<ns>.<piece>`
from a display name on the set, so pieces stop inheriting the fallback's name.

The tint loop itself (`SweaterTextureProvider.java:71-85`) moves to `common/datagen/PngAssets.tint`
in this stage: it is four lines, both current callers want the same per-channel multiply the client
applies to a dyeable layer, and leaving it in `companions` would mean S7 has nowhere to move it to.

**Done when.** On the harness, the humanoid set is drawn on a player with its own textures, the wolf
piece on a wolf, and every piece shows its own name and icon in the inventory. Dyeing a dyeable piece
recolours only the layer declared dyeable.

## S6 — The head item-model backend — **planned**

**Build.** Nothing new in the item path — S3 already omits `setAsset` for an `ITEM_MODEL` piece,
which is the whole mechanism (first finding). What this stage is actually for is the transform: the
client draws a head item through `CustomHeadLayer.translateToHead`, which translates by `-0.25` on Y,
rotates 180° about Y and scales by `0.625` with Z and Y negated
(`CustomHeadLayer.java:85-89`), on top of the parent model's own head transform. A model authored in
Blockbench does not land right by accident, so this stage establishes the `display.head` block a
consumer's model needs and writes it down in the Reference section — measured on the harness, not
derived.

**Done when.** A head-slot piece is drawn on the harness as its own geometry on a player's head,
correctly seated and facing forward when the player turns, and the same on an armor stand. A client
that declined the pack wears the declared fallback item on its head instead — the model gate from S3,
seen from the other side.

## S7 — Migrate `companions` onto the framework — **planned**

**Build.** Rewrite `WolfSweater.register` as sixteen `ArmorSet` declarations — one per `DyeColor`,
each a single `BODY` piece on the `wolf_body` target with a plain base layer and an
`onlyIfDyed` trim overlay — and delete `SweaterEquipmentProvider`, `SweaterModelProvider` and
`SweaterTextureProvider`, whose content is now the framework's. `SweaterRecipeProvider`,
`SweaterTagProvider` and the sweater's own art stay: recipes, tags and base art are the mod's
business, not the framework's. `WolfSweater`'s public accessors (`name`, `key`, `item`, `assetId`)
are used by the surviving providers, so keep them and point them at the framework's naming.

Add `include 'club/rainbowkitty/rkcore/common/armor/**'` to `companions`' `rkcoreSharedJar` filter
and bump its `rkcore_version` after rkcore publishes.

**Done when.** `./gradlew clean generateAssets build` in `companions` produces the same generated
tree as before the migration — diff it against a copy taken first, and expect zero differences in the
equipment JSONs and textures; any difference is a framework bug, not a licence to update the
expectation. Then the built jar on the harness server: sweaters craft, wear, dye and render as they
do on live today.

## S8 — Consumer wiring, jar verification and docs — **planned**

**Build.** Bump rkcore's `mod_version`, `publishToMavenLocal`, and confirm the framework works under
relocation: install `companions`' unclassified `shadowJar` output on a scratch server with rkcore
*absent*, since dev mode never exercises the rename. Then document — rkcore's README layout list
gains `common/armor`, and the workspace `CLAUDE.md` row for rkcore gains the framework, with the
shared-package list in "Sharing code between mods" extended to say that `common/armor` needs
`common/item` and `common/datagen` shaded alongside it. Docs commit goes ahead of the source commits.

**Done when.** `unzip -l companions-<version>.jar | grep shaded/rkcore/common/armor` lists the
relocated classes, and that jar alone on a server with no rkcore installed yields a wearable,
correctly rendered sweater.

## Open decisions

**Whether the head-model piece graduates into real content.** The harness piece is throwaway by
design. If the head-model backend is wanted for the server, it needs a home — `companions` is the
obvious one, but its scope today is pets rather than player cosmetics. Settled by deciding what the
first real custom helmet is for.

**Durability and repair.** Every piece the framework has a precedent for is unbreakable
(`WolfSweater.java:74-76` deliberately drops vanilla's durability calls). Parameterising durability
means also handling the repair tag, the anvil path and Polymer's fallback durability bar, so S2
leaves it out. Settled by the first set that actually wants to break.

**Armor trims on custom sets.** Vanilla composites a trim over armor layers for items in
`#minecraft:trimmable_armor`. Whether that works over a *custom* equipment asset, and what it costs
in generated assets, is unverified — nothing here depends on it, so it is not a stage. Settled by
trying one trim on the harness once S4 lands.

**Creative tab.** `planked-chests` builds its own through `PolymerCreativeModeTabUtils`
(`ChestBlocks.java:79-87`); `companions` registers no tab at all and its items are reachable only by
command. Whether `ArmorSet` should carry an optional tab is a question about how content is found in
game, not about rendering. Settled alongside the first decision above.

## Reference: how a custom armor piece reaches a vanilla client

The chain, verified end to end for the layer backend and by inspection for the item-model backend:

| Step | Where |
|---|---|
| Item registered with an `equippable` component naming a custom asset | `ArmorRegistration`, after `WolfSweater.java:81-90` |
| Server replaces the item for the client, keeping `equippable` and `dyed_color`, setting `item_model` | `PolymerItemUtils.java:164,167,421` |
| Worn stack reaches the client through the patched equipment packet | `PacketPatcher.java:65-98` |
| Client renders a layer when the component names an asset, else the item model on the head | `HumanoidArmorLayer.java:40-47`, `LivingEntityRenderer.java:285-296` |
| Asset resolved from the server pack; unknown ids resolve to an empty layer map | `EquipmentAssetManager.java:19,33-34` |
| Pack-less connection served the fallback's own model and `equippable` instead | `PolymerItemUtils.java:407,437`, `PolymerItem.java:77` |
| Layer texture read from `textures/entity/equipment/<layer type>/<path>.png` | `EquipmentClientInfo.java`, `Layer.getTextureLocation` |

## Reference: layer targets

`EquipmentClientInfo.LayerType` in 26.2, which `ArmorLayerTarget` mirrors by serialized name:
`humanoid`, `humanoid_leggings`, `humanoid_baby`, `wings`, `wolf_body`, `horse_body`, `llama_body`,
`pig_saddle`, `strider_saddle`, `camel_saddle`, `camel_husk_saddle`, `horse_saddle`, `donkey_saddle`,
`mule_saddle`, `zombie_horse_saddle`, `skeleton_horse_saddle`, `happy_ghast_body`, `nautilus_saddle`,
`nautilus_body`.

A piece's `EquipmentSlot` and its layer targets are independent: the slot decides where it equips and
which attributes apply, the targets decide which entity models draw it. A `BODY` piece on `wolf_body`
and a `CHEST` piece on `humanoid` are the two shapes this framework is built around; the saddle
targets are reachable but untried.
