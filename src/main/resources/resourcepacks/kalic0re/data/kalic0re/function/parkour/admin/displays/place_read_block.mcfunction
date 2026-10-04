# As a marker at the block's corner: the block, in whole blocks.
execute store result score #placeBlockX kalic0re.parkour.tmp run data get entity @s Pos[0]
execute store result score #placeBlockY kalic0re.parkour.tmp run data get entity @s Pos[1]
execute store result score #placeBlockZ kalic0re.parkour.tmp run data get entity @s Pos[2]
kill @s
