# /function kalic0re:parkour/admin/displays/edit_nearest, or the Courses dialog's Edit Nearby Leaderboard Display:
# the Edit dialog for the nearest leaderboard display whose title is within 8 blocks, Back to the Courses dialog.
execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_leaderboard_display_near",color:"red"}
function kalic0re:parkour/admin/dialog/build_courses
data modify storage kalic0re:dialog args set value {course:"",current_title:""}
execute as @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run \
    function kalic0re:parkour/admin/displays/edit_nearest_read
function kalic0re:parkour/admin/dialog/leaderboard_display_edit
# Size starts where the board is: its steps count from there.
execute store result storage kalic0re:dialog built.leaderboard_display_edit.inputs[{key:"size"}].initial float 0.001 run \
    scoreboard players get #leaderboardDisplaySize kalic0re.parkour.tmp
data modify storage kalic0re:dialog built.leaderboard_display_edit.exit_action.action.dialog set from storage kalic0re:dialog built.courses
function kalic0re:dialog/show {name:"leaderboard_display_edit"}
