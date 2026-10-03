# Every loaded board, by its title line: changes made while a board's chunk was unloaded reach it here,
# at load and every 1024 ticks, as well as at the change itself.
execute as @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay,scores={kalic0re.parkour.leaderboardDisplayLine=0}] run \
    function kalic0re:parkour/admin/displays/leaderboard_refresh_board
