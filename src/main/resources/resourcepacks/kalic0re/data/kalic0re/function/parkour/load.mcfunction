scoreboard objectives add kalic0re.parkour.isActive dummy
scoreboard objectives add kalic0re.parkour.click minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add kalic0re.parkour.tmp dummy
# Ticks in the current run; the runner's best on its course, -1 for none; and a trigger any player
# may set to a course's id to see its leaderboard, resting at -1.
scoreboard objectives add kalic0re.parkour.runTicks dummy
scoreboard objectives add kalic0re.parkour.courseBestTicks dummy
# The id of the course a runner is on, for the per-tick inclusion zone check.
scoreboard objectives add kalic0re.parkour.runningCourseId dummy
scoreboard objectives add kalic0re.parkour.leaderboard trigger
# /trigger kalic0re.parkour.admin opens the Courses dialog, for players an op has enabled it for:
# /scoreboard players enable <player> kalic0re.parkour.admin
scoreboard objectives add kalic0re.parkour.admin trigger
# Divisors for splitting run ticks into minutes, seconds and tenths.
scoreboard players set #2 kalic0re.parkour.tmp 2
scoreboard players set #10 kalic0re.parkour.tmp 10
scoreboard players set #60 kalic0re.parkour.tmp 60
# How messages name a point, shown through an nbt component: a key built from $(point) could only
# fall back to the point's own id.
data modify storage kalic0re:parkour pointNames set value { \
    start:{translate:"kalic0re.parkour.point.start",fallback:"restart point"}, \
    lobby:{translate:"kalic0re.parkour.point.lobby",fallback:"lobby"}}
