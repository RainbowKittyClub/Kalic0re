# From the corners on #x1..#z2: tmp.newCourseBoundary.boundary, whole blocks as listed, and its
# predicate term, whose bounds are positions, so each max is one past its block to take it all in.
scoreboard players operation #minX kalic0re.parkour.tmp = #x1 kalic0re.parkour.tmp
scoreboard players operation #minX kalic0re.parkour.tmp < #x2 kalic0re.parkour.tmp
scoreboard players operation #maxX kalic0re.parkour.tmp = #x1 kalic0re.parkour.tmp
scoreboard players operation #maxX kalic0re.parkour.tmp > #x2 kalic0re.parkour.tmp
scoreboard players operation #minY kalic0re.parkour.tmp = #y1 kalic0re.parkour.tmp
scoreboard players operation #minY kalic0re.parkour.tmp < #y2 kalic0re.parkour.tmp
scoreboard players operation #maxY kalic0re.parkour.tmp = #y1 kalic0re.parkour.tmp
scoreboard players operation #maxY kalic0re.parkour.tmp > #y2 kalic0re.parkour.tmp
scoreboard players operation #minZ kalic0re.parkour.tmp = #z1 kalic0re.parkour.tmp
scoreboard players operation #minZ kalic0re.parkour.tmp < #z2 kalic0re.parkour.tmp
scoreboard players operation #maxZ kalic0re.parkour.tmp = #z1 kalic0re.parkour.tmp
scoreboard players operation #maxZ kalic0re.parkour.tmp > #z2 kalic0re.parkour.tmp

data modify storage kalic0re:parkour tmp.newCourseBoundary.boundary set value {min:[0,0,0],max:[0,0,0]}
execute store result storage kalic0re:parkour tmp.newCourseBoundary.boundary.min[0] int 1 run scoreboard players get #minX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.boundary.min[1] int 1 run scoreboard players get #minY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.boundary.min[2] int 1 run scoreboard players get #minZ kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.boundary.max[0] int 1 run scoreboard players get #maxX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.boundary.max[1] int 1 run scoreboard players get #maxY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.boundary.max[2] int 1 run scoreboard players get #maxZ kalic0re.parkour.tmp

data modify storage kalic0re:parkour tmp.newCourseBoundary.term set value \
    {condition:"minecraft:location_check",predicate:{position:{x:{min:0,max:0},y:{min:0,max:0},z:{min:0,max:0}}}}
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.x.min int 1 run \
    scoreboard players get #minX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.y.min int 1 run \
    scoreboard players get #minY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.z.min int 1 run \
    scoreboard players get #minZ kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.x.max int 1 run \
    scoreboard players add #maxX kalic0re.parkour.tmp 1
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.y.max int 1 run \
    scoreboard players add #maxY kalic0re.parkour.tmp 1
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.z.max int 1 run \
    scoreboard players add #maxZ kalic0re.parkour.tmp 1
