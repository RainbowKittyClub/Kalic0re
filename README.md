# rkcore

Server-side Fabric mod for the Rainbow Kitty Club server, targeting Minecraft 26.2. Fabric API and Polymer (core + resource pack) are wired up; bil is not.

## Build

```
./gradlew build     # jar lands in build/libs/rkcore-<version>.jar
```

No test suite. `processResources` templates `${project.*}` into `fabric.mod.json` from `gradle.properties`, then `validateFabricModJson` parses the generated file. A `Failed to parse fabric.mod.json` warning at configure time is benign — Loom reads the un-expanded template.

Version, mod id, MC/loader/API versions and the entrypoint all live in `gradle.properties`; nothing is hardcoded in `build.gradle`.

## Layout

Each feature is its own top-level package, sharing the raycast/label machinery in `common`:

- `club.rainbowkitty.rkcore.Main` — `ModInitializer` entrypoint.
- `club.rainbowkitty.rkcore.books` — chiseled-bookshelf hover titles.
- `club.rainbowkitty.rkcore.frame` — item-frame hover labels.
- `club.rainbowkitty.rkcore.armorstands` — armor-stand equipment hover labels.
- `club.rainbowkitty.rkcore.common.label` — the shared per-player raycast and label machinery all three features build on.
- `club.rainbowkitty.rkcore.common.mixin` — mixin package, registered via `src/main/resources/rkcore.mixins.json`.
- `docs/` — design notes, one file per feature.

## Dev server

```
./gradlew runServer
./gradlew stopServer   # asks the running dev server to stop, so the world saves
```
