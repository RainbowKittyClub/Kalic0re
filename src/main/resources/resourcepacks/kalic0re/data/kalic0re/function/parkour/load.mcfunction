scoreboard objectives add kalic0re.parkour.isActive dummy
scoreboard objectives add kalic0re.parkour.click minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add kalic0re.parkour.tmp dummy
# Ticks in the current run; the runner's best on its course, -1 for none; and a trigger any player
# may set to a course's id to see its leaderboard, resting at -1.
scoreboard objectives add kalic0re.parkour.runTicks dummy
scoreboard objectives add kalic0re.parkour.courseBestTicks dummy
# The id of the course a runner is on, for the per-tick course boundary check.
scoreboard objectives add kalic0re.parkour.runningCourseId dummy
# The boundary wand is a warped fungus on a stick, so its uses never count as parkour items'.
scoreboard objectives add kalic0re.parkour.courseBoundaryWandRightClick minecraft.used:minecraft.warped_fungus_on_a_stick
# Whose wand selection each outline display draws; the sweep clears ones left over from a restart.
scoreboard objectives add kalic0re.parkour.boundaryOutlineOwner dummy
# Ticks the wand has been in (or out of) the main hand without the outline following yet.
scoreboard objectives add kalic0re.parkour.boundaryWandHandChangeTicks dummy
# A holder's outline still being drawn: the course's id and the next of its boundaries to draw.
scoreboard objectives add kalic0re.parkour.boundaryOutlineCourseId dummy
scoreboard objectives add kalic0re.parkour.boundaryOutlineNextBoundary dummy
schedule function kalic0re:parkour/admin/course_boundary/wand/outline/sweep 20t replace
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
