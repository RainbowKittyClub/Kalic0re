# As a runner, at their feet: $(id) is their course's, from runningCourseId, so no UUID lookup each tick.
# A course with no zones has no predicate, and no limit on where its runners go.
$execute if data storage kalic0re:parkour courses[{id:$(id)}].inclusionZonePredicate run \
    function kalic0re:parkour/inclusion_zone/test with storage kalic0re:parkour courses[{id:$(id)}]
