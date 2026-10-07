# As the display or a rider: the box it draws, if it draws one, added to the bounce's. A block display
# draws from its translation by its scale; an item display, centred on it.
execute if entity @s[type=minecraft:block_display] if data entity @s block_state{Name:"minecraft:air"} run return 0
execute if entity @s[type=minecraft:item_display] unless data entity @s item run return 0
execute unless entity @s[type=minecraft:block_display] unless entity @s[type=minecraft:item_display] run return 0
execute store success score #displayBounceCentred kalic0re.effect.tmp if entity @s[type=minecraft:item_display]
data modify storage kalic0re:effect displayBounceShape set from entity @s transformation
function kalic0re:effect/display/bounce/shape_axis {axis:"X",index:0}
function kalic0re:effect/display/bounce/shape_axis {axis:"Y",index:1}
function kalic0re:effect/display/bounce/shape_axis {axis:"Z",index:2}
scoreboard players add #displayBounceShapes kalic0re.effect.tmp 1
