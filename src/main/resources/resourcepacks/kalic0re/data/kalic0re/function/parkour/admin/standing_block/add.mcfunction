# /function kalic0re:parkour/admin/standing_block/add {course:"<name>",block:"<block id or #tag>"}
# A course's runners may stand only on its standing blocks, or on anything but them, by its mode.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
# Parsed with the call, this stops it for a block or tag the game doesn't know, or one given with states.
$execute if predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{stepping_on:{block:{blocks:"$(block)"}}}} run \
    scoreboard players set #standingBlockParsed kalic0re.parkour.tmp 1
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[{block:"$(block)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.standing_block_exists", \
    with:["$(course)","$(block)"],color:"gray"}
# No more than the chest has slots for: 25, beside its campfire and its pane.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[24] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.standing_blocks_full",with:["$(course)","25"],color:"red"}
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks append value {block:"$(block)"}
$function kalic0re:parkour/admin/standing_block/rebuild_predicate {course:"$(course)"}
$execute if data storage kalic0re:parkour courses[{name:"$(course)",standingBlockMode:"allow"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.standing_block_allowed", \
    with:["$(course)","$(block)"],color:"green"}
$tellraw @s {translate:"kalic0re.parkour.chat.standing_block_denied", \
    with:["$(course)","$(block)"],color:"green"}
