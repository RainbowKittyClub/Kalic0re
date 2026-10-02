# /function kalic0re:parkour/admin/standing_block/list {course:"<name>"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[0] run return run tellraw @s [ \
    {translate:"kalic0re.parkour.chat.no_standing_blocks",with:["$(course)"],color:"gray"},{text:" "}, \
    {translate:"kalic0re.parkour.chat.standing_block_chest_add",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/standing_block/chest {course:\"$(course)\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/standing_block/chest {course:\"$(course)\"}"}}]
$execute if data storage kalic0re:parkour courses[{name:"$(course)",standingBlockMode:"allow"}] run tellraw @s [ \
    {translate:"kalic0re.parkour.chat.standing_blocks_allowed", \
        with:["$(course)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.standing_blocks_make_denied",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/standing_block/mode {course:\"$(course)\",mode:\"deny\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/standing_block/mode {course:\"$(course)\",mode:\"deny\"}"}}]
$execute unless data storage kalic0re:parkour courses[{name:"$(course)",standingBlockMode:"allow"}] run tellraw @s [ \
    {translate:"kalic0re.parkour.chat.standing_blocks_denied", \
        with:["$(course)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.standing_blocks_make_allowed",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/standing_block/mode {course:\"$(course)\",mode:\"allow\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/standing_block/mode {course:\"$(course)\",mode:\"allow\"}"}}]
$data modify storage kalic0re:parkour tmp.standingBlockList set value {course:"$(course)"}
$data modify storage kalic0re:parkour tmp.standingBlockList.blocks set from storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks
function kalic0re:parkour/admin/standing_block/list_next
data remove storage kalic0re:parkour tmp.standingBlockList
$tellraw @s {translate:"kalic0re.parkour.chat.standing_block_chest_edit",underlined:true,bold:true, \
    click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/standing_block/chest {course:\"$(course)\"}"}, \
    hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/standing_block/chest {course:\"$(course)\"}"}}
