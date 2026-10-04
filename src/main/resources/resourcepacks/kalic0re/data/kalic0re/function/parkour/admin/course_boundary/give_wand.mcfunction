# /function kalic0re:parkour/admin/course_boundary/give_wand {course:"<name>"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$function kalic0re:parkour/admin/course_boundary/wand/lore {course:"$(course)"}
function kalic0re:parkour/admin/course_boundary/give_wand_item with storage kalic0re:parkour tmp.wandLore
data remove storage kalic0re:parkour tmp.wandLore
# Its lore names the trigger that removes a boundary, so they can use it.
scoreboard players enable @s kalic0re.admin.remove_course_boundary
