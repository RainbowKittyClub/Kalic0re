# For create_from_dialog: refuses a name already in use, then $(course) with its start and lobby at the places read,
# in your world.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/name_taken {course:"$(course)"}
data remove storage kalic0re:parkour tmp.pos
$function kalic0re:parkour/admin/course/capture_point_text {position:"$(startPosition)",rotation:"$(startRotation)"}
data modify storage kalic0re:parkour tmp.pos.dim set from entity @s Dimension
$function kalic0re:parkour/admin/store_point {course:"$(course)",point:"start"}
data remove storage kalic0re:parkour tmp.pos
$function kalic0re:parkour/admin/course/capture_point_text {position:"$(lobbyPosition)",rotation:"$(lobbyRotation)"}
data modify storage kalic0re:parkour tmp.pos.dim set from entity @s Dimension
$function kalic0re:parkour/admin/store_point {course:"$(course)",point:"lobby"}
data remove storage kalic0re:parkour tmp.pos
