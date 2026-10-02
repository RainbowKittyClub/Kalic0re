# As offer_add, in the exclusion boundaries' orange.
$tellraw @s {translate:"kalic0re.parkour.chat.wand_add_exclusion_boundary",fallback:"Click to add exclusion boundary: %s to %s in %s", \
    with:["$(x1) $(y1) $(z1)","$(x2) $(y2) $(z2)","$(dim)"],underlined:true,bold:true,color:"#F9801D", \
    click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/course_boundary/add_exclusion {course:\"$(course)\",dim:\"$(dim)\",x1:$(x1),y1:$(y1),z1:$(z1),x2:$(x2),y2:$(y2),z2:$(z2)}"}, \
    hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/course_boundary/add_exclusion {course:\"$(course)\",dim:\"$(dim)\",x1:$(x1),y1:$(y1),z1:$(z1),x2:$(x2),y2:$(y2),z2:$(z2)}"}}
