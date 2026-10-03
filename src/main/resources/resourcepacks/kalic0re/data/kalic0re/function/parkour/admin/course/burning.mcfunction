# /function kalic0re:parkour/admin/course/burning {course:"<name>",mode:"allow" or "deny"}
# deny, the default: a runner who catches fire, from fire or lava, goes back to their checkpoint. allow: they burn on.
# Silent when done: the standing-blocks chest shows it.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$data modify storage kalic0re:parkour tmp.burningMode set value {mode:"$(mode)"}
execute unless data storage kalic0re:parkour tmp.burningMode{mode:"allow"} \
    unless data storage kalic0re:parkour tmp.burningMode{mode:"deny"} run \
    return run function kalic0re:parkour/admin/course/no_such_burning_mode with storage kalic0re:parkour tmp.burningMode
data remove storage kalic0re:parkour tmp.burningMode
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].burningMode set value "$(mode)"
