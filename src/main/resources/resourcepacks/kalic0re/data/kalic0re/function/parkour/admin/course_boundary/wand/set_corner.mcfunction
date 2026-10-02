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
execute if data storage kalic0re:parkour tmp.wand{corner:"firstCorner"} run tellraw @s {translate:"kalic0re.parkour.chat.wand_first_corner", \
    fallback:"First corner set at %s.",with:[{nbt:"tmp.wand.pos[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
execute if data storage kalic0re:parkour tmp.wand{corner:"secondCorner"} run tellraw @s {translate:"kalic0re.parkour.chat.wand_second_corner", \
    fallback:"Second corner set at %s.",with:[{nbt:"tmp.wand.pos[]",storage:"kalic0re:parkour",separator:" "}],color:"green"}
# Only the selection changed; a click before the outline came up brings it all now.
execute if entity @s[tag=kalic0re.parkour.holdingBoundaryWand] run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/redraw_selection
execute unless entity @s[tag=kalic0re.parkour.holdingBoundaryWand] run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/draw
execute unless data storage kalic0re:parkour tmp.wand.held.firstCorner run return 0
execute unless data storage kalic0re:parkour tmp.wand.held.secondCorner run return 0
data modify storage kalic0re:parkour tmp.wand.args set value {}
data modify storage kalic0re:parkour tmp.wand.args.course set from storage kalic0re:parkour tmp.wand.held.course
data modify storage kalic0re:parkour tmp.wand.args.dim set from storage kalic0re:parkour tmp.wand.held.dim
data modify storage kalic0re:parkour tmp.wand.args.x1 set from storage kalic0re:parkour tmp.wand.held.firstCorner[0]
data modify storage kalic0re:parkour tmp.wand.args.y1 set from storage kalic0re:parkour tmp.wand.held.firstCorner[1]
data modify storage kalic0re:parkour tmp.wand.args.z1 set from storage kalic0re:parkour tmp.wand.held.firstCorner[2]
data modify storage kalic0re:parkour tmp.wand.args.x2 set from storage kalic0re:parkour tmp.wand.held.secondCorner[0]
data modify storage kalic0re:parkour tmp.wand.args.y2 set from storage kalic0re:parkour tmp.wand.held.secondCorner[1]
data modify storage kalic0re:parkour tmp.wand.args.z2 set from storage kalic0re:parkour tmp.wand.held.secondCorner[2]
# Sneaking at the click offers an exclusion boundary instead.
execute unless predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_sneaking:true}}} run \
    return run function kalic0re:parkour/admin/course_boundary/wand/offer_add with storage kalic0re:parkour tmp.wand.args
function kalic0re:parkour/admin/course_boundary/wand/offer_add_exclusion with storage kalic0re:parkour tmp.wand.args
