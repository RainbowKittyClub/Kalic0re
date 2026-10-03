# As a direction marker without scores: its base translation and the way it points, in ten-thousandths. The base
# is kept in tags too, kalic0re.parkour.directionMarkerBase<axis>_<n>, as a copy carries tags but not scores, and
# its translation, taken mid-move, is up to 0.2 off its base.
scoreboard players set #directionMarkerBaseTags kalic0re.parkour.tmp 0
data modify storage kalic0re:parkour tmp.directionMarkerTags set from entity @s Tags
function kalic0re:parkour/direction_marker/find_base_tags
data remove storage kalic0re:parkour tmp.directionMarkerTags
execute unless score #directionMarkerBaseTags kalic0re.parkour.tmp matches 3 run \
    function kalic0re:parkour/direction_marker/record_base
tag @s remove kalic0re.parkour.directionMarkerReady
function kalic0re:parkour/direction_marker/refresh_direction
# One tick's interpolation, so the client smooths each step.
data modify entity @s interpolation_duration set value 1
