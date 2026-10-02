# Why $(course) can't be used: courses are created with the New Course dialog, points set by hand.
# Warnings say what's wrong, then "Click here to …" runs the fix, which hovering shows.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run tellraw @s [ \
    {translate:"kalic0re.parkour.chat.no_such_course", \
        with:["$(course)"],color:"red"},{text:" "}, \
    {translate:"kalic0re.parkour.chat.create_it",color:"red",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/new_course_named {course:\"$(course)\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/new_course_named {course:\"$(course)\"}"}}]
$tellraw @s [{translate:"kalic0re.parkour.chat.no_point", \
        with:["$(course)",{nbt:"pointNames.$(point)",storage:"kalic0re:parkour",interpret:true}],color:"red"},{text:" "}, \
    {translate:"kalic0re.parkour.chat.set_it_here",color:"red",underlined:true,bold:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/set {course:\"$(course)\",point:\"$(point)\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/set {course:\"$(course)\",point:\"$(point)\"}"}}]
