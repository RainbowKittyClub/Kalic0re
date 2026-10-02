# The Rename dialog's button: refuses a name in use, then renames the course and any runs on it.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
# Keeping the name is a no-op, not a clash with itself.
$data modify storage kalic0re:parkour tmp.rename set value {name:"$(new_name)"}
$execute if data storage kalic0re:parkour tmp.rename{name:"$(course)"} run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.name_kept",with:["$(course)"],color:"gray"}
$execute if data storage kalic0re:parkour courses[{name:"$(new_name)"}] run return run \
    function kalic0re:parkour/admin/course/rename_taken {course:"$(course)",new_name:"$(new_name)"}
# Guarded: a list filter that matches nothing adds a match, which here would be a record with no uuid.
$execute if data storage kalic0re:parkour players[{course:"$(course)"}] run \
    data modify storage kalic0re:parkour players[{course:"$(course)"}].course set value "$(new_name)"
$function kalic0re:parkour/course/retitle_times {course:"$(course)",new_name:"$(new_name)"}
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].name set value "$(new_name)"
$tellraw @s {translate:"kalic0re.parkour.chat.renamed",with:["$(course)","$(new_name)"],color:"green"}
