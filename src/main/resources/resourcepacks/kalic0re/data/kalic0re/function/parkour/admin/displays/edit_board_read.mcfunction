# As the title edit_board opens: its course, own title (none when it follows the course's), place and facing into
# kalic0re:dialog args; its size as #leaderboardDisplaySize, the slider's quarters from 0.25 to 4 it must open within.
data modify storage kalic0re:dialog args.x set from entity @s Pos[0]
data modify storage kalic0re:dialog args.y set from entity @s Pos[1]
data modify storage kalic0re:dialog args.z set from entity @s Pos[2]
# Position's and Rotation's text, which positioned and rotated read back as where the board is, so an unchanged
# Apply leaves it be.
data modify storage kalic0re:parkour tmp.leaderboardDisplayPlace set value {}
function kalic0re:parkour/admin/displays/edit_board_number {from:"Pos[0]",into:"x"}
function kalic0re:parkour/admin/displays/edit_board_number {from:"Pos[1]",into:"y"}
function kalic0re:parkour/admin/displays/edit_board_number {from:"Pos[2]",into:"z"}
function kalic0re:parkour/admin/displays/edit_board_number {from:"Rotation[0]",into:"yaw"}
function kalic0re:parkour/admin/displays/edit_board_number {from:"Rotation[1]",into:"pitch"}
function kalic0re:parkour/admin/displays/edit_board_place_text with storage kalic0re:parkour tmp.leaderboardDisplayPlace
data remove storage kalic0re:parkour tmp.leaderboardDisplayPlace
execute if entity @s[tag=kalic0re.parkour.leaderboardDisplayCustomTitle] run \
    data modify storage kalic0re:dialog args.current_title set from entity @s text.text
execute store result storage kalic0re:parkour tmp.leaderboardDisplayEdit.id int 1 run \
    scoreboard players get @s kalic0re.parkour.leaderboardDisplayCourseId
function kalic0re:parkour/admin/displays/edit_board_course with storage kalic0re:parkour tmp.leaderboardDisplayEdit
data remove storage kalic0re:parkour tmp.leaderboardDisplayEdit
execute store result score #leaderboardDisplaySize kalic0re.parkour.tmp run data get entity @s transformation.scale[1] 1000
scoreboard players add #leaderboardDisplaySize kalic0re.parkour.tmp 125
scoreboard players operation #leaderboardDisplaySize kalic0re.parkour.tmp /= #250 kalic0re.parkour.tmp
scoreboard players operation #leaderboardDisplaySize kalic0re.parkour.tmp *= #250 kalic0re.parkour.tmp
execute if score #leaderboardDisplaySize kalic0re.parkour.tmp matches ..249 run \
    scoreboard players set #leaderboardDisplaySize kalic0re.parkour.tmp 250
execute if score #leaderboardDisplaySize kalic0re.parkour.tmp matches 4001.. run \
    scoreboard players set #leaderboardDisplaySize kalic0re.parkour.tmp 4000
