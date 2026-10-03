execute unless data storage kalic0re:parkour tmp.boundaryRemoval.terms[0] run return 0
# Setting a copy of the next term to the one sought changes nothing, and so fails, only when they're equal.
data modify storage kalic0re:parkour tmp.boundaryRemoval.compared set from storage kalic0re:parkour tmp.boundaryRemoval.terms[0]
execute store success score #termDiffers kalic0re.parkour.tmp run \
    data modify storage kalic0re:parkour tmp.boundaryRemoval.compared set from storage kalic0re:parkour tmp.boundaryRemoval.term
execute if score #termDiffers kalic0re.parkour.tmp matches 0 run return run \
    execute store result storage kalic0re:parkour tmp.boundaryRemoval.index int 1 run \
    scoreboard players get #boundaryIndex kalic0re.parkour.tmp
data remove storage kalic0re:parkour tmp.boundaryRemoval.terms[0]
scoreboard players add #boundaryIndex kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/course_boundary/find_term_next
