# A quarter turn about x lays the pane's post along z, and translation [0,1,0] moves it back in.
summon minecraft:block_display ~ ~ ~ {block_state:{Name:"minecraft:yellow_stained_glass_pane"}, \
    transformation:{left_rotation:[0.70710677f,0f,0f,0.70710677f],right_rotation:[0f,0f,0f,1f], \
        translation:[0f,1f,0f],scale:[1f,1f,1f]}, \
    Glowing:1b,glow_color_override:16777045,brightness:{sky:15,block:15}, \
    Tags:["kalic0re.parkour.boundaryOutline","kalic0re.parkour.boundaryOutlineSelection","kalic0re.parkour.boundaryOutlineNew"]}
scoreboard players remove #edgeLeft kalic0re.parkour.tmp 1
execute if score #edgeLeft kalic0re.parkour.tmp matches 1.. positioned ~ ~ ~1 run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/edge_z
