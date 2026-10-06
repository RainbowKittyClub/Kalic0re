# The management dialog's Place Leaderboard Display: as open, the Place dialog for $(course), its Position and
# Rotation starting from where you look (displays/place_defaults), and Back to the management dialog.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/dialog/build_manage {course:"$(course)"}
function kalic0re:parkour/admin/displays/place_defaults
function kalic0re:parkour/admin/dialog/leaderboard_display
data modify storage kalic0re:dialog built.leaderboard_display.no.action.dialog set from storage kalic0re:dialog built.manage
function kalic0re:dialog/show {name:"leaderboard_display"}
