# As white_edge_y along x: each post turned about z, then moved back by its own length.
$summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:white_stained_glass_pane"},Glowing:1b,glow_color_override:16777215, \
    brightness:{sky:15,block:15},transformation:{left_rotation:[0f,0f,0.70710677f,0.70710677f],right_rotation:[0f,0f,0f,1f], \
    translation:[$(gx)f,0f,0f],scale:[1f,$(gx)f,1f]},Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned ~$(ex) ~ ~ run summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:white_stained_glass_pane"},Glowing:1b, \
    glow_color_override:16777215,brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0.70710677f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[$(gx)f,0f,0f],scale:[1f,$(gx)f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
