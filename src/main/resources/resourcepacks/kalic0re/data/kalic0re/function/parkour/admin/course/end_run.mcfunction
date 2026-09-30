# As an active player: ends their run if it is on $(course), which is being deleted.
data modify storage kalic0re:parkour tmp.ending.uuid set from entity @s UUID
$data modify storage kalic0re:parkour tmp.ending.course set value "$(course)"
function kalic0re:parkour/admin/course/end_run_if_on with storage kalic0re:parkour tmp.ending
