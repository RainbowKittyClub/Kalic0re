# As a direction marker without base tags: its translation is its base, unless it's tagged Ready, whose base
# scores the first version kept. Then the base goes into its tags.
execute unless entity @s[tag=kalic0re.parkour.directionMarkerReady] store result score @s kalic0re.parkour.directionMarkerBaseX run \
    data get entity @s transformation.translation[0] 10000
execute unless entity @s[tag=kalic0re.parkour.directionMarkerReady] store result score @s kalic0re.parkour.directionMarkerBaseY run \
    data get entity @s transformation.translation[1] 10000
execute unless entity @s[tag=kalic0re.parkour.directionMarkerReady] store result score @s kalic0re.parkour.directionMarkerBaseZ run \
    data get entity @s transformation.translation[2] 10000
function kalic0re:parkour/direction_marker/base_into_storage
function kalic0re:parkour/direction_marker/add_base_tags with storage kalic0re:parkour tmp.directionMarkerBase
data remove storage kalic0re:parkour tmp.directionMarkerBase
