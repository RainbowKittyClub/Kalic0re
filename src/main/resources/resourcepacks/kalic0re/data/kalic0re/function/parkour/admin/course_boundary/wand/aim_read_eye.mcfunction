# As a marker at the holder's eyes: where they are, in hundredths of a block.
execute store result score #aimEyeX kalic0re.parkour.tmp run data get entity @s Pos[0] 100
execute store result score #aimEyeY kalic0re.parkour.tmp run data get entity @s Pos[1] 100
execute store result score #aimEyeZ kalic0re.parkour.tmp run data get entity @s Pos[2] 100
kill @s
