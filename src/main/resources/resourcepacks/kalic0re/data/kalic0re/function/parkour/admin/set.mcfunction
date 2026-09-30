# /function kalic0re:parkour/admin/set {course:"<name>",point:"start"}, or point "lobby"; stand where it goes.
# The list filter adds {name:"<name>"} when no course matches, so a new name makes a new course.
function kalic0re:parkour/pos/capture
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].$(point) set from storage kalic0re:parkour tmp.pos
$tellraw @s {text:"Parkour course \"$(course)\": $(point) set.",color:"green"}
