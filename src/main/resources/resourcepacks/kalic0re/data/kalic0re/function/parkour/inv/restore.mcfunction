$execute unless data storage kalic0re:parkour players[{uuid:$(uuid)}].inventory run \
    return run clear @s *[custom_data~{kalic0re:{parkour:{}}}]
clear @s
$data modify storage kalic0re:parkour tmp.saved set from storage kalic0re:parkour players[{uuid:$(uuid)}]
# Player NBT is read-only, so each item goes back through a transient display entity's one slot.
tag @s add kalic0re.parkour.restoring
execute summon minecraft:item_display run function kalic0re:parkour/inv/unload
tag @s remove kalic0re.parkour.restoring
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}].inventory
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}].equipment
data remove storage kalic0re:parkour tmp.saved
