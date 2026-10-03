# ask_which's line for an exclusion boundary, from tmp.boundaryHere.found[0].
$tellraw @s [{translate:"kalic0re.parkour.chat.exclusion_boundary_choice",with:[ \
        {nbt:"tmp.boundaryHere.found[0].boundary.min[]",storage:"kalic0re:parkour",separator:" "}, \
        {nbt:"tmp.boundaryHere.found[0].boundary.max[]",storage:"kalic0re:parkour",separator:" "},"$(dim)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.remove_it",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/trigger kalic0re.admin.remove_course_boundary set $(answer)"}, \
        hover_event:{action:"show_text",value:"/trigger kalic0re.admin.remove_course_boundary set $(answer)"}}]
