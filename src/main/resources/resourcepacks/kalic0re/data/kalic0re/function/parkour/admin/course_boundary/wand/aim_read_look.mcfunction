# As a marker 10 along the holder's look from their eyes: the look, a unit vector in thousandths.
execute store result score #aimDX kalic0re.parkour.tmp run data get entity @s Pos[0] 100
execute store result score #aimDY kalic0re.parkour.tmp run data get entity @s Pos[1] 100
execute store result score #aimDZ kalic0re.parkour.tmp run data get entity @s Pos[2] 100
scoreboard players operation #aimDX kalic0re.parkour.tmp -= #aimEyeX kalic0re.parkour.tmp
scoreboard players operation #aimDY kalic0re.parkour.tmp -= #aimEyeY kalic0re.parkour.tmp
scoreboard players operation #aimDZ kalic0re.parkour.tmp -= #aimEyeZ kalic0re.parkour.tmp
kill @s
