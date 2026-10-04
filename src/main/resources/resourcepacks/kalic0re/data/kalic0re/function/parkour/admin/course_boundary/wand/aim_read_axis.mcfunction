# As a marker 10 along one of a board's axes from its title: that axis, $(axis), a unit vector in thousandths.
$execute store result score #aim$(axis)X kalic0re.parkour.tmp run data get entity @s Pos[0] 100
$execute store result score #aim$(axis)Y kalic0re.parkour.tmp run data get entity @s Pos[1] 100
$execute store result score #aim$(axis)Z kalic0re.parkour.tmp run data get entity @s Pos[2] 100
$scoreboard players operation #aim$(axis)X kalic0re.parkour.tmp -= #aimTitleX kalic0re.parkour.tmp
$scoreboard players operation #aim$(axis)Y kalic0re.parkour.tmp -= #aimTitleY kalic0re.parkour.tmp
$scoreboard players operation #aim$(axis)Z kalic0re.parkour.tmp -= #aimTitleZ kalic0re.parkour.tmp
kill @s
