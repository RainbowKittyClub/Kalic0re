# From the corners on #x1..#z2: tmp.newInclusionZone.zone, whole blocks as listed, and its predicate
# term, whose bounds are positions, so each max is one past its block's corner to take in the block.
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

data modify storage kalic0re:parkour tmp.newInclusionZone.zone set value {min:[0,0,0],max:[0,0,0]}
execute store result storage kalic0re:parkour tmp.newInclusionZone.zone.min[0] int 1 run scoreboard players get #minX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.zone.min[1] int 1 run scoreboard players get #minY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.zone.min[2] int 1 run scoreboard players get #minZ kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.zone.max[0] int 1 run scoreboard players get #maxX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.zone.max[1] int 1 run scoreboard players get #maxY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.zone.max[2] int 1 run scoreboard players get #maxZ kalic0re.parkour.tmp

data modify storage kalic0re:parkour tmp.newInclusionZone.term set value \
    {condition:"minecraft:location_check",predicate:{position:{x:{min:0,max:0},y:{min:0,max:0},z:{min:0,max:0}}}}
execute store result storage kalic0re:parkour tmp.newInclusionZone.term.predicate.position.x.min int 1 run \
    scoreboard players get #minX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.term.predicate.position.y.min int 1 run \
    scoreboard players get #minY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.term.predicate.position.z.min int 1 run \
    scoreboard players get #minZ kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newInclusionZone.term.predicate.position.x.max int 1 run \
    scoreboard players add #maxX kalic0re.parkour.tmp 1
execute store result storage kalic0re:parkour tmp.newInclusionZone.term.predicate.position.y.max int 1 run \
    scoreboard players add #maxY kalic0re.parkour.tmp 1
execute store result storage kalic0re:parkour tmp.newInclusionZone.term.predicate.position.z.max int 1 run \
    scoreboard players add #maxZ kalic0re.parkour.tmp 1
