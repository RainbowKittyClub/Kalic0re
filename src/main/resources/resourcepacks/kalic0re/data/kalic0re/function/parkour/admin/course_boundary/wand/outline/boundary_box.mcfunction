# Twelve white edges 0.15 inside the box, apart from the yellow selection on the edge and the corner
# frames 0.15 outside. Each starts where its post's display goes: half a block back across its axis.
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~-0.35 ~0.15 ~-0.35 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_y with storage kalic0re:parkour tmp.outline.box
$execute positioned $(topX).0 $(minY).0 $(minZ).0 positioned ~-0.65 ~0.15 ~-0.35 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_y with storage kalic0re:parkour tmp.outline.box
$execute positioned $(minX).0 $(minY).0 $(topZ).0 positioned ~-0.35 ~0.15 ~-0.65 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_y with storage kalic0re:parkour tmp.outline.box
$execute positioned $(topX).0 $(minY).0 $(topZ).0 positioned ~-0.65 ~0.15 ~-0.65 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_y with storage kalic0re:parkour tmp.outline.box

$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~0.15 ~-0.35 ~-0.35 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_x with storage kalic0re:parkour tmp.outline.box
$execute positioned $(minX).0 $(topY).0 $(minZ).0 positioned ~0.15 ~-0.65 ~-0.35 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_x with storage kalic0re:parkour tmp.outline.box
$execute positioned $(minX).0 $(minY).0 $(topZ).0 positioned ~0.15 ~-0.35 ~-0.65 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_x with storage kalic0re:parkour tmp.outline.box
$execute positioned $(minX).0 $(topY).0 $(topZ).0 positioned ~0.15 ~-0.65 ~-0.65 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_x with storage kalic0re:parkour tmp.outline.box

$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~-0.35 ~-0.35 ~0.15 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_z with storage kalic0re:parkour tmp.outline.box
$execute positioned $(topX).0 $(minY).0 $(minZ).0 positioned ~-0.65 ~-0.35 ~0.15 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_z with storage kalic0re:parkour tmp.outline.box
$execute positioned $(minX).0 $(topY).0 $(minZ).0 positioned ~-0.35 ~-0.65 ~0.15 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_z with storage kalic0re:parkour tmp.outline.box
$execute positioned $(topX).0 $(topY).0 $(minZ).0 positioned ~-0.65 ~-0.65 ~0.15 run function kalic0re:parkour/admin/course_boundary/wand/outline/white_edge_z with storage kalic0re:parkour tmp.outline.box
