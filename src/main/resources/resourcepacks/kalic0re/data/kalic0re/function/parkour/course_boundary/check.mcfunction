# As a runner, at their feet. $(id), from runningCourseId, finds their course without a UUID lookup.
# A course with no boundaries has no predicate, and no limit on where its runners go.
$execute if data storage kalic0re:parkour courses[{id:$(id)}].courseBoundaryPredicate run \
    function kalic0re:parkour/course_boundary/test with storage kalic0re:parkour courses[{id:$(id)}]
