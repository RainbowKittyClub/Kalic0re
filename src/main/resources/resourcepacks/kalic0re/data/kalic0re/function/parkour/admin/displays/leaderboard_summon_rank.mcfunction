# As a new board's title: rank #leaderboardDisplayLine riding it, then the next, up to 10.
execute summon minecraft:text_display run function kalic0re:parkour/admin/displays/leaderboard_rank_setup
scoreboard players add #leaderboardDisplayLine kalic0re.parkour.tmp 1
execute if score #leaderboardDisplayLine kalic0re.parkour.tmp matches ..10 run \
    function kalic0re:parkour/admin/displays/leaderboard_summon_rank
