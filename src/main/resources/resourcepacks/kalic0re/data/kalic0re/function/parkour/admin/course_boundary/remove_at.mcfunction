# A boundary and its predicate term share an index: add appends both, and only this removes them.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries[$(index)] run return run \
    function kalic0re:parkour/admin/course_boundary/no_such_boundary {course:"$(course)",boundary:"$(boundary)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries[$(index)]
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate.terms[$(index)]
# With none left the course has no limit, rather than an empty any_of that every runner fails.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries[0] run \
    data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate
$tellraw @s {translate:"kalic0re.parkour.chat.course_boundary_removed",fallback:"Parkour course \"%s\": course boundary %s removed.", \
    with:["$(course)","$(boundary)"],color:"green"}
# Wand holders see the course's boundaries in white, so show them it gone.
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand] at @s run function kalic0re:parkour/admin/course_boundary/wand/outline/show
