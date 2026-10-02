# A boundary and its predicate term share an index: add_boundary appends both, and only this removes them.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaries)[$(index)] run return run \
    function kalic0re:parkour/admin/course_boundary/message/no_such_$(key) {course:"$(course)",boundary:"$(boundary)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaries)[$(index)]
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaryPredicate).terms[$(index)]
# With none left, no any_of at all: an empty one holds no one, so course boundaries would fail every runner.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaries)[0] run \
    data remove storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaryPredicate)
$function kalic0re:parkour/admin/course_boundary/message/$(key)_removed {course:"$(course)",boundary:"$(boundary)"}
# Wand holders see the course's boundaries outlined, so show them it gone.
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand] at @s run function kalic0re:parkour/admin/course_boundary/wand/outline/show
