# For aim_test_board, as a board's title at it: display-local ($(x), $(y), $(z)), in ten-thousandths, turned by the
# title's right_rotation then left_rotation as the renderer turns it (direction_marker/rotate), then stood 10 along
# from the title in its facing, where aim_read_axis reads it as the board's $(axis) axis.
$scoreboard players set #rotatedX kalic0re.parkour.tmp $(x)
$scoreboard players set #rotatedY kalic0re.parkour.tmp $(y)
$scoreboard players set #rotatedZ kalic0re.parkour.tmp $(z)
data modify storage kalic0re:parkour tmp.directionMarkerTransformation.rotation set from \
    storage kalic0re:parkour tmp.directionMarkerTransformation.right_rotation
function kalic0re:parkour/direction_marker/rotate
data modify storage kalic0re:parkour tmp.directionMarkerTransformation.rotation set from \
    storage kalic0re:parkour tmp.directionMarkerTransformation.left_rotation
function kalic0re:parkour/direction_marker/rotate
$data modify storage kalic0re:parkour tmp.aimAxis set value {axis:"$(axis)"}
execute store result storage kalic0re:parkour tmp.aimAxis.x double 0.001 run scoreboard players get #rotatedX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.aimAxis.y double 0.001 run scoreboard players get #rotatedY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.aimAxis.z double 0.001 run scoreboard players get #rotatedZ kalic0re.parkour.tmp
function kalic0re:parkour/admin/course_boundary/wand/aim_mark_axis with storage kalic0re:parkour tmp.aimAxis
data remove storage kalic0re:parkour tmp.aimAxis
