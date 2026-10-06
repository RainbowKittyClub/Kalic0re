# The finish firework editor's Reset to Default: $(course)'s own finish firework cleared, and the editor again, as the
# default now stands.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].finishFirework
$tellraw @s {translate:"kalic0re.parkour.chat.finish_firework_reset",with:["$(course)"],color:"yellow"}
$function kalic0re:parkour/admin/course/open_finish_firework {course:"$(course)"}
