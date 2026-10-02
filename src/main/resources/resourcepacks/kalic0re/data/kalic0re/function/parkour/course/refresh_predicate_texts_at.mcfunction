$execute unless data storage kalic0re:parkour courses[$(index)] run return 0
data modify storage kalic0re:parkour tmp.predicateTexts set value {}
$data modify storage kalic0re:parkour tmp.predicateTexts.course set from storage kalic0re:parkour courses[$(index)].name
function kalic0re:parkour/admin/standing_block/rebuild_predicate with storage kalic0re:parkour tmp.predicateTexts
$scoreboard players set #course kalic0re.parkour.tmp $(index)
execute store result storage kalic0re:parkour tmp.predicateTexts.index int 1 run scoreboard players add #course kalic0re.parkour.tmp 1
function kalic0re:parkour/course/refresh_predicate_texts_at with storage kalic0re:parkour tmp.predicateTexts
