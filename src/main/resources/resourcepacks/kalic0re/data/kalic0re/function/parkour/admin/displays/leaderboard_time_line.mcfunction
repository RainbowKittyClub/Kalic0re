# The leaderboard dialog's own line, built into kalic0re:dialog built.leaderboard_line, white for everyone.
data modify storage kalic0re:dialog args set value {color:"white"}
data modify storage kalic0re:dialog args.player set from storage kalic0re:parkour tmp.leaderboardDisplay.times[0].player
data modify storage kalic0re:dialog args.rank set from storage kalic0re:parkour tmp.leaderboardDisplay.rank
execute store result score #ticks kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.leaderboardDisplay.times[0].ticks
function kalic0re:parkour/timer/split_into_args
function kalic0re:parkour/leaderboard/leaderboard_line
data modify storage kalic0re:parkour tmp.leaderboardDisplay.lines append from storage kalic0re:dialog built.leaderboard_line
data remove storage kalic0re:dialog args
data remove storage kalic0re:dialog built.leaderboard_line
# Nothing else built: no empty compound left behind.
execute store result score #dialogBuiltEntries kalic0re.parkour.tmp run data get storage kalic0re:dialog built
execute if score #dialogBuiltEntries kalic0re.parkour.tmp matches 0 run data remove storage kalic0re:dialog built
