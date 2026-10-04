# As the wand's holder, at their feet with their rotation: marks $(corner) (firstCorner or
# secondCorner) on the held wand, at the block they look at.
$data modify storage kalic0re:parkour tmp.wand set value {corner:"$(corner)"}
function kalic0re:parkour/admin/course_boundary/wand/read_held
scoreboard players set #rayStepsLeft kalic0re.parkour.tmp 640
execute anchored eyes positioned ^ ^ ^ anchored feet run \
    function kalic0re:parkour/admin/look_ray/step {hit:"kalic0re:parkour/admin/course_boundary/wand/mark_here"}
execute if data storage kalic0re:parkour tmp.wand.pos run \
    function kalic0re:parkour/admin/course_boundary/wand/set_corner with storage kalic0re:parkour tmp.wand
execute unless data storage kalic0re:parkour tmp.wand.pos run tellraw @s {translate:"kalic0re.parkour.chat.wand_no_block",color:"red"}
data remove storage kalic0re:parkour tmp.wand
