# Twelve posts 0.15 outside the block at $(x) $(y) $(z), each stretched to 1.3, so the frame clears the
# yellow edges running through the same corner. Offsets as in edges: half a block back across the axis.
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.65 ~-0.15 ~-0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~0.65 ~-0.15 ~-0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.65 ~-0.15 ~0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~0.65 ~-0.15 ~0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}

$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.15 ~-0.65 ~-0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0.70710677f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[1.3f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.15 ~0.65 ~-0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0.70710677f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[1.3f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.15 ~-0.65 ~0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0.70710677f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[1.3f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.15 ~0.65 ~0.65 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0.70710677f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[1.3f,0f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}

$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.65 ~-0.65 ~-0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,1f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~0.65 ~-0.65 ~-0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,1f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~-0.65 ~0.65 ~-0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,1f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned $(x).0 $(y).0 $(z).0 positioned ~0.65 ~0.65 ~-0.15 run summon minecraft:block_display ~ ~ ~ \
    {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f],translation:[0f,1f,0f],scale:[1f,1.3f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
