# As a player who used a warped fungus on a stick; only the wand in the main hand counts. Looking straight up
# opens the Add Boundary dialog; otherwise it marks the second corner.
scoreboard players reset @s kalic0re.parkour.courseBoundaryWandRightClick
execute unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run return 0
# Both bounds: a range without a min starts at 0 and wraps, so ..-89 would take looking down too.
execute if entity @s[x_rotation=-90..-89] run return run function kalic0re:parkour/admin/course_boundary/wand/open_add_dialog
function kalic0re:parkour/admin/course_boundary/wand/click {corner:"secondCorner"}
