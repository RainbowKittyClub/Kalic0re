# The zone and its predicate term share an index, since add appends both and only this removes them.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones[$(index)] run return run \
    function kalic0re:parkour/admin/inclusion_zone/no_such_zone {course:"$(course)",zone:"$(zone)"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones[$(index)]
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZonePredicate.terms[$(index)]
# With none left the course has no limit, rather than an empty any_of that every runner fails.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones[0] run \
    data remove storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZonePredicate
$tellraw @s {translate:"kalic0re.parkour.chat.inclusion_zone_removed",fallback:"Parkour course \"%s\": inclusion zone %s removed.", \
    with:["$(course)","$(zone)"],color:"green"}
# The zones after it move up one, so show the numbers as they are now.
$function kalic0re:parkour/admin/inclusion_zone/list {course:"$(course)"}
