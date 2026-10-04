# The corner lives on the wand itself, so each wand keeps its own selection, across logouts too.
# A selection lies in one dimension: a corner in another starts a new one. A wand without one takes this.
data modify storage kalic0re:parkour tmp.wand.dim set from entity @s Dimension
execute unless data storage kalic0re:parkour tmp.wand.held.dim run \
    data modify storage kalic0re:parkour tmp.wand.held.dim set from storage kalic0re:parkour tmp.wand.dim
execute store success score #selectionChangedDimension kalic0re.parkour.tmp run \
    data modify storage kalic0re:parkour tmp.wand.held.dim set from storage kalic0re:parkour tmp.wand.dim
execute if score #selectionChangedDimension kalic0re.parkour.tmp matches 1 run data remove storage kalic0re:parkour tmp.wand.held.firstCorner
execute if score #selectionChangedDimension kalic0re.parkour.tmp matches 1 run data remove storage kalic0re:parkour tmp.wand.held.secondCorner
$data modify storage kalic0re:parkour tmp.wand.held.$(corner) set from storage kalic0re:parkour tmp.wand.pos
item modify entity @s weapon.mainhand {function:"minecraft:copy_custom_data", \
    source:{type:"minecraft:storage",source:"kalic0re:parkour"}, \
    ops:[{source:"tmp.wand.held",target:"kalic0re.parkourCourseBoundaryWand",op:"replace"}]}
execute if data storage kalic0re:parkour tmp.wand{corner:"firstCorner"} run tellraw @s {translate:"kalic0re.parkour.chat.wand_first_corner",with:[{nbt:"tmp.wand.pos[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
execute if data storage kalic0re:parkour tmp.wand{corner:"secondCorner"} run tellraw @s {translate:"kalic0re.parkour.chat.wand_second_corner",with:[{nbt:"tmp.wand.pos[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
# Only the selection changed; a click before the outline came up brings it all now.
execute if entity @s[tag=kalic0re.parkour.holdingBoundaryWand] run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/redraw_selection
execute unless entity @s[tag=kalic0re.parkour.holdingBoundaryWand] run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/draw
