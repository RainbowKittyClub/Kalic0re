# As boundary_edge_y along z: each post turned about x, then moved back up into its block.
$summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow), \
    brightness:{sky:15,block:15},transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f], \
    translation:[0f,1f,0f],scale:[1f,$(gz)f,1f]},Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned ~ ~ ~$(ez) run summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:$(pane)"},Glowing:1b, \
    glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,1f,0f],scale:[1f,$(gz)f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
