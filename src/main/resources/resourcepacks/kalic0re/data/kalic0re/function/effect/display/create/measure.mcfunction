# Along $(axis): the cuboid's lowest block, where the scan starts, and its size in thousandths.
$execute store result score #displayFrom kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayCreate.from[$(index)]
$execute store result score #displayTo kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayCreate.to[$(index)]
scoreboard players operation #displayLow kalic0re.effect.tmp = #displayFrom kalic0re.effect.tmp
scoreboard players operation #displayLow kalic0re.effect.tmp < #displayTo kalic0re.effect.tmp
$execute store result storage kalic0re:effect displayCreate.place.$(key) int 1 run \
    scoreboard players get #displayLow kalic0re.effect.tmp
$scoreboard players operation #displaySize$(axis) kalic0re.effect.tmp = #displayFrom kalic0re.effect.tmp
$scoreboard players operation #displaySize$(axis) kalic0re.effect.tmp > #displayTo kalic0re.effect.tmp
$scoreboard players operation #displaySize$(axis) kalic0re.effect.tmp -= #displayLow kalic0re.effect.tmp
$scoreboard players add #displaySize$(axis) kalic0re.effect.tmp 1
$scoreboard players operation #displaySize$(axis) kalic0re.effect.tmp *= #1000 kalic0re.effect.tmp
