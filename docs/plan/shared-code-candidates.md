# Shared code candidates from planked-chests

Grows `rkcore`'s `common/` shared surface with six patterns that `planked-chests` currently carries
alone, after first repairing the branch state that makes `publishToMavenLocal` unsafe today.

Everything below was read out of the working trees this session and is cited by file and line. The
vanilla signatures in S2 were checked with `javap`/`unzip -l` against
`minecraft-merged-deobf-26.2.jar`; the class *existence* claims for `MultiVariantGenerator` and
`BlockModelDefinitionGenerator` are from the jar index only — their constructors and builder
shapes are **not** verified and S2 has to read them before committing to that route. Claims quoted
from existing Javadoc (vanilla's atlas merge in S7, `ChestLidController`'s ramp rate in S4) were
verified when that code was written, not re-verified here. S1 has been built and verified; every
stage after it is `planned` and unrun.

S1 is the blocker and has nothing to do with the extractions — it repairs a split-branch state in
which one innocent Gradle command breaks a sibling mod's compile. Do it first; no other stage's
"Done when" is trustworthy until `rkcore`'s published jar is reproducible from a branch.

S2 runs before S3 and S6, because S2 decides whether `planked-chests`' datagen keeps hand-writing
JSON at all, and that answer determines how much of `ElementModels` (S3) and the blockstate emitter
(S6) still exists to extract. S4, S5 and S7 are independent of everything and of each other. S2–S4
are the substance; S5–S7 are small and can be dropped without losing the point of the exercise.

## Findings that reshape the work

**The cargo-minecart framework was already extracted, and `planked-chests` already depends on it,
but it lives on an unmerged branch — so the only thing making `planked-chests` compile is an
untracked `~/.m2` jar.** `rkcore`'s `cargo-minecarts` branch head `fbb0af1` adds seven classes under
`common/vehicle` (1027 lines). `planked-chests@main` imports four of them —
`cart/ChestCarts.java:34`, `cart/PlankedChestMinecart.java:30-31`, `cart/ChestMinecartItem.java:19`,
`cart/ChestCartDisplay.java:10`, `cart/ShulkerCartDisplay.java:16` — and `build.gradle:56` lists
`include 'club/rainbowkitty/rkcore/common/vehicle/**'`. Neither `main` nor the checked-out
`custom-armor-models` contains that package; the checkout's `common/vehicle/` is an empty directory
left behind by a branch switch. All three `~/.m2` jars (0.2.0, 0.3.0, 0.4.0) do contain the seven
classes, so they were published from a tree that had them. `rkcore/gradle.properties:18` declares
`mod_version = 0.4.0`, which means `./gradlew publishToMavenLocal` from the current checkout
overwrites the 0.4.0 jar `companions/gradle.properties:56` depends on, silently stripping
`common/vehicle` and breaking `planked-chests`' compile. This is S1.

**The three branches are a deliberate untangle, and `backup/pre-untangle` is the tree the published
jars came from.** `main@0127d9b` is an ancestor of `custom-armor-models@2fb507f`, and
`cargo-minecarts@fbb0af1`'s merge-base with `custom-armor-models` is also `0127d9b` — so the two
feature branches are siblings forked from the same point on `main`. `backup/pre-untangle@6189814`
forks earlier (`46f3e04`) and carries **both** the armor work and `common/vehicle`, and its eight
commits are a patch-for-patch mirror of `custom-armor-models`' eight. So one tangled branch was
split in two, and `mod_version = 0.4.0` on both `custom-armor-models` and `backup/pre-untangle`
identifies the latter as what produced the published 0.4.0 jar. Merging the two siblings
reconstitutes that content from real branches.

**`main`'s 6-ahead/6-behind divergence from `origin/main` is pure noise and can be discarded.** All
six commit pairs have identical `git patch-id --stable` values in the same order, `git diff main
origin/main` is empty, and author dates match to the second; only the committer timestamps differ
(`0127d9b` at 15:16 vs `fef1694` at 02:18 on 2026-09-10). A local rebase that changed no content.
This removes what looks like the riskiest part of S1.

**Half of what looks extractable from `planked-chests`' datagen is not a missing abstraction but a
vanilla API that `rkcore` already uses.** `ArmorModelProvider` builds item definitions and generated
models through `ClientItem`/`ClientItem.CODEC` (`:68-69,76`), `ItemModelUtils.plainModel` (`:100`),
`ModelTemplates.FLAT_ITEM` + `TextureMapping.layer0` (`:105`), and saves with
`DataProvider.saveAll` (`:74-76`); `ArmorEquipmentProvider:62` does the same with
`EquipmentClientInfo.CODEC`. `planked-chests` hand-writes all three shapes as raw `JsonObject` —
`ElementModels.itemDefinition:168`, `ElementModels.variantModel:156`, `CartIcons.generatedModel:87`,
`ChestBlockStateProvider.variants:59` — and hand-rolls the save loop seven times. So S2 is a
convergence, not a new base class. **But it is not free:** `companions/build.gradle:25` runs datagen
with `client = true` and `planked-chests/build.gradle` deliberately runs `client = false`, giving
raw JSON as the reason in its own comment. The typed builders live under
`net.minecraft.client.data.models.*` and `net.minecraft.client.renderer.item`, so S2 must establish
whether those *load* without a booted client before it can proceed.

## How these stages get tested

S1 is tested with git alone — branch topology, `git diff` against the reconstituted tree, and a
`unzip -l` of the republished jar showing all shared packages present — plus a clean
`./gradlew build` in each of `rkcore`, `planked-chests` and `companions`.

S2–S7 are all datagen or asset changes, so the governing test is **byte-comparison of generated
output before and after**: capture `src/main/generated` (and `companions`' equivalent), apply the
stage, regenerate with `./gradlew clean generateAssets`, and diff. A stage that changes generated
bytes has either found a real bug or broken something, and must say which. Behaviour beyond the
files is checked on the `planked-chests` dev server via `scripts/devserver.sh
rkc/mods/planked-chests restart` — a placed chest still animating its lid, a cart still showing its
cargo, zero mixin errors.

Because every consumer builds against a `mavenLocal` jar, each stage that changes a shared package
ends with `cd rkc/mods/rkcore && ./gradlew publishToMavenLocal` and a rebuild of **both** consumers,
not just the one that motivated the change.

A stage is not finished until its "Done when" is observed. A failure at stage N implicates stage N
and not the ones before it.

## S1 — Reunite the split rkcore branches — **done**

**Build.** Discard `main`'s content-free divergence (`git reset --hard origin/main`, safe per the
patch-id finding). Then bring `custom-armor-models` and `cargo-minecarts` back together into one
line of development so that a single branch carries both `common/armor` and `common/vehicle`, as
`backup/pre-untangle` does. Keep `backup/pre-untangle` untouched as the reference tree until the
merged result is confirmed to match it. Republish to `mavenLocal`, and lift
`planked-chests/gradle.properties:21` off its stale `rkcore_version=0.2.0` pin onto whatever the
merged branch declares.

**Done when.** One branch's `git ls-tree -r --name-only` lists both
`common/armor/datagen/ArmorDatagen.java` and all seven `common/vehicle/*.java`; `git diff` between
that branch and `backup/pre-untangle` shows only the intended differences; a freshly published
`~/.m2` jar's `unzip -l` lists `common/{armor,client,datagen,item,label,mixin,net,rule,vehicle}`;
and `./gradlew build` exits 0 in all three mods with both consumers pinned to that version.

`backup/pre-untangle` should not be deleted as part of this stage; it is the only witness to the
pre-split tree.

There is also an uncommitted Javadoc-only edit on
`common/net/PolymerClientHandshake.java` — the caveat that handshake key parity does not imply
registry-id parity, because Fabric's entrypoint order is hash-shuffled per launch. Commit it before
any branch surgery so it cannot be lost to a reset.

### As built

**Rebase, not merge, and the choice was evidence rather than preference.** All five first-party
mod repos have zero merge commits across 40 commits of history (`git log --merges` on each), so
the house convention settled what the Build text had left to the caller. That removed the
merge-or-rebase entry from Open decisions rather than resolving it by taste.

**`main`'s divergence was discarded by moving the branch, not by force-pushing later.** `git
branch -f main origin/main` threw away the six locally-rewritten commits, and both feature
branches were then replayed on top with `git rebase --onto`. Because the rewritten commits' trees
were identical to `origin/main`'s, nothing was lost, and `main` can now be pushed as a
fast-forward — no force-push enters the picture at all. Worth preferring over `reset --hard` on a
checked-out branch generally: the working tree was on `custom-armor-models` throughout.

**The Build text understated the work: `custom-armor-models` carried 17 commits past the base, not
the 8 the initial survey showed.** The eight-commit figure came from a `git log --graph` truncated
at `-20`, and the mirror against `backup/pre-untangle` looked complete because both halves were
cut at the same depth. `git rev-list --count` is the check that would have caught it. All 17
replayed with zero conflicts, as did `cargo-minecarts`' single commit — unsurprising once `git
diff --name-only` against the shared base showed the two siblings touch entirely disjoint file
sets.

**Verified after the reunion.** `main` is 18 commits past `origin/main` at `23f06a8`, carrying all
nine `common/` packages and `mod_version = 0.4.0`. `git diff backup/pre-untangle main` shows exactly
three files — this plan doc (new), the handshake Javadoc caveat (new), and the post-split head-model
doc correction — so every line of shared *source* is identical to the tree that published the 0.4.0
jar. The republished jar carries 7 `common/vehicle` classes and all nine packages.
`planked-chests/gradle.properties:21` moved from `rkcore_version=0.2.0` to `0.4.0`, and
`./gradlew build` exits 0 in `rkcore`, `planked-chests` and `companions`.

**The reunited branch cannot be pushed: `rkcore`'s remote no longer exists.** `git push origin
main` fails with "Repository not found" while `planked-chests` pushes fine over an identical
`ssh://git@github.com/RainbowKittyClub/...` URL, so it is the repository and not the credentials.
`gh api repos/RainbowKittyClub/rkcore` returns a bare 404 with no rename redirect, and a GitHub
rename always leaves one — so the repo was deleted rather than renamed.
`RainbowKittyClub/Kalic0re`, described as the "java library used by all RKC/Kalic0 mods", was
created empty at 2026-09-12T03:39:19Z and pushed once a second later; fetching it read-only shows a
single unrelated "Initial commit" sharing no history with this checkout. It is the evident successor
but holds none of this work yet. See Open decisions.

**Cleanup state.** `custom-armor-models` and `cargo-minecarts` are deleted, both being fully
subsumed by `main`. The three `prereunion/*` tags are kept for now: they were to go once the push
landed, and it has not. They mark nothing unique — `main` carries all 18 commits and
`backup/pre-untangle` remains — so they can be dropped with one command whenever the remote question
is settled.

## S2 — Converge planked-chests datagen on the typed vanilla API — **planned**

**Build.** First resolve the unknown: write a throwaway provider on `planked-chests`' existing
`client = false` datagen source set that touches `ClientItem.CODEC` and `ModelTemplates.FLAT_ITEM`,
and run `./gradlew runDatagen`. If those classes load, flip nothing; if they need a client instance,
either move `planked-chests` to `client = true` or record that raw JSON stays and stop this stage
after the `saveAll` half.

Then, in dependency order: replace the seven hand-rolled save loops with
`DataProvider.saveAll(cache, Codec|Function<E,JsonElement>, Function<T,Path>, Map<T,E>)` —
verified present on `net.minecraft.data.DataProvider` this session. The sites are
`ChestBlockStateProvider:53-54`, `ChestElementModelProvider:59-60`,
`ShulkerElementModelProvider:69-70`, `ChestMinecartTextureProvider:89-90`,
`CargoCartTextureProvider:93-94`, `ChestTextureProvider:102`, `ShulkerAtlasProvider:63`. Then delete
`ElementModels.itemDefinition:168`, `ElementModels.variantModel:156` and
`CartIcons.generatedModel:87` in favour of `ClientItem`, `ItemModelUtils.plainModel` and
`ModelTemplates.FLAT_ITEM` + `TextureMapping.layer0`, matching `ArmorModelProvider:100-124`.

**Done when.** `./gradlew clean generateAssets` regenerates `src/main/generated` byte-identically to
the captured baseline, and `./gradlew build` exits 0. A byte difference is a finding to explain in
`As built`, not a result to accept: the typed builders may order keys differently from the
hand-written objects, in which case the diff has to be shown to be key order only.

The `ElementModels.model:142` emitter (a `particle` texture plus an `elements[]` array) has no
typed equivalent, because nothing in vanilla's builders emits arbitrary cuboids. It stays, and is
what S3 extracts.

## S3 — Extract the entity-model box transcription — **planned**

**Build.** Move `ElementModels`' geometry half into `rkcore` as `common/datagen/EntityModelBoxes`:
the two `box` overloads (`:39`, `:59`), `invertedTwin:122`, `model:142` and `UV_SCALE:25`. Leave
behind whatever S2 deleted. `common/datagen/**` is already shaded by both consumers
(`planked-chests/build.gradle:53`, `companions/build.gradle:52`), so no build wiring changes.

**Done when.** `ChestElementModelProvider` and `ShulkerElementModelProvider` both compile against
the shaded copy, `./gradlew clean generateAssets` regenerates byte-identical output, and a placed
chest and a cart-borne shulker box both still render correctly on the dev server — the second half
matters because the UV winding bugs this code encodes are invisible in a JSON diff and obvious
in-world.

This is the highest-value stage. The code reproduces `ModelPart.Cube`'s box-UV unwrap in
block-model `uv` space, with the u/v pair swaps that entity-cube vertex winding needs, the
`flippedY` distinction between `ChestModel`'s block convention (`PartPose.ZERO`) and
`ShulkerModel`'s mob convention (`PartPose.offset(0, 24, 0)`, Y growing downward), and
`invertedTwin`'s inward-facing duplicate that restores what no-cull entity rendering drew. Its own
comment says a second copy would be a liability; there are already two consumers inside one mod.

## S4 — Extract the display-element pose constants — **planned**

**Build.** Add `rkcore` `common/display` carrying the three things any `ItemDisplayElement` user
re-derives: the 180° Y turn `ItemDisplayRenderer#submitInner` bakes into every `Display$ItemDisplay`
(`ChestVisual.RENDERER_YAW_OFFSET:43`), the `orientation(float yaw)` helper that accounts for it
(`:95-96`), and the vanilla lid tween — `Mth.approach` ±0.1/tick then an ease-out cubic, reproducing
`ChestLidController` plus `ChestRenderer#submit` server-side (`tweenOpenness:115`). Keep
`ChestVisual` itself in `planked-chests`: its `HINGE:52` offset and two-part base/lid split are
chest geometry, not a general pattern.

`planked-chests/build.gradle` needs a new `include 'club/rainbowkitty/rkcore/common/display/**'`
line alongside the existing five (`:52-56`).

**Done when.** `ChestVisual` compiles against the shaded constants with its own copies deleted, and
a placed chest's lid still animates smoothly and faces the right way on the dev server. Facing is
the specific thing to watch: a sign error in the yaw offset is a 180° rotation, which is
unmistakable in-world and invisible to any build check.

`minecart-tweaks`' `LinkChain` is the second consumer that justifies this, but it is a
`BlockDisplayElement` and hits a *sibling* quirk rather than the same one — its `MODEL_AXIS:40`
cancels a corner offset, where `ChestVisual` cancels a baked rotation. Do **not** try to unify the
two into one abstraction in this stage; put both facts in one package so the next person finds them
together, and leave `LinkChain` alone. `minecart-tweaks` does not shade `rkcore` at all today, and
making it a consumer is out of scope here.

## S5 — Move image compositing into PngAssets — **planned**

**Build.** Add to `rkcore`'s existing `common/datagen/PngAssets`, beside `read` and `save`:
`composite(base, overlay)` and its per-pixel `over` from `CartIcons:58,99`, and `sized(image, name)`
from `CartIcons:78`. Carry the magenta `CUTOUT` chroma-key convention as documented behaviour rather
than a bare constant — it exists because full transparency already means "show the cargo
underneath", so "nothing here" needed a colour instead.

**Done when.** `ChestMinecartTextureProvider` and `CargoCartTextureProvider` both compile against
the shared copy and `./gradlew clean generateAssets` regenerates every cart icon byte-identically.

Low risk and small, but only two consumers exist and both are in one mod. `companions`'
`SweaterTextureProvider` does its own `Graphics2D` tint pass and would not use `composite` as
written. Worth doing because `PngAssets` is already the agreed home for datagen pixel work, not
because a third caller is waiting.

## S6 — Extract the Polymer real-block file set — **planned**

**Build.** Generalise what `ChestBlockStateProvider` writes so that any Polymer mod sending real
block states to handshake-capable clients gets it: a blockstate pointing at a `particle`-only,
element-less block model (`variants:59`, `particleModel:70`). Without those files a client that
*does* run the mod renders a missing-model cube behind whatever its `BlockEntityRenderer` draws,
while break and landing particles still match the material. Land it next to the
`PolymerClientHandshake` and `PolymerChestRenderer` it completes.

**Done when.** `planked-chests` generates the same 27 blockstates and models as before, and a
handshake-capable dev client shows no missing-model cube behind a placed chest.

Its companion fact is a one-liner and belongs in prose, not code: `ChestBlocks:150` sets
`.isRedstoneConductor((state, level, pos) -> false)` because Polymer reports the barrier
client-state's full-cube shape for non-player collision queries, so the default predicate would make
a chest a redstone conductor and a chest directly below it would refuse to open. Add that to the
Polymer section of the workspace `CLAUDE.md` as part of this stage; it costs nothing and is exactly
the kind of trap that burns a debugging session.

Shape this against S2's outcome: if the typed route worked, emit `MultiVariantGenerator` /
`BlockModelDefinitionGenerator` rather than the raw objects. Read their actual builder signatures
first — this document only established that both classes exist in the jar.

## S7 — Extract the atlas-merge helper — **planned**

**Build.** Generalise `ShulkerAtlasProvider`'s technique into `common/datagen` as a small
`AtlasSources` helper that appends `minecraft:single` sources to a named vanilla atlas. Writing
`assets/minecraft/atlases/blocks.json` *adds* to vanilla's block atlas rather than replacing it,
which is how an item model can reference a vanilla entity texture without copying the PNG — and
copying would both redistribute vanilla art and freeze it against resource packs.

**Done when.** `ShulkerAtlasProvider` compiles against the helper and regenerates an identical
`blocks.json`, and a cart-borne shulker box still draws with its vanilla shell texture on the dev
server.

Smallest stage and the most optional: one consumer, and most of the value is the recorded reasoning
rather than the six lines of code. The supporting claims — `SpriteSourceList.load` walking
`ResourceManager#getResourceStack`, vanilla's own `entity/bell/bell_body` precedent, and
`SingleFile#run` naming a sprite after its `resource` when no `sprite` is given — come from
`ShulkerAtlasProvider`'s Javadoc and were verified when it was written, not re-verified here.
Re-check them before relying on the helper somewhere new.

## Open decisions

**Where `rkcore` pushes now.** Its GitHub repository was deleted and the empty
`RainbowKittyClub/Kalic0re` created in its place, so 19 commits of finished work exist only in this
checkout. Three shapes, and they are not equivalent: repoint `origin` at `Kalic0re` and push over
its lone initial commit (force, or an unrelated-histories merge); recreate `RainbowKittyClub/rkcore`
and push the fast-forward as intended; or treat `Kalic0re` as a genuine re-scoping, in which case
the mod id, Maven coordinate and `club.rainbowkitty.rkcore` package all move too and both consumers'
`relocate` lines change with them. Settled by what the new repository was created *for*, which is
not recoverable from the repository itself. Until it is settled, nothing off this machine has a copy
of the armor or cargo-minecart frameworks.

**Whether `planked-chests` moves to `client = true` datagen.** Settled by the probe at the top of
S2, not by argument. If the typed builders load without a client instance the question never comes
up; if they do not, the trade is a client-booting datagen pass against deleting four hand-rolled
JSON emitters, and the honest answer may be to keep the raw JSON and record why.

**Whether `rk-tweaks` ever becomes an rkcore consumer.** Its `BuiltinDataPacks` is a verbatim twin
of `planked-chests`' — same `ResourceLoader.registerBuiltinPack` shape, same `getModContainer`
guard — and extracting it would save roughly ten lines. `rk-tweaks` shades nothing today, so the
cost is the entire Shadow wiring for one small helper. Left undecided rather than rejected: if
`rk-tweaks` gains a second reason to shade, this comes with it for free.

## Reference: what is deliberately not extracted

| Pattern | Where | Why it stays |
| --- | --- | --- |
| `WoodType` enum | `planked-chests/wood/WoodType.java` | The variant list is mod-specific. Only the optional-mod gate (`requiredModId:97`) and `ChestBlocks.DATAGEN:59` generalise, and both are a line each. |
| `DATAGEN` sniff | `ChestBlocks.java:59` | `System.getProperty("fabric-api.datagen")`, found by reading `FabricDataGenHelper`'s bytecode — no public constant exposes it. One line; put it in `common/datagen` during S3 rather than building a framework around it. |
| `BuiltinDataPacks` | `planked-chests`, `rk-tweaks` | Two verbatim copies, but see Open decisions — the shading cost exceeds the saving. |
| `registerCreativeTab` | `ChestBlocks.java:87` | One consumer. The knowledge is the value: vanilla-tab additions do not reliably sync to Polymer clients, a Polymer-registered tab does, and a vanilla client reaches it only through `/polymer creative`. |
| `MOD_ID` / `id(path)` / `LOGGER` | every mod's entrypoint | A shared helper would need the namespace passed in anyway. |

## Reference: verified this session

| Claim | How checked |
| --- | --- |
| `DataProvider.saveAll` in three overloads, `saveStable` in three | `javap -cp minecraft-merged-deobf-26.2.jar net.minecraft.data.DataProvider` |
| `MultiVariantGenerator`, `BlockModelDefinitionGenerator`, `ClientItem`, `ClientItem$Properties`, `ItemModelUtils` present | `unzip -l` on the same jar — **existence only, no signatures** |
| All three `~/.m2` rkcore jars carry seven `common/vehicle` classes | `unzip -l ~/.m2/repository/club/rainbowkitty/rkcore/rkcore/<v>/rkcore-<v>.jar` |
| `main` ≡ `origin/main` in content | six matching `git patch-id --stable` values, empty `git diff main origin/main`, committer timestamps the only delta |
| `main` is an ancestor of `custom-armor-models`; `cargo-minecarts` forks from `main@0127d9b` | `git merge-base` against each branch |
| `backup/pre-untangle` carries both armor and vehicle and mirrors `custom-armor-models` patch-for-patch | `git ls-tree -r --name-only`, `git log --graph --all` |
