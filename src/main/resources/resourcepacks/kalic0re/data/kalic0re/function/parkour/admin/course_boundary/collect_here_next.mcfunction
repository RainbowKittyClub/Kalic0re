# A boundary and its predicate term share an index, so both lists are walked together.
execute unless data storage kalic0re:parkour tmp.boundaryHere.terms[0] run return 0
data modify storage kalic0re:parkour tmp.boundaryHere.scan.term set from storage kalic0re:parkour tmp.boundaryHere.terms[0]
data modify storage kalic0re:parkour tmp.boundaryHere.scan.boundary set from storage kalic0re:parkour tmp.boundaryHere.entries[0]
function kalic0re:parkour/admin/course_boundary/test_here with storage kalic0re:parkour tmp.boundaryHere.scan
data remove storage kalic0re:parkour tmp.boundaryHere.terms[0]
data remove storage kalic0re:parkour tmp.boundaryHere.entries[0]
function kalic0re:parkour/admin/course_boundary/collect_here_next
