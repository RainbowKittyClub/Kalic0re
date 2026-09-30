# A player's button in the Clear Leaderboard dialog: asks first. Back opens that dialog, embedded.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].times[{player:"$(player)"}] run return run \
    function kalic0re:parkour/admin/course/no_time {course:"$(course)",player:"$(player)"}
$function kalic0re:parkour/admin/dialog/build_times {course:"$(course)"}
$data modify storage kalic0re:dialog args set value {course:"$(course)",player:"$(player)"}
function kalic0re:parkour/admin/dialog/clear_player
data modify storage kalic0re:dialog built.clear_player.no.action.dialog set from storage kalic0re:dialog built.times
function kalic0re:dialog/show {name:"clear_player"}
