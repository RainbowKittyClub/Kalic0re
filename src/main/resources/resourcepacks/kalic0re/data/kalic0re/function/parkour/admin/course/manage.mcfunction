# A Courses dialog button: opens $(course)'s management dialog, unless renamed or deleted meanwhile.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/dialog/build_manage {course:"$(course)"}
function kalic0re:dialog/show {name:"manage"}
