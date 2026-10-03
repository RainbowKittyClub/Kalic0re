# The Delete dialog's button. Runs on it end first, while its lobby still exists to send them to.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/course/each_runner {course:"$(course)",run:"kalic0re:parkour/admin/course/end_run"}
$function kalic0re:parkour/course/remove_times {course:"$(course)"}
$function kalic0re:parkour/admin/displays/leaderboard_remove_named {course:"$(course)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}]
$tellraw @s {translate:"kalic0re.parkour.chat.deleted",with:["$(course)"],color:"green"}
