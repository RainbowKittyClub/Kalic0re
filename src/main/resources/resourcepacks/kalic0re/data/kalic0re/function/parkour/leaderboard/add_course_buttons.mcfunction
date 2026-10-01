# Adds a leaderboard_button.json to the Leaderboards dialog for each name left in tmp.names.
execute unless data storage kalic0re:parkour tmp.names[0] run return 0
data modify storage kalic0re:dialog args set value {}
data modify storage kalic0re:dialog args.course set from storage kalic0re:parkour tmp.names[0]
function kalic0re:parkour/leaderboard/add_course_button with storage kalic0re:dialog args
data remove storage kalic0re:parkour tmp.names[0]
function kalic0re:parkour/leaderboard/add_course_buttons
