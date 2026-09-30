# A time button outlived its time, cleared since the dialog opened: show the list as it is now.
$tellraw @s {text:"$(player) has no time on parkour course \"$(course)\".",color:"red"}
$function kalic0re:parkour/admin/course/times {course:"$(course)"}
