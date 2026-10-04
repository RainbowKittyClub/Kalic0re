# The Move Start and Move Lobby dialogs' button: $(course)'s $(point) at $(position), facing $(rotation), both
# read as execute reads them, so ~ counts from where you are and the way you face, and a whole-number x or z is
# the block's centre; in your world. Checked first, since storing would make a new course of a stale name.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
data remove storage kalic0re:parkour tmp.pos
$function kalic0re:parkour/admin/course/capture_point_text {position:"$(position)",rotation:"$(rotation)"}
$execute unless data storage kalic0re:parkour tmp.pos run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.not_a_place",with:["$(position)","$(rotation)"],color:"red"}
data modify storage kalic0re:parkour tmp.pos.dim set from entity @s Dimension
$function kalic0re:parkour/admin/store_point {course:"$(course)",point:"$(point)"}
data remove storage kalic0re:parkour tmp.pos
