# A time button outlived its time, cleared since the dialog opened: show the list as it is now.
$tellraw @s {translate:"kalic0re.parkour.chat.no_time",with:["$(player)","$(course)"],color:"red"}
$function kalic0re:parkour/admin/course/times {course:"$(course)"}
