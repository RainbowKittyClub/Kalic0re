# The New Course dialog's confirm button: refuses a name already in use, then sets the start here.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/name_taken {course:"$(course)"}
$function kalic0re:parkour/admin/set {course:"$(course)",point:"start"}
