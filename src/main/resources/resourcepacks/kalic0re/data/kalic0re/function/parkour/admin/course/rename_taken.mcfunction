$tellraw @s {translate:"kalic0re.parkour.chat.name_taken",fallback:"A parkour course named \"%s\" already exists. Choose another name.",with:["$(new_name)"],color:"red"}
$function kalic0re:parkour/admin/course/open {course:"$(course)",dialog:"rename"}
