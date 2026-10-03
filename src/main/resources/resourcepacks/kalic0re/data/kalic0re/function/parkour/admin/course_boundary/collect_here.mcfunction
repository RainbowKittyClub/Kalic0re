# Appends each of $(course)'s $(boundaries) holding this player's feet to tmp.boundaryHere.found, with all
# remove_found needs: its course, kind and predicate term, and the boundary itself for the messages.
$data modify storage kalic0re:parkour tmp.boundaryHere.scan set value {course:"$(course)", \
    boundaries:"$(boundaries)",boundaryPredicate:"$(boundaryPredicate)",key:"$(key)"}
$data modify storage kalic0re:parkour tmp.boundaryHere.terms set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaryPredicate).terms
$data modify storage kalic0re:parkour tmp.boundaryHere.entries set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaries)
function kalic0re:parkour/admin/course_boundary/collect_here_next
data remove storage kalic0re:parkour tmp.boundaryHere.scan
data remove storage kalic0re:parkour tmp.boundaryHere.terms
data remove storage kalic0re:parkour tmp.boundaryHere.entries
