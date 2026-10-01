$tellraw @s {translate:"kalic0re.parkour.chat.wand_add_boundary",fallback:"Click to add boundary: %s to %s", \
    with:["$(x1) $(y1) $(z1)","$(x2) $(y2) $(z2)"],underlined:true,bold:true,color:"yellow", \
    click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/course_boundary/add {course:\"$(course)\",x1:$(x1),y1:$(y1),z1:$(z1),x2:$(x2),y2:$(y2),z2:$(z2)}"}, \
    hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/course_boundary/add {course:\"$(course)\",x1:$(x1),y1:$(y1),z1:$(z1),x2:$(x2),y2:$(y2),z2:$(z2)}"}}
