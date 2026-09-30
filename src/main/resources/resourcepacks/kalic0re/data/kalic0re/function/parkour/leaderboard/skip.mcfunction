# Drops the first #start entries of tmp.board.times.
execute if score #start kalic0re.parkour.tmp matches ..0 run return 0
data remove storage kalic0re:parkour tmp.board.times[0]
scoreboard players remove #start kalic0re.parkour.tmp 1
function kalic0re:parkour/leaderboard/skip
