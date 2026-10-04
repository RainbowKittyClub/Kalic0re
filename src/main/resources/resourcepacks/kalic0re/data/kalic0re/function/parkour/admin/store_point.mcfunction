# For admin/set and admin/course/move_point: tmp.pos as $(course)'s $(point), and the player told.
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].$(point) set from storage kalic0re:parkour tmp.pos
$tellraw @s {translate:"kalic0re.parkour.chat.point_set",with:["$(course)",{nbt:"pointNames.$(point)",storage:"kalic0re:parkour",interpret:true}],color:"green"}
