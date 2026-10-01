# /function kalic0re:parkour/admin/course_boundary/remove {course:"<name>",boundary:<number from the list>}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
# Numbered from 1; a list index below 0 would count from the end.
$scoreboard players set #boundary kalic0re.parkour.tmp $(boundary)
$execute unless score #boundary kalic0re.parkour.tmp matches 1.. run return run \
    function kalic0re:parkour/admin/course_boundary/no_such_boundary {course:"$(course)",boundary:"$(boundary)"}
$data modify storage kalic0re:parkour tmp.courseBoundaryRemoval set value {course:"$(course)",boundary:"$(boundary)"}
execute store result storage kalic0re:parkour tmp.courseBoundaryRemoval.index int 1 run \
    scoreboard players remove #boundary kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/remove_at with storage kalic0re:parkour tmp.courseBoundaryRemoval
data remove storage kalic0re:parkour tmp.courseBoundaryRemoval
