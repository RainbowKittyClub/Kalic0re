# ask_which's line for a course boundary, from tmp.boundaryHere.found[0].
$tellraw @s [{translate:"kalic0re.parkour.chat.course_boundary_choice",with:[ \
        {nbt:"tmp.boundaryHere.found[0].boundary.min[]",storage:"kalic0re:parkour",separator:" "}, \
        {nbt:"tmp.boundaryHere.found[0].boundary.max[]",storage:"kalic0re:parkour",separator:" "},"$(dim)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.remove_it",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/course_boundary/remove_answer {index:$(index)}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/course_boundary/remove_answer {index:$(index)}"}}]
