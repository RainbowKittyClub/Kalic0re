# Sets tmp.boundaryRemoval.index to where tmp.boundaryRemoval.term is among $(course)'s $(boundaryPredicate)
# terms; leaves it unset when the course or the term is gone.
$data modify storage kalic0re:parkour tmp.boundaryRemoval.terms set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaryPredicate).terms
scoreboard players set #boundaryIndex kalic0re.parkour.tmp 0
function kalic0re:parkour/admin/course_boundary/find_term_next
data remove storage kalic0re:parkour tmp.boundaryRemoval.terms
data remove storage kalic0re:parkour tmp.boundaryRemoval.compared
