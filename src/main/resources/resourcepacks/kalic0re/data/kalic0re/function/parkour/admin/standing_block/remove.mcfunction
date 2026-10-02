# /function kalic0re:parkour/admin/standing_block/remove {course:"<name>",block:"<block id or #tag>"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[{block:"$(block)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_standing_block", \
    with:["$(course)","$(block)"],color:"red"}
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[{block:"$(block)"}]
$function kalic0re:parkour/admin/standing_block/rebuild_predicate {course:"$(course)"}
$tellraw @s {translate:"kalic0re.parkour.chat.standing_block_removed", \
    with:["$(course)","$(block)"],color:"green"}
