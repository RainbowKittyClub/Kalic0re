# Removes the first of $(course)'s exclusion boundaries holding this player's feet, else the first of its
# course boundaries.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
$function kalic0re:parkour/admin/course_boundary/remove_here_from {course:"$(course)", \
    boundaries:"courseExclusionBoundaries",boundaryPredicate:"courseExclusionBoundaryPredicate",key:"exclusion_boundary"}
$execute unless data storage kalic0re:parkour tmp.boundaryHere.found run \
    function kalic0re:parkour/admin/course_boundary/remove_here_from {course:"$(course)", \
    boundaries:"courseBoundaries",boundaryPredicate:"courseBoundaryPredicate",key:"course_boundary"}
$execute unless data storage kalic0re:parkour tmp.boundaryHere.found run tellraw @s {translate:"kalic0re.parkour.chat.not_in_course_boundary", \
    fallback:"You aren't inside any boundary of parkour course \"%s\".",with:["$(course)"],color:"gray"}
data remove storage kalic0re:parkour tmp.boundaryHere
