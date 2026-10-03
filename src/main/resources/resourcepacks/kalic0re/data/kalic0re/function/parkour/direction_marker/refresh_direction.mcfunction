# As a direction marker: the way it points, from its rotations, in ten-thousandths. The chevron points to the
# text's left, which the text renderer puts on the display's local -X; right_rotation turns that, then
# left_rotation. Scale is left out: it turns the way only when it's uneven and the arrow is off its axes. All in
# the display's own frame, as its translation is, so its yaw doesn't matter.
data modify storage kalic0re:parkour tmp.directionMarkerTransformation set from entity @s transformation
scoreboard players set #rotatedX kalic0re.parkour.tmp -10000
scoreboard players set #rotatedY kalic0re.parkour.tmp 0
scoreboard players set #rotatedZ kalic0re.parkour.tmp 0
data modify storage kalic0re:parkour tmp.directionMarkerTransformation.rotation set from \
    storage kalic0re:parkour tmp.directionMarkerTransformation.right_rotation
function kalic0re:parkour/direction_marker/rotate
data modify storage kalic0re:parkour tmp.directionMarkerTransformation.rotation set from \
    storage kalic0re:parkour tmp.directionMarkerTransformation.left_rotation
function kalic0re:parkour/direction_marker/rotate
scoreboard players operation @s kalic0re.parkour.directionMarkerDirectionX = #rotatedX kalic0re.parkour.tmp
scoreboard players operation @s kalic0re.parkour.directionMarkerDirectionY = #rotatedY kalic0re.parkour.tmp
scoreboard players operation @s kalic0re.parkour.directionMarkerDirectionZ = #rotatedZ kalic0re.parkour.tmp
data remove storage kalic0re:parkour tmp.directionMarkerTransformation
