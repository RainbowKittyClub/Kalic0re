# As a runner, at their feet. $(id), from runningCourseId, finds their course without a UUID lookup.
# Out is inside any exclusion boundary, or outside every course boundary; a kind the course lacks has no
# predicate and limits nothing. Out goes where the checkpoint item would, the timer running on.
scoreboard players set #runnerOutOfBounds kalic0re.parkour.tmp 0
$execute if data storage kalic0re:parkour courses[{id:$(id)}].courseExclusionBoundaryPredicate run \
    function kalic0re:parkour/course_boundary/test_exclusion with storage kalic0re:parkour courses[{id:$(id)}]
$execute if score #runnerOutOfBounds kalic0re.parkour.tmp matches 0 \
    if data storage kalic0re:parkour courses[{id:$(id)}].courseBoundaryPredicate run \
    function kalic0re:parkour/course_boundary/test with storage kalic0re:parkour courses[{id:$(id)}]
execute if score #runnerOutOfBounds kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
