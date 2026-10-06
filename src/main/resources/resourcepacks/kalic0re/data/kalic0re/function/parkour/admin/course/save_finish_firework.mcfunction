# The finish firework editor's Save (kalic0re:firework/editor/save): course $(context)'s own finish firework, the
# edited one over it, then back to its management dialog.
$execute unless data storage kalic0re:parkour courses[{name:"$(context)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(context)"}
$data modify storage kalic0re:parkour courses[{name:"$(context)"}].finishFirework merge from storage kalic0re:firework edited
$tellraw @s {translate:"kalic0re.parkour.chat.finish_firework_saved",with:["$(context)"],color:"green"}
$function kalic0re:parkour/admin/course/manage {course:"$(context)"}
