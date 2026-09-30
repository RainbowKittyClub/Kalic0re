# /function kalic0re:parkour/admin/set {course:"<name>",point:"start"}, or point "lobby"; stand where it goes.
function kalic0re:parkour/pos/capture
$data modify storage kalic0re:parkour courses."$(course)".$(point) set from storage kalic0re:parkour tmp.pos
$tellraw @s {text:'Parkour course "$(course)": $(point) set.',color:"green"}
