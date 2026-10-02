# A dialog outlived its course, renamed or deleted since it opened: show the list as it is now.
$tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
function kalic0re:parkour/admin/courses
