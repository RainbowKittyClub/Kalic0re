execute unless data storage kalic0re:parkour tmp.inclusionZoneList.zones[0] run return 0
# An nbt component would print the dimension quoted, as SNBT; a macro puts a string in bare.
data modify storage kalic0re:parkour tmp.inclusionZoneList.dim set value ""
data modify storage kalic0re:parkour tmp.inclusionZoneList.dim set from storage kalic0re:parkour tmp.inclusionZoneList.zones[0].dim
function kalic0re:parkour/admin/inclusion_zone/list_line with storage kalic0re:parkour tmp.inclusionZoneList
data remove storage kalic0re:parkour tmp.inclusionZoneList.zones[0]
execute store result score #zone kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.inclusionZoneList.zone
execute store result storage kalic0re:parkour tmp.inclusionZoneList.zone int 1 run scoreboard players add #zone kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/inclusion_zone/list_next
