# Custom armor models

A `common/armor` package in `kalic0re` that sibling mods shade, turning one declaration of an armor set
into its registered items, the Polymer wiring that makes them visible to vanilla clients, and the
datagen that writes their equipment assets, item models and textures. Each slot picks its own render
backend: a vanilla-shaped **layer** (a custom equipment asset — the path `companions`' wolf sweaters
already take) or a real **item model** on the head, the one slot where a vanilla client will draw
arbitrary geometry. Wearers are not limited to players: a set names the layer targets it draws on, so
wolves, horses, llamas and happy ghasts are declarable the same way humanoids are.

Everything below about vanilla's render path and Polymer's item handling was read out of
`libs/decompiledjava/minecraft-26.2` and `libs/polymer` (branch `dev/26.2`), and out of the working
sweater implementation under `rkc/mods/companions`; it is cited by file and line. Every stage is now
`done`: the framework is built, both backends are confirmed in world, and `companions` runs on it.

S1 was the harness, and came first because `kalic0re` registers no content of its own and nothing here
could be seen without a consumer. S2 is the declaration every later stage compiles against. S3 makes
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
which runs on exactly that server, so layer targets are expressed as kalic0re's own `ArmorLayerTarget`
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
test suite, and `kalic0re` cannot be tested alone: it registers no content, so the harness is a
prerequisite and not a convenience.

Three traps specific to this work:

Dev mode never exercises the relocation. `runServer` in a consumer runs against kalic0re's unrelocated
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

## S1 — Scratch consumer harness — **done**

**Build.** Copy `fabric_mod_skeleton/` to `armor-harness/` at the workspace root — outside
`rkc/mods/`, since this is a throwaway and the workspace root is not a repo — with mod id
`armorharness`. Give it companions' consumer arrangement verbatim: the `kalic0re`/`shade`
configurations and `kalic0reSharedJar` filter (`companions/build.gradle:38-66`), the
`fabricApi.configureDataGeneration { client = true; createSourceSet = true }` block (`:22-28`), and
`stopServer` so `scripts/devserver.sh` can drive it. It declares one armor set per backend once S2
exists: a four-piece humanoid layer set, a single wolf-body piece, and a head-slot item-model piece.

**Done when.** `scripts/devserver.sh armor-harness start` boots and
`scripts/devserver.sh armor-harness rcon "list"` answers on the harness's own port, with the user
able to join it from their client and Polymer offering its generated pack on join.

### As built

The skeleton already carried more than the Build text assumed: the `fabricApi` datagen block with
`client = true` is in `fabric_mod_skeleton/build.gradle` as shipped, and `gradle/devserver.gradle`
brings `runServer`/`stopServer` plus first-run seeding of `run/` (EULA, superflat world, ops, rcon).
So the only thing that had to be added was companions' shade arrangement; what had to be *removed*
was more: bil and `fabric-permissions-api` with their Modrinth and Sponge repos, and the example
block's `content/`/`polymer/` packages and assets. `mavenLocal()` stayed, now for kalic0re rather than
for bil. Ports are 25605 game, 25582 rcon — mob-overhaul already holds 25599 and sensible-storage
25604.

The `kalic0reSharedJar` filter lists `common/armor/**` from the start, matching nothing until S2.
`common/item` and `common/datagen` do match, and the built `armorharness-0.1.0.jar` carries them as
`club/rainbowkitty/armorharness/shaded/kalic0re/{item,datagen}/*.class`, so the relocation is known
good before any framework code exists.

One thing the harness gets for free that the testing section had assumed would need setting up:
`run/config/polymer/auto-host.json` is seeded `enabled: true` with `required: false`, so the pack is
offered and declinable on join. Testing the pack-less path in the third finding is a matter of
clicking Decline, with no config change and no second install.

Boot is 0.37s to `Done`, rcon answers, and the entrypoint logs that no sets are declared yet.

## S2 — The armor set declaration — **done**

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

### As built

**One equipment asset per set, not per piece — this changed S3, S4 and S5.** Vanilla gives a whole
material a single asset (`minecraft:iron` and the rest, `EquipmentAssets.java:11-27`) and separates
the slots by layer *target*, not by asset: a helmet, chestplate and boots all read
`textures/entity/equipment/humanoid/<name>.png` while leggings read the `humanoid_leggings/` copy of
the same id (`EquipmentClientInfo.java`, `Layer.getTextureLocation`). Per-piece assets would have
produced four assets and four identical humanoid PNGs. So the asset key is `ArmorSet.assetKey()` at
`<ns>:<set>`, pieces contribute layer targets, and `ArmorSet.layers()` merges them; two pieces
declaring one target with different layers is rejected at construction, since only one of the two
could ever be written. The affected Build texts have been edited to say so.

The baby target had to be a default rather than an option: vanilla routes *every* slot on a baby
through `HUMANOID_BABY` (`HumanoidArmorLayer.java:69-71`), and the 26.2 jar ships 10 textures under
`humanoid_baby/` against 10 under `humanoid/`, so a set that omits it renders babies untextured
rather than unarmoured. A humanoid piece therefore defaults to its outer target plus
`HUMANOID_BABY`, and a four-piece set merges to exactly vanilla's three.

Two things went in beyond the Build text. `ArmorSet.equippableExtras` is a
`Consumer<Equippable.Builder>` applied after the framework's own calls, because an animal piece
needs `setEquipOnInteract`/`setCanBeSheared` (`Equippable.java:235,240`) and re-declaring that
surface field by field would have been a second, worse copy of it. And a `BODY` piece defaults its
registry path to the bare set name rather than `<set>_body`, which is what lets companions' sixteen
`<colour>_wolf_sweater` items keep their current ids in S7.

Validation is in the records' compact constructors rather than the builder, so a hand-built
`ArmorPiece` cannot dodge it: `ITEM_MODEL` outside the head slot, a `LAYER` piece with no layers or
with a model, a `BODY` piece on a humanoid target and the converse, a target declared with no
layers, two pieces in one slot, and the merge conflict above. The harness's chestplate flip failed
the boot with `ArmorBackend.ITEM_MODEL is only drawn in the head slot, got CHESTPLATE`.

## S3 — Registration and Polymer wiring — **done**

**Build.** `ArmorRegistration.register(ArmorSet)`, walking the pieces through
`common/item/ItemRegistration` and building each `Item.Properties` the way `WolfSweater` does by hand
(`WolfSweater.java:76-94`): `material.createAttributes(type)`, an `EQUIPPABLE` component built for
the piece's slot with the allowed-entity set and — for a `LAYER` piece only — `setAsset` naming the
set's own `ArmorSet.assetKey()`, plus the set's `equippableExtras`, `UNBREAKABLE` and
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

### As built

As planned, in three classes: `ArmorRegistration`, `PolymerArmorItem` and `ArmorPolymerSupport`. The
one thing the Build text left implicit is that an `ITEM_MODEL` piece still needs an `ArmorMaterial`
to get its attributes from, and that record has no optional asset — so the material is built per set
with the set's asset whatever its pieces do, and only `createAttributes` is ever read off it.

The degradation was observed from both sides on the harness. With the pack declined, every piece
wears as its fallback — leather for the humanoid set, wolf armour for the body piece. With the pack
accepted, the pieces wear as nothing at all, which is the empty layer map from the third finding and
stays that way until S4 writes the asset. Tooltips carry the declared armour points and no
durability bar in both cases, so the attribute and unbreakable paths do not depend on the pack.

Registration is observable without a client: the six ids parse over rcon while a made-up one comes
back `Unknown item`.

## S4 — Equipment-asset datagen — **done**

**Build.** `common/armor/datagen/ArmorEquipmentProvider`, taking the declared sets and writing one
`assets/<ns>/equipment/<set>.json` per set with a `LAYER` piece, straight off `ArmorSet.layers()`:
`SweaterEquipmentProvider.java:38-51` generalised, with `ArmorLayerTarget` mapped to
`EquipmentClientInfo.LayerType` here and nowhere else. `ArmorLayer.Dye` maps just as directly —
`ONLY_IF_DYED` to `Layer.onlyIfDyed`, `TINTED` to `Layer.leatherDyeable`, `NONE` to the plain
`Layer` constructor.

**Done when.** `./gradlew generateAssets` in the harness writes one equipment JSON per set, carrying
the three humanoid targets for `ranger` and `wolf_body` for `wolf_coat`, and — with S5's textures in
place — a player wearing the humanoid set is drawn wearing it, seen from a second client. Before S5
the asset resolves to textures that do not exist, so expect missing-texture rather than nothing:
"nothing" would mean the asset itself did not resolve, which is a different bug. A second client that
declines the pack on join wears plain leather armor, not nothing, which is S3's degradation observed
for the first time.

### As built

As planned. `ArmorEquipmentProvider` writes `ranger.json` with its three humanoid targets and
`wolf_coat.json` with `wolf_body` carrying a plain base and a `dyeable: {}` trim; `top_hat` is
skipped for having no layer piece, which is `hasLayerPieces()` doing its job. In world, with the pack
accepted, all four humanoid pieces and the wolf coat draw as missing-texture rather than as nothing,
and a declined pack still wears leather and wolf armour.

`ArmorLayerTarget` is paired to the vanilla enum by serialized name at class-init, and a target with
no counterpart throws there — so a vanilla rename fails datagen with a message naming the target
instead of silently dropping a layer at render time.

Two consumer-wiring details for S8 to write down, both found here rather than designed:
`pack.addProvider` needs its lambda parameter typed `(FabricPackOutput output)`, because
`DataGenerator.PackGenerator` carries an `addProvider` overload of its own that otherwise captures an
untyped lambda and hands it a plain `PackOutput`; and datagen leaves a `.cache/` directory beside its
output that needs `exclude '.cache/**'` in `processResources`, which `companions` already does and
the skeleton does not.

## S5 — Item models, textures and lang — **done**

**Build.** Three providers in `common/armor/datagen`, all parameterised by the declared sets.
`ArmorModelProvider` writes the item definition and model for each piece:
`SweaterModelProvider.java:49-75` generalised, `ModelTemplates.FLAT_ITEM` for a plain piece and the
`hasComponent(DYED_COLOR)` conditional pair for a dyeable one, while an `ITEM_MODEL` piece gets a
definition pointing at the consumer's authored model and no generated geometry.
`ArmorTextureProvider` writes each layer's PNG to `textures/entity/equipment/<layer target>/<set>`
— the path `EquipmentClientInfo.Layer.getTextureLocation` builds, one image per target rather than
per piece — plus each piece's item texture, tinting
from supplied base art where the set asks for it. `ArmorLanguageProvider` writes `item.<ns>.<piece>`
from a display name on the set, so pieces stop inheriting the fallback's name.

The tint loop itself (`SweaterTextureProvider.java:71-85`) moves to `common/datagen/PngAssets.tint`
in this stage: it is four lines, both current callers want the same per-channel multiply the client
applies to a dyeable layer, and leaving it in `companions` would mean S7 has nowhere to move it to.

**Done when.** On the harness, the humanoid set is drawn on a player with its own textures, the wolf
piece on a wolf, and every piece shows its own name and icon in the inventory. Dyeing a dyeable piece
recolours only the layer declared dyeable.

### As built

Four providers, not three. **Declaring a dyeable layer does not make a piece dyeable**, which only
surfaced when the wolf coat could not be dyed in world: 26.2 has no tag-driven dye recipe, only the
ordinary `minecraft:crafting_dye` datapack recipe naming its target item explicitly
(`DyeRecipe.java:20-46`), so a piece can respond to `dyed_color` while nothing can ever put that
component on it. `ArmorDyeRecipeProvider` closes that, emitting one recipe per piece that has a
dyeable layer — the same shape `SweaterRecipeProvider` writes by hand today, so S7 keeps working.
Recipes otherwise stay the consuming mod's business; this one is part of the declaration.

`ArmorModelProvider` derives the dyed item icon rather than taking it as a declaration: a set with an
`ONLY_IF_DYED` layer gets the conditional `has_component: dyed_color` pair, with the overlay icon
read as `<namespace>:item/<that layer's texture path>`. The generated definitions match companions'
existing output shape exactly, which is what S7's diff depends on. A `TINTED` layer maps to a
dye-tinted single-layer icon, which is code no in-world test has exercised — the harness and
companions both use `NONE` plus `ONLY_IF_DYED`.

`ArmorTextureProvider` takes its art through a separate {@code ArmorArt} record rather than through
`ArmorSet`, keeping classpath paths to source art out of a declaration the server reads. Sets with
hand-drawn textures skip it entirely; it exists for families that differ only in colour.
`ArmorLanguageProvider` title-cases registry paths, which gives companions' "Light Blue Wolf Sweater"
with nothing declared.

One last piece of S2 was settled here: `piece.itemModel()` is the *geometry* id, and every piece's
item definition is generated at its own id, so Polymer always points a client at
`<namespace>:<piece path>` whichever backend the piece uses.

**Villagers cannot wear layer armour, and nothing server-side can change that.** `VillagerRenderer`
carries a head layer, a profession layer and a crossed-arms item layer, and no `HumanoidArmorLayer`
— so a villager draws no armour at all, custom or vanilla. A baby zombie in the same set renders
correctly from `humanoid_baby/ranger.png`. Reaching villagers would take the display-entity rig this
framework deliberately does not have.

## S6 — The head item-model backend — **done**

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

### As built

The item path needed nothing, as expected: omitting `setAsset` is the whole mechanism. Calibration
took one round. A model with no `display.head` transform lands centred on the *middle* of the head,
not on top of it, so the harness's hat sat sunk into the skull with its crown poking out — which
matched the prediction from the transform arithmetic closely enough to fix in one edit. The mapping
this produced is in "Reference: putting a model on a head"; `display` translations turned out to be
parsed in model pixels and applied inside the scale the client has already imposed
(`ItemTransform.java:60-62`), which is what makes the lift a single number.

The pack-less half was already settled by S3 and only needed confirming: a declined pack draws the
fallback item's armour rather than an unresolvable model, so the question the plan carried as
unverified never had to be answered on its own terms.

Villagers take a head model even though they refuse layer armour (S5's finding), since
`VillagerRenderer` carries a `CustomHeadLayer` and no armour layer. Worth knowing before choosing a
backend for a set meant to be worn by something other than a player.

## S7 — Migrate `companions` onto the framework — **done**

**Build.** Rewrite `WolfSweater.register` as sixteen `ArmorSet` declarations — one per `DyeColor`,
each a single `BODY` piece on the `wolf_body` target with a plain base layer and an
`onlyIfDyed` trim overlay — and delete `SweaterEquipmentProvider`, `SweaterModelProvider` and
`SweaterTextureProvider`, whose content is now the framework's. `SweaterRecipeProvider`,
`SweaterTagProvider` and the sweater's own art stay: recipes, tags and base art are the mod's
business, not the framework's. `WolfSweater`'s public accessors (`name`, `key`, `item`, `assetId`)
are used by the surviving providers, so keep them and point them at the framework's naming.

Add `include 'club/rainbowkitty/kalic0re/common/armor/**'` to `companions`' `kalic0reSharedJar` filter
and bump its `kalic0re_version` after kalic0re publishes.

**Done when.** `./gradlew clean generateAssets build` in `companions` produces the same generated
tree as before the migration — diff it against a copy taken first, and expect zero differences in the
equipment JSONs and textures; any difference is a framework bug, not a licence to update the
expectation. Then the built jar on the harness server: sweaters craft, wear, dye and render as they
do on live today.

### As built

`diff -r` against the pre-migration snapshot reported **zero differences** across all 162 generated
files — equipment assets, item definitions, models, the 32 tinted textures, lang, recipes and
advancements alike. Four providers were deleted rather than the three the Build text names: lang
generalised as cleanly as the rest, so `SweaterLanguageProvider` went too, and the framework's
title-casing reproduces "Light Blue Wolf Sweater" with nothing declared.

`SweaterRecipeProvider` kept its wool recipe and lost its dye recipe to `ArmorDyeRecipeProvider`;
that swap is inside the zero-diff, so the framework's recipe is byte-identical to the hand-written
one it replaced. `companions` also picked up the pack-less fallback for free — a declined pack now
wears vanilla wolf armour where it used to show nothing, confirmed in world on a wolf.

**A wild wolf refuses a sweater, and always did.** `Wolf.mobInteract` gates body armour on
`isOwnedBy(player)` (`Wolf.java:461`), so an untamed wolf rejects sweaters exactly as it rejects
vanilla wolf armour — this surfaced during testing and is not a migration regression, since
`Item.java:558-573` shows vanilla's own `wolfArmor()` properties omit `setEquipOnInteract` too. The
harness's `wolf_coat` sets that flag and so routes through `Equippable.equipOnTarget`, which checks
neither ownership nor age; that difference is a set's to make, and `companions` stays with vanilla's.

`markAsRequired` was dropped from `companions` on the way through, deliberately: with the fallback in
place a pack-less player gets usable wolf armour rather than an invisible item, so requiring the pack
is now a question about appearance alone.

## S8 — Consumer wiring, jar verification and docs — **done**

**Build.** Bump kalic0re's `mod_version`, `publishToMavenLocal`, and confirm the framework works under
relocation: install `companions`' unclassified `shadowJar` output on a scratch server with kalic0re
*absent*, since dev mode never exercises the rename. Then document — kalic0re's README layout list
gains `common/armor`, and the workspace `CLAUDE.md` row for kalic0re gains the framework, with the
shared-package list in "Sharing code between mods" extended to say that `common/armor` needs
`common/item` and `common/datagen` shaded alongside it. Docs commit goes ahead of the source commits.

**Done when.** `unzip -l companions-<version>.jar | grep shaded/kalic0re/common/armor` lists the
relocated classes, and that jar alone on a server with no kalic0re installed yields a wearable,
correctly rendered sweater.

### As built

kalic0re is at 0.3.0 and `companions` pins it; its generated tree stayed byte-identical across the
bump. The relocation was proven from the bytecode rather than from an environment: `WolfSweater` and
`Main` reference only `club/rainbowkitty/companions/shaded/kalic0re/...`, and no class in the jar names
the unrelocated package at all. That is a stronger result than the planned clean-server run, which
would only have shown that nothing *happened* to need kalic0re — the installed jar cannot reach
kalic0re's classes even when they are on the classpath, which is the case on the harness server where
the sweaters were confirmed working.

Documentation landed in three places rather than two: kalic0re's README layout list, the workspace
`CLAUDE.md` kalic0re and companions rows, and a new paragraph in "Sharing code between mods" carrying
the two consumer-wiring traps from S4 — the typed `pack.addProvider` lambda and the `.cache/`
exclusion — since both cost a build cycle to rediscover.

## Cleanup pass — **done**

A simplification run over the finished framework, after every stage had landed.

**What came out.** `ArmorStats.defenseFor` had no caller anywhere — the `defense` map itself is what
`ArmorMaterial` reads. `ArmorLanguageProvider`'s hand-rolled title-caser became a three-line stream.
`ArmorModelProvider` carried `plainModel` as an alias for `icon`, two names for one value.
`ArmorTextureProvider` spelled `java.nio.file.Path` out in a signature and rebuilt a `PathProvider`
once per set though it never varies. `ArmorEquipmentProvider`'s layer-type pairing ran a nested loop
past its own match and then re-checked with `containsKey`.

**One latent bug, not a style point.** `ArmorSet.layers()` returned `Map.copyOf(...)` of an
enum-keyed map. `Map.copyOf` probes on `hashCode`, and an enum's is identity-based, so its iteration
order varies *between JVM runs*. `ArmorModelProvider.dyedOverlay` picks a set's dyed overlay with
`findFirst` over exactly that map, which would have made generated output depend on the run for any
set declaring a dyed layer on more than one target. Nothing shipped hits it — every real set has one
layer target — and the equipment asset itself was never at risk, since `EquipmentClientInfo.Builder`
keys into an `EnumMap` of its own. Merging into an `EnumMap` fixes it at the source and makes the
merged view ordinal-ordered by construction rather than by what vanilla happens to normalise
downstream.

**`ArmorSet.Builder.allowedWearers`** was calling the deprecated `EntityType::builtInRegistryHolder`
— an intrusive holder — which was the one deprecation warning in kalic0re's build. Now
`BuiltInRegistries.ENTITY_TYPE::wrapAsHolder`, the same route `companions` already takes for sounds.

**What went in: `ArmorDatagen`.** Every consumer was repeating the same five `pack.addProvider`
calls, each carrying the `(FabricPackOutput output)` type annotation that S4 cost a build cycle to
discover, and one of them repeated the explanatory comment verbatim. `ArmorDatagen.addProviders(pack,
sets, art)` is now the whole armour half of a consumer's datagen entrypoint, and the trap lives in
one place a consumer never reads. `companions`' entrypoint lost fourteen lines to it; the generated
tree stayed byte-identical across all 162 files.

kalic0re went to 0.4.0 for this — `defenseFor` coming out makes it a breaking change, however dead it
was.

## Open decisions

**Whether the head-model piece graduates into real content — not yet.** `armor-harness` was
refactored into `rkc/mods/artifacts`, a real mod for player cosmetics; `companions` was the wrong
home, its scope being pets. But the harness's three demo sets did not survive the move. `artifacts`
ships one item, `talaria`, which is a LAYER piece, so **the `ITEM_MODEL` backend now has no consumer
anywhere in the workspace.** It is built, documented and was confirmed in world in S6, and nothing
exercises it today. The first hat or mask that wants real geometry picks it back up; until then,
treat S6's result as the last time that path was known to work.

Two other consequences. Talaria's textures are vanilla gold armour tinted bronze — placeholder, with
the tint load-bearing rather than decorative, since the piece falls back to golden boots and untinted
gold art would render identically with and without the pack. And with the harness gone there is no
throwaway consumer left to try a framework change against: the next one goes on `artifacts` or on a
new scratch copy.

**Durability and repair.** Every piece the framework has a precedent for is unbreakable
(`WolfSweater.java:74-76` deliberately drops vanilla's durability calls). Parameterising durability
means also handling the repair tag, the anvil path and Polymer's fallback durability bar, so S2
leaves it out. Settled by the first set that actually wants to break.

**Armor trims on custom sets.** Vanilla composites a trim over armor layers for items in
`#minecraft:trimmable_armor`. Whether that works over a *custom* equipment asset, and what it costs
in generated assets, is unverified — nothing here depends on it, so it is not a stage. Settled by
trying one trim on the harness once S4 lands.

**Creative tab.** `sensible-storage` builds its own through `PolymerCreativeModeTabUtils`
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

## Reference: putting a model on a head

Measured on the harness, and the arithmetic behind it read out of `CustomHeadLayer.java:85-89` and
`ItemTransform.java:60-62`.

The client draws a head item by lifting a quarter block, turning 180° and scaling by 0.625, all
before the model's own `display.head` transform applies. Two consequences, which are what an author
actually needs:

| Quantity | Value |
|---|---|
| Where model-space (8, 8, 8) lands | the middle of the head, not the top |
| One model unit, on screen | 0.625 pixels |
| Head cube, in model units | 12.8 wide and 12.8 tall, so its top is 6.4 units above centre |
| Lift to rest geometry on the skull | `display.head.translation` of `[0, 6.4, 0]` |

`display` translations are in model pixels, clamped to ±5 blocks, and applied before the rotation
and scale in the same transform — so the 0.625 already applies to them and no compensation is
needed.

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
