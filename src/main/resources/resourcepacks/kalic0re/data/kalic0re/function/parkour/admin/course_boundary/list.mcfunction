# /function kalic0re:parkour/admin/course_boundary/list {course:"<name>"}
# Numbers each boundary from 1, as remove takes it.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries[0] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_course_boundaries", \
    fallback:"Parkour course \"%s\" has no course boundaries, so its runners may go anywhere.",with:["$(course)"],color:"gray"}
$tellraw @s {translate:"kalic0re.parkour.chat.course_boundaries",fallback:"Course boundaries of parkour course \"%s\":",with:["$(course)"]}
$data modify storage kalic0re:parkour tmp.courseBoundaryList set value {course:"$(course)",boundary:1}
$data modify storage kalic0re:parkour tmp.courseBoundaryList.boundaries set from storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries
function kalic0re:parkour/admin/course_boundary/list_next
data remove storage kalic0re:parkour tmp.courseBoundaryList
