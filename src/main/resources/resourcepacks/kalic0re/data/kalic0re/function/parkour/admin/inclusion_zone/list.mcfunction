# /function kalic0re:parkour/admin/inclusion_zone/list {course:"<name>"}: each zone numbered from 1, as remove takes it.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones[0] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_inclusion_zones", \
    fallback:"Parkour course \"%s\" has no inclusion zones, so its runners may go anywhere.",with:["$(course)"],color:"gray"}
$tellraw @s {translate:"kalic0re.parkour.chat.inclusion_zones",fallback:"Parkour course \"%s\" inclusion zones:",with:["$(course)"]}
$data modify storage kalic0re:parkour tmp.inclusionZoneList set value {course:"$(course)",zone:1}
$data modify storage kalic0re:parkour tmp.inclusionZoneList.zones set from storage kalic0re:parkour courses[{name:"$(course)"}].inclusionZones
function kalic0re:parkour/admin/inclusion_zone/list_next
data remove storage kalic0re:parkour tmp.inclusionZoneList
