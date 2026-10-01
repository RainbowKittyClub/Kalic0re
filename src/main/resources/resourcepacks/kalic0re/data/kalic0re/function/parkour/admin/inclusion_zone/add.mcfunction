# /function kalic0re:parkour/admin/inclusion_zone/add {course:"<name>",x1:<x>,y1:<y>,z1:<z>,x2:<x>,y2:<y>,z2:<z>}
# Block corners, either order, both taken in; in the start's dimension. Runners' feet must stay in some
# zone, so together they need to hold the start, checkpoints and finish, and the air jumped through.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
$scoreboard players set #x1 kalic0re.parkour.tmp $(x1)
$scoreboard players set #y1 kalic0re.parkour.tmp $(y1)
$scoreboard players set #z1 kalic0re.parkour.tmp $(z1)
$scoreboard players set #x2 kalic0re.parkour.tmp $(x2)
$scoreboard players set #y2 kalic0re.parkour.tmp $(y2)
$scoreboard players set #z2 kalic0re.parkour.tmp $(z2)
function kalic0re:parkour/admin/inclusion_zone/build_zone
$data modify storage kalic0re:parkour tmp.newInclusionZone.zone.dim set from storage kalic0re:parkour courses[{name:"$(course)"}].start.dim
data modify storage kalic0re:parkour tmp.newInclusionZone.term.predicate.dimension set from \
    storage kalic0re:parkour tmp.newInclusionZone.zone.dim
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones append from storage kalic0re:parkour tmp.newInclusionZone.zone
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZonePredicate run data modify storage \
    kalic0re:parkour courses[{name:"$(course)"}].inclusionZonePredicate set value {condition:"minecraft:any_of",terms:[]}
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZonePredicate.terms append from \
    storage kalic0re:parkour tmp.newInclusionZone.term
$execute store result storage kalic0re:parkour tmp.newInclusionZone.number int 1 run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones
$tellraw @s {translate:"kalic0re.parkour.chat.inclusion_zone_added",fallback:"Parkour course \"%s\": inclusion zone %s added, from %s to %s.", \
    with:["$(course)",{nbt:"tmp.newInclusionZone.number",storage:"kalic0re:parkour"}, \
    {nbt:"tmp.newInclusionZone.zone.min[]",storage:"kalic0re:parkour",separator:" "}, \
    {nbt:"tmp.newInclusionZone.zone.max[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
data remove storage kalic0re:parkour tmp.newInclusionZone
