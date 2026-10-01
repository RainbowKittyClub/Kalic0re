$tellraw @s {translate:"kalic0re.parkour.chat.name_taken",fallback:"A parkour course named \"%s\" already exists. Choose another name.",with:["$(course)"],color:"red"}
$function kalic0re:parkour/admin/new_course_named {course:"$(course)"}
