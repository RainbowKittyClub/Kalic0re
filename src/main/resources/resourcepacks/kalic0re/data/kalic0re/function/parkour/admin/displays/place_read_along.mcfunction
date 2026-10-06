# As a marker 10 along the look past the step before: how far that is along each axis, in thousandths, unsigned.
execute store result score #placeAlongX kalic0re.parkour.tmp run data get entity @s Pos[0] 1000
execute store result score #placeAlongY kalic0re.parkour.tmp run data get entity @s Pos[1] 1000
execute store result score #placeAlongZ kalic0re.parkour.tmp run data get entity @s Pos[2] 1000
scoreboard players operation #placeAlongX kalic0re.parkour.tmp -= #placeBeforeX kalic0re.parkour.tmp
scoreboard players operation #placeAlongY kalic0re.parkour.tmp -= #placeBeforeY kalic0re.parkour.tmp
scoreboard players operation #placeAlongZ kalic0re.parkour.tmp -= #placeBeforeZ kalic0re.parkour.tmp
execute if score #placeAlongX kalic0re.parkour.tmp matches ..-1 run scoreboard players operation #placeAlongX kalic0re.parkour.tmp *= #-1 kalic0re.parkour.tmp
execute if score #placeAlongY kalic0re.parkour.tmp matches ..-1 run scoreboard players operation #placeAlongY kalic0re.parkour.tmp *= #-1 kalic0re.parkour.tmp
execute if score #placeAlongZ kalic0re.parkour.tmp matches ..-1 run scoreboard players operation #placeAlongZ kalic0re.parkour.tmp *= #-1 kalic0re.parkour.tmp
kill @s
