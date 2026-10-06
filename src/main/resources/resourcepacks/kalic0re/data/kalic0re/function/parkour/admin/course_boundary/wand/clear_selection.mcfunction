# As the wand's holder, sneaking: the held wand's selection gone, both corners and its dimension, and its
# outline with it.
function kalic0re:parkour/admin/course_boundary/wand/read_held
data remove storage kalic0re:parkour tmp.wand.held.firstCorner
data remove storage kalic0re:parkour tmp.wand.held.secondCorner
data remove storage kalic0re:parkour tmp.wand.held.dim
item modify entity @s weapon.mainhand {function:"minecraft:copy_custom_data", \
    source:{type:"minecraft:storage",source:"kalic0re:parkour"}, \
    ops:[{source:"tmp.wand.held",target:"kalic0re.parkourCourseBoundaryWand",op:"replace"}]}
tellraw @s {translate:"kalic0re.parkour.chat.wand_selection_cleared",color:"green"}
execute if entity @s[tag=kalic0re.parkour.holdingBoundaryWand] run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/redraw_selection
data remove storage kalic0re:parkour tmp.wand
