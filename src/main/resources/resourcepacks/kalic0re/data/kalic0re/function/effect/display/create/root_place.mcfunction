# The root's place from the cuboid's lowest block, in thousandths: centred across, and half a block into
# the top layer, which keeps its light that of the display's top while it sinks or rises.
scoreboard players operation #displayRootX kalic0re.effect.tmp = #displaySizeX kalic0re.effect.tmp
scoreboard players operation #displayRootX kalic0re.effect.tmp /= #2 kalic0re.effect.tmp
scoreboard players operation #displayRootY kalic0re.effect.tmp = #displaySizeY kalic0re.effect.tmp
scoreboard players remove #displayRootY kalic0re.effect.tmp 500
scoreboard players operation #displayRootZ kalic0re.effect.tmp = #displaySizeZ kalic0re.effect.tmp
scoreboard players operation #displayRootZ kalic0re.effect.tmp /= #2 kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.place.rootX double 0.001 run \
    scoreboard players get #displayRootX kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.place.rootY double 0.001 run \
    scoreboard players get #displayRootY kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.place.rootZ double 0.001 run \
    scoreboard players get #displayRootZ kalic0re.effect.tmp
