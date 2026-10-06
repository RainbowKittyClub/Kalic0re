# The wand enchantment's post_piercing_attack: as the holder, at their feet, with their rotation. Sneaking, it
# opens the Wand Course dialog; otherwise it marks the first corner.
execute if predicate {condition:"minecraft:entity_properties",entity:"this",predicate:{flags:{is_sneaking:true}}} run \
    return run function kalic0re:parkour/admin/course_boundary/wand/open_course_dialog
function kalic0re:parkour/admin/course_boundary/wand/click {corner:"firstCorner"}
