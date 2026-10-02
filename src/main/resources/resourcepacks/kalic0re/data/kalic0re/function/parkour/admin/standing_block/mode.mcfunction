# /function kalic0re:parkour/admin/standing_block/mode {course:"<name>",mode:"allow" or "deny"}
# allow: runners may stand only on the course's standing blocks. deny, the default: on anything but them.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$data modify storage kalic0re:parkour tmp.standingBlockMode set value {mode:"$(mode)"}
execute unless data storage kalic0re:parkour tmp.standingBlockMode{mode:"allow"} \
    unless data storage kalic0re:parkour tmp.standingBlockMode{mode:"deny"} run \
    return run function kalic0re:parkour/admin/standing_block/no_such_mode with storage kalic0re:parkour tmp.standingBlockMode
data remove storage kalic0re:parkour tmp.standingBlockMode
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].standingBlockMode set value "$(mode)"
$function kalic0re:parkour/admin/standing_block/rebuild_predicate {course:"$(course)"}
$execute if data storage kalic0re:parkour courses[{name:"$(course)",standingBlockMode:"allow"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.standing_blocks_allow",with:["$(course)"],color:"green"}
$tellraw @s {translate:"kalic0re.parkour.chat.standing_blocks_deny",with:["$(course)"],color:"green"}
