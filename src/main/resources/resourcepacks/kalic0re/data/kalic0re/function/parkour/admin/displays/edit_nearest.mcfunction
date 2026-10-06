# /function kalic0re:parkour/admin/displays/edit_nearest: the Edit dialog for the nearest leaderboard display whose
# title is within 8 blocks, Back to the Courses dialog. The wand's right-click at a display opens it too.
execute unless entity @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_leaderboard_display_near",color:"red"}
function kalic0re:parkour/admin/dialog/build_courses
tag @n[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..8] add kalic0re.parkour.leaderboardDisplayToEdit
function kalic0re:parkour/admin/displays/edit_board
data modify storage kalic0re:dialog built.leaderboard_display_edit.exit_action.action.dialog set from storage kalic0re:dialog built.courses
function kalic0re:dialog/show {name:"leaderboard_display_edit"}
