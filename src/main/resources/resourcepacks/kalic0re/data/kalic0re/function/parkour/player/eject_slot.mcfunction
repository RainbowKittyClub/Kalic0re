# As a runner, at their feet: the stack in $(slot) onto the ground and the slot emptied. Copied with item replace,
# so no player NBT is read. An item entity summoned empty discards itself, hence the stone it starts with.
summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:stone",count:1},PickupDelay:40s,Tags:["kalic0re.parkour.ejectedItem"]}
$item replace entity @n[type=minecraft:item,tag=kalic0re.parkour.ejectedItem,distance=..1] contents from entity @s $(slot)
tag @n[type=minecraft:item,tag=kalic0re.parkour.ejectedItem,distance=..1] remove kalic0re.parkour.ejectedItem
$item replace entity @s $(slot) with minecraft:air
