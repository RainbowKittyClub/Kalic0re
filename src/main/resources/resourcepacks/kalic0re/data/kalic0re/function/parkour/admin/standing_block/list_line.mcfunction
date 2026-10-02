$tellraw @s [{translate:"kalic0re.parkour.chat.standing_block_line",with:["$(block)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.remove_it",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/standing_block/remove {course:\"$(course)\",block:\"$(block)\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/standing_block/remove {course:\"$(course)\",block:\"$(block)\"}"}}]
