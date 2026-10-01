# Leaves #boundaryIndex at the first term holding this player's feet, and tmp.boundaryHere.found set.
execute unless data storage kalic0re:parkour tmp.boundaryHere.terms[0] run return 0
data modify storage kalic0re:parkour tmp.boundaryHere.term set from storage kalic0re:parkour tmp.boundaryHere.terms[0]
function kalic0re:parkour/admin/course_boundary/test_here with storage kalic0re:parkour tmp.boundaryHere
execute if data storage kalic0re:parkour tmp.boundaryHere.found run return 0
data remove storage kalic0re:parkour tmp.boundaryHere.terms[0]
scoreboard players add #boundaryIndex kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/find_here
