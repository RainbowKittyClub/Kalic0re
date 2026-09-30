# Resets $(player)'s score on $(course)'s best-times scoreboard, or everyone's for "*".
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].id run return 0
$data modify storage kalic0re:parkour tmp.forget set value {player:"$(player)"}
$data modify storage kalic0re:parkour tmp.forget.id set from storage kalic0re:parkour courses[{name:"$(course)"}].id
function kalic0re:parkour/timer/reset_score with storage kalic0re:parkour tmp.forget
data remove storage kalic0re:parkour tmp.forget
