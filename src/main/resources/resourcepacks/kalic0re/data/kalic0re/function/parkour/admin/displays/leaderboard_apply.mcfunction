# As one line of a board: its text, the built line its number points to.
execute store result storage kalic0re:parkour tmp.leaderboardDisplay.line int 1 run \
    scoreboard players get @s kalic0re.parkour.leaderboardDisplayLine
function kalic0re:parkour/admin/displays/leaderboard_apply_line with storage kalic0re:parkour tmp.leaderboardDisplay
