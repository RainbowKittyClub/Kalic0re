$tellraw @s [{translate:"kalic0re.parkour.chat.course_boundary_line",with:["$(boundary)", \
        {nbt:"tmp.courseBoundaryList.boundaries[0].min[]",storage:"kalic0re:parkour",separator:" "}, \
        {nbt:"tmp.courseBoundaryList.boundaries[0].max[]",storage:"kalic0re:parkour",separator:" "}, \
        "$(dim)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.remove_it",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/course_boundary/$(remove) {course:\"$(course)\",boundary:$(boundary)}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/course_boundary/$(remove) {course:\"$(course)\",boundary:$(boundary)}"}}]
