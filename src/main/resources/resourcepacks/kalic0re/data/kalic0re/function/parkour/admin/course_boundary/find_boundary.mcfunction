# Returns 1 if $(course) already has the boundary build_boundary just made, matched on its term's
# bounds: a filter on corner lists would match any list holding those numbers, in any order.
$data modify storage kalic0re:parkour tmp.newCourseBoundary.bounds set value {course:"$(course)"}
data modify storage kalic0re:parkour tmp.newCourseBoundary.bounds.position set from \
    storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position
function kalic0re:parkour/admin/course_boundary/find_boundary_bounds with storage kalic0re:parkour tmp.newCourseBoundary.bounds
return run execute if score #found kalic0re.parkour.tmp matches 1
