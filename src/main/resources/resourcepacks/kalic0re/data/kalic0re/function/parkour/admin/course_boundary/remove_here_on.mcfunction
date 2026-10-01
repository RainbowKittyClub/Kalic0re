# Tests $(course)'s boundaries' terms in order against this player's feet; removes the first that holds them.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
$data modify storage kalic0re:parkour tmp.boundaryHere set value {course:"$(course)"}
$data modify storage kalic0re:parkour tmp.boundaryHere.terms set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate.terms
scoreboard players set #boundaryIndex kalic0re.parkour.tmp 0
function kalic0re:parkour/admin/course_boundary/find_here
$execute unless data storage kalic0re:parkour tmp.boundaryHere.found run tellraw @s {translate:"kalic0re.parkour.chat.not_in_course_boundary", \
    fallback:"You aren't inside any course boundary of parkour course \"%s\".",with:["$(course)"],color:"gray"}
execute store result storage kalic0re:parkour tmp.boundaryHere.index int 1 run \
    scoreboard players get #boundaryIndex kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.boundaryHere.boundary int 1 run \
    scoreboard players add #boundaryIndex kalic0re.parkour.tmp 1
execute if data storage kalic0re:parkour tmp.boundaryHere.found run \
    function kalic0re:parkour/admin/course_boundary/remove_at with storage kalic0re:parkour tmp.boundaryHere
data remove storage kalic0re:parkour tmp.boundaryHere
