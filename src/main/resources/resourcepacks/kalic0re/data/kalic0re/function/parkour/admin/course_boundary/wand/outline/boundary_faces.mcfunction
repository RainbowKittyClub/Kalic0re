# One unlit white pane per face of the inset box. A pane joined both ways is a panel 0.125 thick through
# its block's middle: scaled 0.08 that's 0.01, centred back on the face by 0.04. A face standing at its
# low corner spans W, H or D; the top and bottom turn an east-west panel flat about x.
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~0.15 ~0.15 ~0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:white_stained_glass_pane",Properties:{east:"true",west:"true"}},brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,-0.04f],scale:[$(w)f,$(h)f,0.08f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(minX).0 $(minY).0 $(topZ).0 positioned ~0.15 ~0.15 ~-0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:white_stained_glass_pane",Properties:{east:"true",west:"true"}},brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,-0.04f],scale:[$(w)f,$(h)f,0.08f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~0.15 ~0.15 ~0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:white_stained_glass_pane",Properties:{north:"true",south:"true"}},brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[-0.04f,0f,0f],scale:[0.08f,$(h)f,$(d)f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(topX).0 $(minY).0 $(minZ).0 positioned ~-0.15 ~0.15 ~0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:white_stained_glass_pane",Properties:{north:"true",south:"true"}},brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[-0.04f,0f,0f],scale:[0.08f,$(h)f,$(d)f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(minX).0 $(minY).0 $(minZ).0 positioned ~0.15 ~0.15 ~0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:white_stained_glass_pane",Properties:{east:"true",west:"true"}},brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.04f,0f],scale:[$(w)f,$(d)f,0.08f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(minX).0 $(topY).0 $(minZ).0 positioned ~0.15 ~-0.15 ~0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:white_stained_glass_pane",Properties:{east:"true",west:"true"}},brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.04f,0f],scale:[$(w)f,$(d)f,0.08f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
