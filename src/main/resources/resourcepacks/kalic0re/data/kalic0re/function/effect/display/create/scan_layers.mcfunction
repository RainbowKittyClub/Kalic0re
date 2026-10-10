scoreboard players operation #displayPart kalic0re.effect.tmp = #displayCellY kalic0re.effect.tmp
scoreboard players operation #displayPart kalic0re.effect.tmp -= #displayRootY kalic0re.effect.tmp
scoreboard players operation #displayPart kalic0re.effect.tmp /= #500 kalic0re.effect.tmp
scoreboard players operation #displayPart kalic0re.effect.tmp *= #displayScaleY kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.translation[1] float 0.00048828125 run \
    scoreboard players get #displayPart kalic0re.effect.tmp
scoreboard players set #displayCellZ kalic0re.effect.tmp 0
function kalic0re:effect/display/create/scan_rows
scoreboard players add #displayCellY kalic0re.effect.tmp 1000
execute if score #displayCellY kalic0re.effect.tmp < #displaySizeY kalic0re.effect.tmp positioned ~ ~1 ~ run \
    function kalic0re:effect/display/create/scan_layers
