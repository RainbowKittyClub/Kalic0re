# Every boundary of $(course) holding this player's feet, exclusion boundaries first, as one usually sits
# inside a course boundary: one alone is removed, more are asked about by $(askWhich), in chat or a dialog.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
data remove storage kalic0re:parkour tmp.boundaryRemoval
$data modify storage kalic0re:parkour tmp.boundaryHere set value {course:"$(course)",found:[]}
$function kalic0re:parkour/admin/course_boundary/collect_here {course:"$(course)", \
    boundaries:"courseExclusionBoundaries",boundaryPredicate:"courseExclusionBoundaryPredicate",key:"exclusion_boundary"}
$function kalic0re:parkour/admin/course_boundary/collect_here {course:"$(course)", \
    boundaries:"courseBoundaries",boundaryPredicate:"courseBoundaryPredicate",key:"course_boundary"}
$execute unless data storage kalic0re:parkour tmp.boundaryHere.found[0] run \
    tellraw @s {translate:"kalic0re.parkour.chat.not_in_course_boundary",with:["$(course)"],color:"gray"}
execute if data storage kalic0re:parkour tmp.boundaryHere.found[0] unless data storage kalic0re:parkour tmp.boundaryHere.found[1] run \
    data modify storage kalic0re:parkour tmp.boundaryRemoval set from storage kalic0re:parkour tmp.boundaryHere.found[0]
execute if data storage kalic0re:parkour tmp.boundaryRemoval run function kalic0re:parkour/admin/course_boundary/remove_found
$execute if data storage kalic0re:parkour tmp.boundaryHere.found[1] run function kalic0re:parkour/admin/course_boundary/$(askWhich)
data remove storage kalic0re:parkour tmp.boundaryHere
