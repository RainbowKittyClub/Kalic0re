# The Clear All Times button: asks first. Back opens the Clear Leaderboard dialog, embedded.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/dialog/build_times {course:"$(course)"}
$data modify storage kalic0re:dialog args set value {course:"$(course)"}
function kalic0re:parkour/admin/dialog/clear_all
data modify storage kalic0re:dialog built.clear_all.no.action.dialog set from storage kalic0re:dialog built.times
function kalic0re:dialog/show {name:"clear_all"}
