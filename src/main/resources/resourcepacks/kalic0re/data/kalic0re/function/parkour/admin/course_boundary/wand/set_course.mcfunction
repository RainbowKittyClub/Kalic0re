# The Wand Course dialog's buttons: the held wand's course set to $(course), and its lore's first line; its
# selection kept. Its outline is redrawn for the new course.
execute unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.wand_not_held",color:"red"}
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",with:["$(course)"],color:"red"}
$item modify entity @s weapon.mainhand [{function:"minecraft:set_custom_data",tag:{kalic0re:{parkourCourseBoundaryWand:{course:"$(course)"}}}}, \
    {function:"minecraft:set_lore",mode:"replace_section",offset:0,size:1, \
        lore:[{translate:"kalic0re.parkour.item.course_boundary_wand.course",with:["$(course)"],color:"gray",italic:false}]}]
$tellraw @s {translate:"kalic0re.parkour.chat.wand_course_set",with:["$(course)"],color:"green"}
execute if entity @s[tag=kalic0re.parkour.holdingBoundaryWand] at @s run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/show
