# As a player who used a warped fungus on a stick; only the wand in the main hand counts. Sneaking, it clears the
# selection; looking straight up opens the Add Boundary dialog, straight down removes the boundary they stand in;
# otherwise it marks the second corner.
scoreboard players reset @s kalic0re.parkour.courseBoundaryWandRightClick
execute unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run return 0
execute if predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_sneaking:true}}} run \
    return run function kalic0re:parkour/admin/course_boundary/wand/clear_selection
# Both bounds: a range without a min starts at 0 and wraps, so ..-89 would take looking down too.
execute if entity @s[x_rotation=-90..-89] run return run function kalic0re:parkour/admin/course_boundary/wand/open_add_dialog
execute if entity @s[x_rotation=89..90] run return run function kalic0re:parkour/admin/course_boundary/wand/remove_boundary_here
function kalic0re:parkour/admin/course_boundary/wand/click {corner:"secondCorner"}
