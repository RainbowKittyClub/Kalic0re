# As a marker where place_ahead puts the board: where, in tenths, then gone.
execute store result score #placeX kalic0re.parkour.tmp run data get entity @s Pos[0] 10
execute store result score #placeY kalic0re.parkour.tmp run data get entity @s Pos[1] 10
execute store result score #placeZ kalic0re.parkour.tmp run data get entity @s Pos[2] 10
kill @s
