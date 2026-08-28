# rkcore-books

Server-side Fabric mod for the Rainbow Kitty Club server, targeting Minecraft 26.2. Fabric API and Polymer (core + resource pack) are wired up; bil is not.

## Build

```
./gradlew build     # jar lands in build/libs/rkcore-books-<version>.jar
```

No test suite. `processResources` templates `${project.*}` into `fabric.mod.json` from `gradle.properties`, then `validateFabricModJson` parses the generated file. A `Failed to parse fabric.mod.json` warning at configure time is benign — Loom reads the un-expanded template.

Version, mod id, MC/loader/API versions and the entrypoint all live in `gradle.properties`; nothing is hardcoded in `build.gradle`.

## Layout

- `club.rainbowkitty.rkcore.books.Main` — `ModInitializer` entrypoint.
- `club.rainbowkitty.rkcore.books.mixin` — mixin package, registered via `src/main/resources/rkcore-books.mixins.json` (currently empty).
- `docs/bookshelf-hover-titles.md` — design notes for the chiseled-bookshelf hover label feature.

## Dev server

```
./gradlew runServer
./gradlew stopServer   # asks the running dev server to stop, so the world saves
```
