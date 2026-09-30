# Deleting $(course) removes its times scoreboard, if it has one yet.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].id run return 0
data modify storage kalic0re:parkour tmp.times set value {}
$data modify storage kalic0re:parkour tmp.times.id set from storage kalic0re:parkour courses[{name:"$(course)"}].id
function kalic0re:parkour/course/remove_times_objective with storage kalic0re:parkour tmp.times
data remove storage kalic0re:parkour tmp.times
