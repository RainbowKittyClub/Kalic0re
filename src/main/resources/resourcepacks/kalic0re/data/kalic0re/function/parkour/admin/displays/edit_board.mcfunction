# As a player: the Edit dialog built for the board whose title is tagged leaderboardDisplayToEdit, the tag then
# taken off. Its Apply and Remove name that title's position, so they reach this board wherever the player is.
# The caller sets its exit and shows it.
data modify storage kalic0re:dialog args set value {course:"",current_title:""}
execute as @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplayToEdit,limit=1] run \
    function kalic0re:parkour/admin/displays/edit_board_read
tag @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplayToEdit] remove kalic0re.parkour.leaderboardDisplayToEdit
function kalic0re:parkour/admin/dialog/leaderboard_display_edit
# Size starts where the board is: its steps count from there.
execute store result storage kalic0re:dialog built.leaderboard_display_edit.inputs[{key:"size"}].initial float 0.001 run \
    scoreboard players get #leaderboardDisplaySize kalic0re.parkour.tmp
