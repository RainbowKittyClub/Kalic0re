# The Move Start dialog's button. Checked first, since admin/set would make a new course of a stale name.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/set {course:"$(course)",point:"start"}
