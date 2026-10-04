# The held wand's lore set to $(lore), and its name to the wand's, so one named before a rename catches up too.
$item modify entity @s weapon.mainhand [{function:"minecraft:set_lore",mode:"replace_all",lore:$(lore)}, \
    {function:"minecraft:set_name",target:"item_name",name:{translate:"kalic0re.parkour.item.course_boundary_wand"}}]
