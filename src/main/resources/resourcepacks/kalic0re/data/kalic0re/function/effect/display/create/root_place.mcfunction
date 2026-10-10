# The root's place from the cuboid's lowest block, in thousandths: centred across, and half a layer into
# the top layer, which keeps its light that of the display's top while it sinks or rises. Up, that is
# scaled too, half blocks times the y scale in 1024ths, so in 2048ths: the display keeps the ground.
scoreboard players operation #displayRootX kalic0re.effect.tmp = #displaySizeX kalic0re.effect.tmp
scoreboard players operation #displayRootX kalic0re.effect.tmp /= #2 kalic0re.effect.tmp
scoreboard players operation #displayRootY kalic0re.effect.tmp = #displaySizeY kalic0re.effect.tmp
scoreboard players remove #displayRootY kalic0re.effect.tmp 500
scoreboard players operation #displayRootZ kalic0re.effect.tmp = #displaySizeZ kalic0re.effect.tmp
scoreboard players operation #displayRootZ kalic0re.effect.tmp /= #2 kalic0re.effect.tmp
scoreboard players operation #displayRootScaledY kalic0re.effect.tmp = #displayRootY kalic0re.effect.tmp
scoreboard players operation #displayRootScaledY kalic0re.effect.tmp /= #500 kalic0re.effect.tmp
scoreboard players operation #displayRootScaledY kalic0re.effect.tmp *= #displayScaleY kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.place.rootX double 0.001 run \
    scoreboard players get #displayRootX kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.place.rootY double 0.00048828125 run \
    scoreboard players get #displayRootScaledY kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.place.rootZ double 0.001 run \
    scoreboard players get #displayRootZ kalic0re.effect.tmp
