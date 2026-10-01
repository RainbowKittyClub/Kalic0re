# Four edges along each axis, at the box's outer faces ($(top*) is one past the last block). A
# display's block spans +0..1 from it, so panes shift back half a block across their axis to centre
# on the edge. ".0": positioned centres a whole-number x or z on its block.
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenX kalic0re.parkour.tmp
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~ ~-0.5 ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_x
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenX kalic0re.parkour.tmp
$execute positioned $(minX).0 $(topY).0 $(minZ).0 positioned ~ ~-0.5 ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_x
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenX kalic0re.parkour.tmp
$execute positioned $(minX).0 $(minY).0 $(topZ).0 positioned ~ ~-0.5 ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_x
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenX kalic0re.parkour.tmp
$execute positioned $(minX).0 $(topY).0 $(topZ).0 positioned ~ ~-0.5 ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_x

scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenY kalic0re.parkour.tmp
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~-0.5 ~ ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_y
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenY kalic0re.parkour.tmp
$execute positioned $(topX).0 $(minY).0 $(minZ).0 positioned ~-0.5 ~ ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_y
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenY kalic0re.parkour.tmp
$execute positioned $(minX).0 $(minY).0 $(topZ).0 positioned ~-0.5 ~ ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_y
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenY kalic0re.parkour.tmp
$execute positioned $(topX).0 $(minY).0 $(topZ).0 positioned ~-0.5 ~ ~-0.5 run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_y

scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenZ kalic0re.parkour.tmp
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~-0.5 ~-0.5 ~ run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_z
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenZ kalic0re.parkour.tmp
$execute positioned $(topX).0 $(minY).0 $(minZ).0 positioned ~-0.5 ~-0.5 ~ run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_z
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenZ kalic0re.parkour.tmp
$execute positioned $(minX).0 $(topY).0 $(minZ).0 positioned ~-0.5 ~-0.5 ~ run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_z
scoreboard players operation #edgeLeft kalic0re.parkour.tmp = #lenZ kalic0re.parkour.tmp
$execute positioned $(topX).0 $(topY).0 $(minZ).0 positioned ~-0.5 ~-0.5 ~ run function kalic0re:parkour/admin/course_boundary/wand/outline/edge_z
