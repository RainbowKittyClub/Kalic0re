# As the wand's holder, sneaking at a left-click: the Wand Course dialog, its current course named and a button
# per course that sets the held wand's.
execute unless data storage kalic0re:parkour courses[0] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_courses",color:"red"}
function kalic0re:parkour/admin/course_boundary/wand/read_held
data modify storage kalic0re:dialog args set value {}
data modify storage kalic0re:dialog args.course set from storage kalic0re:parkour tmp.wand.held.course
data remove storage kalic0re:parkour tmp.wand
function kalic0re:parkour/admin/dialog/wand_course
data modify storage kalic0re:parkour tmp.names set value []
data modify storage kalic0re:parkour tmp.names append from storage kalic0re:parkour courses[].name
function kalic0re:parkour/admin/dialog/add_wand_course_buttons
data remove storage kalic0re:parkour tmp.names
function kalic0re:dialog/show {name:"wand_course"}
