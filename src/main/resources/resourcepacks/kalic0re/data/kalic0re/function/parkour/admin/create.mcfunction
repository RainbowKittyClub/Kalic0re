# /function kalic0re:parkour/admin/create {course:"<name>"}: a course with its start here and no lobby yet; a name
# already in use opens New Course instead.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/name_taken {course:"$(course)"}
$function kalic0re:parkour/admin/set {course:"$(course)",point:"start"}
