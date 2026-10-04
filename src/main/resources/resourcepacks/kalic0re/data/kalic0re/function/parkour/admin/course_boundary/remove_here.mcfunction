# /function kalic0re:parkour/admin/course_boundary/remove_here, holding a Parkour Boundary Wand: removes the
# boundary of the wand's course around your feet. Inside more than one, chat asks which. With the wand's
# right-click looking down, the only ways a boundary is removed.
execute unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.remove_here_needs_wand",color:"red"}
function kalic0re:parkour/admin/course_boundary/wand/read_held
data modify storage kalic0re:parkour tmp.wand.held.askWhich set value "ask_which"
function kalic0re:parkour/admin/course_boundary/remove_here_on with storage kalic0re:parkour tmp.wand.held
data remove storage kalic0re:parkour tmp.wand
