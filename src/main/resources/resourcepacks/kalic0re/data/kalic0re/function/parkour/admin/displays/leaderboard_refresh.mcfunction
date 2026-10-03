# Course $(id)'s boards, every line set again from its times; boards of a course that's gone go with it.
# Text displays don't resolve score or nbt text, so each line is built as plain text first.
$execute unless data storage kalic0re:parkour courses[{id:$(id)}] run return run \
    function kalic0re:parkour/admin/displays/leaderboard_remove {id:$(id)}
$execute unless entity @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayCourseId=$(id)},limit=1] run return 0
data modify storage kalic0re:parkour tmp.leaderboardDisplay set value {lines:[],times:[]}
$data modify storage kalic0re:parkour tmp.leaderboardDisplay.times set from storage kalic0re:parkour courses[{id:$(id)}].times
$data modify storage kalic0re:parkour tmp.leaderboardDisplay.course set from storage kalic0re:parkour courses[{id:$(id)}].name
function kalic0re:parkour/admin/displays/leaderboard_title with storage kalic0re:parkour tmp.leaderboardDisplay
scoreboard players set #leaderboardDisplayRank kalic0re.parkour.tmp 1
function kalic0re:parkour/admin/displays/leaderboard_rank_lines
# A title given its own text in the Edit dialog keeps it.
$execute as @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay,tag=!kalic0re.parkour.leaderboardDisplayCustomTitle, \
    scores={kalic0re.parkour.leaderboardDisplayCourseId=$(id)}] run function kalic0re:parkour/admin/displays/leaderboard_apply
data remove storage kalic0re:parkour tmp.leaderboardDisplay
