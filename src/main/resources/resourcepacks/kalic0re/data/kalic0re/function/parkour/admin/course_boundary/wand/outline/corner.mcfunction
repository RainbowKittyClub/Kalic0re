# Frames the held wand's $(corner) block in $(pane), glowing $(glow).
$data modify storage kalic0re:parkour tmp.outline.corner set value {pane:"$(pane)",glow:$(glow)}
$execute store result storage kalic0re:parkour tmp.outline.corner.x int 1 run data get storage kalic0re:parkour tmp.wand.held.$(corner)[0]
$execute store result storage kalic0re:parkour tmp.outline.corner.y int 1 run data get storage kalic0re:parkour tmp.wand.held.$(corner)[1]
$execute store result storage kalic0re:parkour tmp.outline.corner.z int 1 run data get storage kalic0re:parkour tmp.wand.held.$(corner)[2]
function kalic0re:parkour/admin/course_boundary/wand/outline/corner_frame with storage kalic0re:parkour tmp.outline.corner
