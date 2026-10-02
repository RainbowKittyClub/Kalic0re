# /function kalic0re:parkour/admin/course_boundary/add {course:"<name>",dim:"<dimension>",x1:<x>,y1:<y>,z1:<z>,x2:<x>,y2:<y>,z2:<z>}
# Block corners, either order, both taken in. Runners' feet must stay in some boundary, so together
# they must hold start, checkpoints, finish and the air jumped through.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
# An unknown dimension fails `in` before the score is set; the predicate would take it and hold no one.
scoreboard players set #dimensionExists kalic0re.parkour.tmp 0
$execute in $(dim) run scoreboard players set #dimensionExists kalic0re.parkour.tmp 1
$execute unless score #dimensionExists kalic0re.parkour.tmp matches 1 run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_dimension",fallback:"There is no dimension \"%s\".",with:["$(dim)"],color:"red"}
$scoreboard players set #x1 kalic0re.parkour.tmp $(x1)
$scoreboard players set #y1 kalic0re.parkour.tmp $(y1)
$scoreboard players set #z1 kalic0re.parkour.tmp $(z1)
$scoreboard players set #x2 kalic0re.parkour.tmp $(x2)
$scoreboard players set #y2 kalic0re.parkour.tmp $(y2)
$scoreboard players set #z2 kalic0re.parkour.tmp $(z2)
function kalic0re:parkour/admin/course_boundary/build_boundary
$data modify storage kalic0re:parkour tmp.newCourseBoundary.boundary.dim set value "$(dim)"
$data modify storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.dimension set value "$(dim)"
# A wand's link can be clicked twice; the same boundary again would only add a test.
$execute store result score #duplicate kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course_boundary/find_boundary {course:"$(course)"}
execute if score #duplicate kalic0re.parkour.tmp matches 1.. run data remove storage kalic0re:parkour tmp.newCourseBoundary
$execute if score #duplicate kalic0re.parkour.tmp matches 1.. run return run tellraw @s {translate:"kalic0re.parkour.chat.course_boundary_exists", \
    fallback:"Parkour course \"%s\" already has that course boundary.",with:["$(course)"],color:"gray"}
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries append from storage kalic0re:parkour tmp.newCourseBoundary.boundary
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate run data modify storage \
    kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate set value {condition:"minecraft:any_of",terms:[]}
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate.terms append from \
    storage kalic0re:parkour tmp.newCourseBoundary.term
$execute store result storage kalic0re:parkour tmp.newCourseBoundary.number int 1 run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaries
$tellraw @s {translate:"kalic0re.parkour.chat.course_boundary_added",fallback:"Parkour course \"%s\": course boundary %s added, from %s to %s.", \
    with:["$(course)",{nbt:"tmp.newCourseBoundary.number",storage:"kalic0re:parkour"}, \
    {nbt:"tmp.newCourseBoundary.boundary.min[]",storage:"kalic0re:parkour",separator:" "}, \
    {nbt:"tmp.newCourseBoundary.boundary.max[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
data remove storage kalic0re:parkour tmp.newCourseBoundary
# Wand holders see the course's boundaries in white, so show them the new one.
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand] at @s run function kalic0re:parkour/admin/course_boundary/wand/outline/show
