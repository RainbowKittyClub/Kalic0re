# The management dialog's Clear Leaderboard button, unless $(course) was renamed or deleted meanwhile.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/dialog/build_times {course:"$(course)"}
function kalic0re:dialog/show {name:"times"}
