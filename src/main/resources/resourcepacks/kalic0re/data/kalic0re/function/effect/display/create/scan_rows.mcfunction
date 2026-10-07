scoreboard players operation #displayPart kalic0re.effect.tmp = #displayCellZ kalic0re.effect.tmp
scoreboard players operation #displayPart kalic0re.effect.tmp -= #displayRootZ kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.translation[2] float 0.001 run \
    scoreboard players get #displayPart kalic0re.effect.tmp
scoreboard players set #displayCellX kalic0re.effect.tmp 0
function kalic0re:effect/display/create/scan_cells
scoreboard players add #displayCellZ kalic0re.effect.tmp 1000
execute if score #displayCellZ kalic0re.effect.tmp < #displaySizeZ kalic0re.effect.tmp positioned ~ ~ ~1 run \
    function kalic0re:effect/display/create/scan_rows
