# The Move Start and Move Lobby dialogs' button: $(course)'s $(point) at the texts' position and rotation, in your
# world. The course is checked first, as storing would make a new course of a stale name.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
execute unless function kalic0re:parkour/admin/text/place_readable run return run function kalic0re:parkour/admin/text/refuse_place
data remove storage kalic0re:parkour tmp.pos
function kalic0re:parkour/admin/course/capture_point_text with storage kalic0re:parkour tmp.dialogTexts
execute unless data storage kalic0re:parkour tmp.pos run return run function kalic0re:parkour/admin/text/refuse_place
data remove storage kalic0re:parkour tmp.dialogTexts
data modify storage kalic0re:parkour tmp.pos.dim set from entity @s Dimension
$function kalic0re:parkour/admin/store_point {course:"$(course)",point:"$(point)"}
data remove storage kalic0re:parkour tmp.pos
