# Player NBT names no held item, so a transient display copies the main hand's into tmp.wand.held.
tag @s add kalic0re.parkour.wandHolder
execute summon minecraft:item_display run function kalic0re:parkour/admin/course_boundary/wand/copy_held
tag @s remove kalic0re.parkour.wandHolder
