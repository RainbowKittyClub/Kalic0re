# As the new root: the name and tags, then a part for each block, scanned from the lowest, $(x) $(y) $(z).
tag @s add kalic0re.effect.display
execute unless data storage kalic0re:effect displayCreate{name:""} run \
    data modify entity @s CustomName set from storage kalic0re:effect displayCreate.name
data modify entity @s Tags append from storage kalic0re:effect displayCreate.tags[]
scoreboard players set #displayCellY kalic0re.effect.tmp 0
$execute positioned $(x) $(y) $(z) align xyz run function kalic0re:effect/display/create/scan_layers
execute if score #displayParts kalic0re.effect.tmp matches 0 run kill @s
