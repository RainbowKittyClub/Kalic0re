# As a runner, at their feet, every tick; $(id) is their runningCourseId. Out is one predicate a tick: a
# forbidden block underfoot, inside an exclusion boundary, or outside every course boundary. A course
# without rules has no predicate text and limits nothing.
# Burning, on their turn of 16, means they touched fire or lava: unless the course allows it, that ends the
# attempt like leaving the boundaries does, boundaries or not.
$execute if score @s kalic0re.parkour.runnerTickPhaseOf16 = #16ticks kalic0re.timer \
    unless data storage kalic0re:parkour courses[{id:$(id),burningMode:"allow"}] \
    if predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_on_fire:true}}} run \
    return run function kalic0re:parkour/course_boundary/burning
$execute if data storage kalic0re:parkour courses[{id:$(id)}].runnerOutOfBoundsPredicateText run \
    function kalic0re:parkour/course_boundary/test with storage kalic0re:parkour courses[{id:$(id)}]
