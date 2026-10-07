function kalic0re:effect/display/create/cell
scoreboard players add #displayCellX kalic0re.effect.tmp 1000
execute if score #displayCellX kalic0re.effect.tmp < #displaySizeX kalic0re.effect.tmp positioned ~1 ~ ~ run \
    function kalic0re:effect/display/create/scan_cells
