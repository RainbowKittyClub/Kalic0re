# tmp.newCourseBoundary.term, from build_boundary's scores, 0.29 wider each way in x and z: a runner 0.6 wide is
# partly inside it with their feet's centre under 0.3 out, while one touching a block at its edge isn't held.
scoreboard players operation #footprintBound kalic0re.parkour.tmp = #minX kalic0re.parkour.tmp
scoreboard players operation #footprintBound kalic0re.parkour.tmp *= #100 kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.x.min double 0.01 run \
    scoreboard players remove #footprintBound kalic0re.parkour.tmp 29
scoreboard players operation #footprintBound kalic0re.parkour.tmp = #maxX kalic0re.parkour.tmp
scoreboard players operation #footprintBound kalic0re.parkour.tmp *= #100 kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.x.max double 0.01 run \
    scoreboard players add #footprintBound kalic0re.parkour.tmp 29
scoreboard players operation #footprintBound kalic0re.parkour.tmp = #minZ kalic0re.parkour.tmp
scoreboard players operation #footprintBound kalic0re.parkour.tmp *= #100 kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.z.min double 0.01 run \
    scoreboard players remove #footprintBound kalic0re.parkour.tmp 29
scoreboard players operation #footprintBound kalic0re.parkour.tmp = #maxZ kalic0re.parkour.tmp
scoreboard players operation #footprintBound kalic0re.parkour.tmp *= #100 kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.position.z.max double 0.01 run \
    scoreboard players add #footprintBound kalic0re.parkour.tmp 29
