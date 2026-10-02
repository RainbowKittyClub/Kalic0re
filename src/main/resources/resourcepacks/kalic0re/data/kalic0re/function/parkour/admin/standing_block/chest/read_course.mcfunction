# Course $(courseId)'s name, list, mode and burning, and its chest's record, into tmp.chest; 0 if either is gone.
$execute unless data storage kalic0re:parkour courses[{id:$(courseId)}] run return 0
$execute unless data storage kalic0re:parkour standingBlockChests[{courseId:$(courseId)}] run return 0
$data modify storage kalic0re:parkour tmp.chest.course set from storage kalic0re:parkour courses[{id:$(courseId)}].name
data modify storage kalic0re:parkour tmp.chest.stored set value []
$data modify storage kalic0re:parkour tmp.chest.stored set from storage kalic0re:parkour courses[{id:$(courseId)}].standingBlocks
data modify storage kalic0re:parkour tmp.chest.mode set value "deny"
$data modify storage kalic0re:parkour tmp.chest.mode set from storage kalic0re:parkour courses[{id:$(courseId)}].standingBlockMode
data modify storage kalic0re:parkour tmp.chest.burning set value "deny"
$data modify storage kalic0re:parkour tmp.chest.burning set from storage kalic0re:parkour courses[{id:$(courseId)}].burningMode
$data modify storage kalic0re:parkour tmp.chest.record set from storage kalic0re:parkour standingBlockChests[{courseId:$(courseId)}]
return 1
