# Splits the runner's best on this course into #best… digits, so their current time can be split
# into the plain ones beside it.
scoreboard players operation #ticks kalic0re.parkour.tmp = @s kalic0re.parkour.courseBestTicks
function kalic0re:parkour/timer/split
scoreboard players operation #bestTensOfMinutes kalic0re.parkour.tmp = #tensOfMinutes kalic0re.parkour.tmp
scoreboard players operation #bestOnesOfMinutes kalic0re.parkour.tmp = #onesOfMinutes kalic0re.parkour.tmp
scoreboard players operation #bestTensOfSeconds kalic0re.parkour.tmp = #tensOfSeconds kalic0re.parkour.tmp
scoreboard players operation #bestOnesOfSeconds kalic0re.parkour.tmp = #onesOfSeconds kalic0re.parkour.tmp
scoreboard players operation #bestTenthsOfSecond kalic0re.parkour.tmp = #tenthsOfSecond kalic0re.parkour.tmp
