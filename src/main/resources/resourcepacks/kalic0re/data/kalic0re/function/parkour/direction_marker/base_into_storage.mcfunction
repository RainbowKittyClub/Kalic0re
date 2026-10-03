# As a direction marker: its base scores into tmp.directionMarkerBase, for the base tag macros.
execute store result storage kalic0re:parkour tmp.directionMarkerBase.x int 1 run \
    scoreboard players get @s kalic0re.parkour.directionMarkerBaseX
execute store result storage kalic0re:parkour tmp.directionMarkerBase.y int 1 run \
    scoreboard players get @s kalic0re.parkour.directionMarkerBaseY
execute store result storage kalic0re:parkour tmp.directionMarkerBase.z int 1 run \
    scoreboard players get @s kalic0re.parkour.directionMarkerBaseZ
