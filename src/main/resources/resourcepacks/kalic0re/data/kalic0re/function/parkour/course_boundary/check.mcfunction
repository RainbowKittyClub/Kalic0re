# As a runner, at their feet, every tick; $(id) is their runningCourseId. Out is a forbidden block underfoot,
# any tick, as a hop touches one for a tick or two; and on their turn of 16, inside an exclusion boundary or
# outside every course boundary. A rule the course lacks has no predicate text and limits nothing.
scoreboard players set #runnerOutOfBounds kalic0re.parkour.tmp 0
$execute if data storage kalic0re:parkour courses[{id:$(id)}].forbiddenStandingBlockPredicateText run \
    function kalic0re:parkour/standing_block/test with storage kalic0re:parkour courses[{id:$(id)}]
$execute if score #runnerOutOfBounds kalic0re.parkour.tmp matches 0 \
    if score @s kalic0re.parkour.runnerTickPhaseOf16 = #16ticks kalic0re.timer \
    if data storage kalic0re:parkour courses[{id:$(id)}].courseExclusionBoundaryPredicateText run \
    function kalic0re:parkour/course_boundary/test_exclusion with storage kalic0re:parkour courses[{id:$(id)}]
$execute if score #runnerOutOfBounds kalic0re.parkour.tmp matches 0 \
    if score @s kalic0re.parkour.runnerTickPhaseOf16 = #16ticks kalic0re.timer \
    if data storage kalic0re:parkour courses[{id:$(id)}].courseBoundaryPredicateText run \
    function kalic0re:parkour/course_boundary/test with storage kalic0re:parkour courses[{id:$(id)}]
execute if score #runnerOutOfBounds kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
