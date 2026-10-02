# Tests the terms of $(course)'s $(boundaryPredicate) in order against this player's feet; removes the
# first that holds them, and leaves tmp.boundaryHere.found set.
$data modify storage kalic0re:parkour tmp.boundaryHere set value {course:"$(course)", \
    boundaries:"$(boundaries)",boundaryPredicate:"$(boundaryPredicate)",key:"$(key)",name:"$(name)"}
$data modify storage kalic0re:parkour tmp.boundaryHere.terms set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].$(boundaryPredicate).terms
scoreboard players set #boundaryIndex kalic0re.parkour.tmp 0
function kalic0re:parkour/admin/course_boundary/find_here
execute store result storage kalic0re:parkour tmp.boundaryHere.index int 1 run \
    scoreboard players get #boundaryIndex kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.boundaryHere.boundary int 1 run \
    scoreboard players add #boundaryIndex kalic0re.parkour.tmp 1
execute if data storage kalic0re:parkour tmp.boundaryHere.found run \
    function kalic0re:parkour/admin/course_boundary/remove_at with storage kalic0re:parkour tmp.boundaryHere
