# Along $(axis): the scale given, in 1024ths, and each part's scale from it.
$execute store result score #displayScale$(axis) kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayCreate.scale[$(index)] 1024
$execute store result storage kalic0re:effect displayCreate.part.scale[$(index)] float 0.0009765625 run \
    scoreboard players get #displayScale$(axis) kalic0re.effect.tmp
