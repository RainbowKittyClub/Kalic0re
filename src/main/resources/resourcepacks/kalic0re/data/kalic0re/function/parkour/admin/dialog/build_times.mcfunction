# $(course)'s Clear Leaderboard dialog: Clear All, then a button per time. Its Back opens the
# course's management dialog, embedded here.
$function kalic0re:parkour/admin/dialog/build_manage {course:"$(course)"}
function kalic0re:parkour/admin/dialog/times
data modify storage kalic0re:dialog built.times.exit_action.action.dialog set from storage kalic0re:dialog built.manage
data modify storage kalic0re:parkour tmp.entries set value []
$data modify storage kalic0re:parkour tmp.entries set from storage kalic0re:parkour courses[{name:"$(course)"}].times
$function kalic0re:parkour/admin/dialog/add_time_buttons {course:"$(course)"}
data remove storage kalic0re:parkour tmp.entries
