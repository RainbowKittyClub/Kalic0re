# As the wand's holder aiming at a leaderboard display, its title tagged by aim_at_leaderboard_display: its Edit
# dialog, which Cancel closes.
function kalic0re:parkour/admin/displays/edit_board
data modify storage kalic0re:dialog built.leaderboard_display_edit.exit_action set value {label:{translate:"gui.cancel"}}
function kalic0re:dialog/show {name:"leaderboard_display_edit"}
