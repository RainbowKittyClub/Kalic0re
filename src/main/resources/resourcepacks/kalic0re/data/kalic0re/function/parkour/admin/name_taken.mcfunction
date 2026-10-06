# A course name already in use: the answer, then New Course again, holding what was typed when it came from there.
$tellraw @s {translate:"kalic0re.parkour.chat.name_taken",with:["$(course)"],color:"red"}
execute if data storage kalic0re:parkour tmp.dialogTexts run return run function kalic0re:parkour/admin/new_course_as_typed
$function kalic0re:parkour/admin/new_course_named {course:"$(course)"}
