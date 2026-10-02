# /function kalic0re:parkour/admin/course_boundary/list {course:"<name>"}
# Numbers each kind's boundaries from 1, as remove and remove_exclusion take them.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries[0] \
    unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaries[0] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_course_boundaries",with:["$(course)"],color:"gray"}
$function kalic0re:parkour/admin/course_boundary/list_kind {course:"$(course)",boundaries:"courseBoundaries", \
    heading:"course_boundaries",remove:"remove"}
$function kalic0re:parkour/admin/course_boundary/list_kind {course:"$(course)",boundaries:"courseExclusionBoundaries", \
    heading:"exclusion_boundaries",remove:"remove_exclusion"}
