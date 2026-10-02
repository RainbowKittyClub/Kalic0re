# One boundary edge along y from here, in $(pane) glowing $(glow): a glowing $(gy) at each end and a
# gap between, so many boundaries don't drown in outline.
$summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:$(pane)"},Glowing:1b,glow_color_override:$(glow), \
    brightness:{sky:15,block:15},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,$(gy)f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
$execute positioned ~ ~$(ey) ~ run summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:$(pane)"},Glowing:1b, \
    glow_color_override:$(glow),brightness:{sky:15,block:15}, \
    transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,$(gy)f,1f]}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineNew"]}
