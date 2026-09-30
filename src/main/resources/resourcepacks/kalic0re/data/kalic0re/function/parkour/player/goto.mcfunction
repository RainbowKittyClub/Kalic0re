# Teleports to $(point), "start" or "lobby", of the course this player is running.
$data modify storage kalic0re:parkour tmp.goto set value {point:"$(point)"}
$data modify storage kalic0re:parkour tmp.goto.course set from storage kalic0re:parkour players[{uuid:$(uuid)}].course
function kalic0re:parkour/course/goto with storage kalic0re:parkour tmp.goto
