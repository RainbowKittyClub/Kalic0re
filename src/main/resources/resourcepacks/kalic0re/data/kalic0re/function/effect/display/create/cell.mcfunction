# As the root, at a block: a part for it, unless it is air or a block kalic0re:block_state/read can't name.
execute if block ~ ~ ~ #minecraft:air run return 0
execute unless function kalic0re:block_state/read run return 0
scoreboard players operation #displayPart kalic0re.effect.tmp = #displayCellX kalic0re.effect.tmp
scoreboard players operation #displayPart kalic0re.effect.tmp -= #displayRootX kalic0re.effect.tmp
execute store result storage kalic0re:effect displayCreate.translation[0] float 0.001 run \
    scoreboard players get #displayPart kalic0re.effect.tmp
execute at @s summon minecraft:block_display run function kalic0re:effect/display/create/part
scoreboard players add #displayParts kalic0re.effect.tmp 1
