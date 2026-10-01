# /function kalic0re:parkour/admin/inclusion_zone/remove {course:"<name>",zone:<number from the list>}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
# Numbered from 1; a list index below 0 would count from the end.
$scoreboard players set #zone kalic0re.parkour.tmp $(zone)
$execute unless score #zone kalic0re.parkour.tmp matches 1.. run return run \
    function kalic0re:parkour/admin/inclusion_zone/no_such_zone {course:"$(course)",zone:"$(zone)"}
$data modify storage kalic0re:parkour tmp.inclusionZoneRemoval set value {course:"$(course)",zone:"$(zone)"}
execute store result storage kalic0re:parkour tmp.inclusionZoneRemoval.index int 1 run \
    scoreboard players remove #zone kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/inclusion_zone/remove_at with storage kalic0re:parkour tmp.inclusionZoneRemoval
data remove storage kalic0re:parkour tmp.inclusionZoneRemoval
