# /function kalic0re:parkour/admin/set {course:"<name>",point:"start"}, or point "lobby"; stand where it goes.
# The list filter adds {name:"<name>"} when no course matches, so a new name makes a new course.
function kalic0re:parkour/pos/capture
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].$(point) set from storage kalic0re:parkour tmp.pos
$tellraw @s {translate:"kalic0re.parkour.chat.point_set",fallback:"Parkour course \"%s\": %s set.",with:["$(course)",{nbt:"pointNames.$(point)",storage:"kalic0re:parkour",interpret:true}],color:"green"}
