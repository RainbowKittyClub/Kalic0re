# A lone pane is already a post along y.
summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:yellow_stained_glass_pane"}, \
    Glowing:1b,glow_color_override:16777045,brightness:{sky:15,block:15}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
scoreboard players remove #edgeLeft kalic0re.parkour.tmp 1
execute if score #edgeLeft kalic0re.parkour.tmp matches 1.. positioned ~ ~1 ~ run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/edge_y
