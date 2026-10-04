# An answer whose boundary is gone, or with no question asked; the link asks again where they stand.
tellraw @s [{translate:"kalic0re.parkour.chat.course_boundary_answer_out_of_date",color:"red"},{text:" "}, \
    {translate:"kalic0re.parkour.chat.course_boundary_ask_again",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/course_boundary/remove_here"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/course_boundary/remove_here"}}]
