# As a player: runs $(run) {uuid,course} if they're running $(course).
data modify storage kalic0re:parkour tmp.runner.uuid set from entity @s UUID
$data modify storage kalic0re:parkour tmp.runner.course set value "$(course)"
$data modify storage kalic0re:parkour tmp.runner.run set value "$(run)"
function kalic0re:parkour/admin/course/runner_on with storage kalic0re:parkour tmp.runner
