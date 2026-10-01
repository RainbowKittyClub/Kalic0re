# The Leaderboards dialog: a button per course, which uses the leaderboard trigger with its number.
# A dialog needs a button, so with no courses there is a message instead.
execute unless data storage kalic0re:parkour courses[0] run return run tellraw @s \
    {translate:"kalic0re.parkour.chat.no_courses",fallback:"There are no parkour courses yet.",color:"red"}
function kalic0re:parkour/leaderboard/leaderboards
data modify storage kalic0re:parkour tmp.names set value []
data modify storage kalic0re:parkour tmp.names append from storage kalic0re:parkour courses[].name
function kalic0re:parkour/leaderboard/add_course_buttons
data remove storage kalic0re:parkour tmp.names
function kalic0re:dialog/show {name:"leaderboards"}
