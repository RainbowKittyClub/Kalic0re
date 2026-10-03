# tmp.leaderboardDisplayPlacement.yaw to the nearest of north, east, south and west. A player's yaw is kept
# in -180 to 180, so +405 keeps it positive for the division: (floor(yaw) + 405) / 90 - 4, times 90.
execute store result score #leaderboardDisplayYaw kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayPlacement.yaw
scoreboard players add #leaderboardDisplayYaw kalic0re.parkour.tmp 405
scoreboard players operation #leaderboardDisplayYaw kalic0re.parkour.tmp /= #90 kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.leaderboardDisplayPlacement.yaw int 90 run \
    scoreboard players remove #leaderboardDisplayYaw kalic0re.parkour.tmp 4
