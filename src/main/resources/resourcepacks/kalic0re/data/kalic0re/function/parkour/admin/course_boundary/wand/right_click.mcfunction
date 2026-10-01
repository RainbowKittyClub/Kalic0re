# As a player who used a warped fungus on a stick; only the wand in the main hand marks a corner.
scoreboard players reset @s kalic0re.parkour.courseBoundaryWandRightClick
execute if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run \
    function kalic0re:parkour/admin/course_boundary/wand/click {corner:"secondCorner"}
