# Why $(course) can't be used: courses are created with the New Course dialog, points set by hand.
# Warnings say what's wrong, then "Click here to …" runs the fix, which hovering shows.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run tellraw @s [ \
    {text:"There is no parkour course named \"$(course)\". ",color:"red"}, \
    {text:"Click here to create it.",color:"red",underlined:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/new_course_named {course:\"$(course)\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/new_course_named {course:\"$(course)\"}"}}]
$tellraw @s [{text:"Parkour course \"$(course)\" has no $(point). ",color:"red"}, \
    {text:"Click here to set it where you stand.",color:"red",underlined:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/set {course:\"$(course)\",point:\"$(point)\"}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/set {course:\"$(course)\",point:\"$(point)\"}"}}]
