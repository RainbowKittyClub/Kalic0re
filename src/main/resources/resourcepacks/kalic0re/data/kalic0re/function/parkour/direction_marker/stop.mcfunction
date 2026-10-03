# /execute as <marker> run function kalic0re:parkour/direction_marker/stop: the marker back at its base and no
# longer one, its base tags and scores gone, so its translation can be changed; tag it
# kalic0re.parkour.directionMarker again and it takes its translation then as its base.
tag @s remove kalic0re.parkour.directionMarker
tag @s remove kalic0re.parkour.directionMarkerReady
execute unless score @s kalic0re.parkour.directionMarkerDirectionX matches -2147483648.. run return 0
data modify storage kalic0re:parkour tmp.directionMarkerTranslation set value [0.0f,0.0f,0.0f]
execute store result storage kalic0re:parkour tmp.directionMarkerTranslation[0] float 0.0001 run \
    scoreboard players get @s kalic0re.parkour.directionMarkerBaseX
execute store result storage kalic0re:parkour tmp.directionMarkerTranslation[1] float 0.0001 run \
    scoreboard players get @s kalic0re.parkour.directionMarkerBaseY
execute store result storage kalic0re:parkour tmp.directionMarkerTranslation[2] float 0.0001 run \
    scoreboard players get @s kalic0re.parkour.directionMarkerBaseZ
data modify entity @s transformation.translation set from storage kalic0re:parkour tmp.directionMarkerTranslation
data remove storage kalic0re:parkour tmp.directionMarkerTranslation
function kalic0re:parkour/direction_marker/base_into_storage
function kalic0re:parkour/direction_marker/remove_base_tags with storage kalic0re:parkour tmp.directionMarkerBase
data remove storage kalic0re:parkour tmp.directionMarkerBase
scoreboard players reset @s kalic0re.parkour.directionMarkerBaseX
scoreboard players reset @s kalic0re.parkour.directionMarkerBaseY
scoreboard players reset @s kalic0re.parkour.directionMarkerBaseZ
scoreboard players reset @s kalic0re.parkour.directionMarkerDirectionX
scoreboard players reset @s kalic0re.parkour.directionMarkerDirectionY
scoreboard players reset @s kalic0re.parkour.directionMarkerDirectionZ
