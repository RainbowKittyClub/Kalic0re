# /function kalic0re:parkour/admin/course_boundary/remove_here, holding a Parkour Boundary Wand: removes
# the boundary of the wand's course around your feet. Exclusion boundaries first, as one usually sits
# inside a course boundary; inside overlapping ones, the lowest-numbered.
execute unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.remove_here_needs_wand", \
    fallback:"Hold a Parkour Boundary Wand: the boundary removed is one of its course's.",color:"red"}
function kalic0re:parkour/admin/course_boundary/wand/read_held
function kalic0re:parkour/admin/course_boundary/remove_here_on with storage kalic0re:parkour tmp.wand.held
data remove storage kalic0re:parkour tmp.wand
