# As a board's title line: its course's boards refreshed. One already removed has no score: course 0, none.
execute store result storage kalic0re:parkour tmp.leaderboardDisplayBoard.id int 1 run \
    scoreboard players get @s kalic0re.parkour.leaderboardDisplayCourseId
function kalic0re:parkour/admin/displays/leaderboard_refresh with storage kalic0re:parkour tmp.leaderboardDisplayBoard
data remove storage kalic0re:parkour tmp.leaderboardDisplayBoard
