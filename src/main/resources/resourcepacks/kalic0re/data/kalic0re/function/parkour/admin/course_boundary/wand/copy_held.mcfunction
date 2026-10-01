item replace entity @s contents from entity @a[tag=kalic0re.parkour.wandHolder,limit=1] weapon.mainhand
data modify storage kalic0re:parkour tmp.wand.held set from entity @s item.components."minecraft:custom_data".kalic0re.parkourCourseBoundaryWand
kill @s
