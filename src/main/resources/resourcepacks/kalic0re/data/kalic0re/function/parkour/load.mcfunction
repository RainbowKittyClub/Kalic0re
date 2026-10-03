scoreboard objectives add kalic0re.parkour.isActive dummy
scoreboard objectives add kalic0re.parkour.click minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add kalic0re.parkour.tmp dummy
# Ticks in the current run; the runner's best on its course, -1 for none; and a trigger any player
# may set to a course's id to see its leaderboard, resting at -1.
scoreboard objectives add kalic0re.parkour.runTicks dummy
scoreboard objectives add kalic0re.parkour.courseBestTicks dummy
# The id of the course a runner is on, for the per-tick course boundary check.
scoreboard objectives add kalic0re.parkour.runningCourseId dummy
# Each runner's turn within every 2, 4 and 16 ticks, so their periodic work falls on different ticks.
scoreboard objectives add kalic0re.parkour.runnerTickPhaseOf2 dummy
scoreboard objectives add kalic0re.parkour.runnerTickPhaseOf4 dummy
scoreboard objectives add kalic0re.parkour.runnerTickPhaseOf16 dummy
# A runner's food exhaustion when the run began, and gained since, in hundredths: their carrier's
# enchantment cancels what's gained, bit by bit, so a run leaves hunger where it was.
scoreboard objectives add kalic0re.parkour.foodExhaustionAtRunStart dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionSinceRunStart dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit1 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit2 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit4 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit8 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit16 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit32 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit64 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit128 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit256 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit512 dummy
scoreboard objectives add kalic0re.parkour.foodExhaustionCancelBit1024 dummy
# The boundary wand is a warped fungus on a stick, so its uses never count as parkour items'.
scoreboard objectives add kalic0re.parkour.courseBoundaryWandRightClick minecraft.used:minecraft.warped_fungus_on_a_stick
# Whose wand selection each outline display draws; the sweep clears ones left over from a restart.
scoreboard objectives add kalic0re.parkour.boundaryOutlineOwner dummy
# Ticks the wand has been in (or out of) the main hand without the outline following yet.
scoreboard objectives add kalic0re.parkour.boundaryWandHandChangeTicks dummy
# A holder's outline still being drawn: the course's id and the next of its boundaries to draw, or of
# its exclusion boundaries once tagged boundaryOutlineDrawingExclusionBoundaries.
scoreboard objectives add kalic0re.parkour.boundaryOutlineCourseId dummy
scoreboard objectives add kalic0re.parkour.boundaryOutlineNextBoundary dummy
schedule function kalic0re:parkour/admin/course_boundary/wand/outline/sweep 20t replace
# Whose standing-blocks chest a chest minecart is, and its course. Chests close at a reload: their lists are
# saved already, and one kept in an unloaded chunk would come back with old contents.
scoreboard objectives add kalic0re.parkour.standingBlockChestOwner dummy
scoreboard objectives add kalic0re.parkour.standingBlockChestCourseId dummy
execute as @e[type=minecraft:chest_minecart,tag=kalic0re.parkour.standingBlockChest] run function kalic0re:parkour/admin/standing_block/chest/close
kill @e[type=minecraft:block_display,tag=kalic0re.parkour.standingBlockChestSeat]
data remove storage kalic0re:parkour standingBlockChests
scoreboard objectives add kalic0re.parkour.leaderboard trigger
# /trigger kalic0re.parkour.admin opens the Courses dialog, for players an op has enabled it for:
# /scoreboard players enable <player> kalic0re.parkour.admin
scoreboard objectives add kalic0re.parkour.admin trigger
# /trigger kalic0re.admin.remove_course_boundary removes the boundary of a held wand's course around the
# player's feet. give_wand enables it for whoever takes a wand; an op can for anyone else:
# /scoreboard players enable <player> kalic0re.admin.remove_course_boundary
scoreboard objectives add kalic0re.admin.remove_course_boundary trigger
# Divisors for splitting run ticks into minutes, seconds and tenths, and for runners' tick phases.
scoreboard players set #2 kalic0re.parkour.tmp 2
scoreboard players set #4 kalic0re.parkour.tmp 4
scoreboard players set #16 kalic0re.parkour.tmp 16
scoreboard players set #10 kalic0re.parkour.tmp 10
scoreboard players set #60 kalic0re.parkour.tmp 60
# How messages name a point, shown through an nbt component: a key built from $(point) could only
# fall back to the point's own id.
data modify storage kalic0re:parkour pointNames set value { \
    start:{translate:"kalic0re.parkour.point.start"}, \
    lobby:{translate:"kalic0re.parkour.point.lobby"}}
# Standing blocks a runner can be inside, so their rule also tests the feet with inTerm, and which the
# standing-blocks chest takes as their bucket. Powder snow can be stood on as well, in leather boots.
data modify storage kalic0re:parkour enterableStandingBlocks set value [ \
    {block:"minecraft:water",bucket:"minecraft:water_bucket", \
        inTerm:{condition:"minecraft:location_check",predicate:{fluid:{fluids:"#minecraft:water"}}}}, \
    {block:"minecraft:lava",bucket:"minecraft:lava_bucket", \
        inTerm:{condition:"minecraft:location_check",predicate:{fluid:{fluids:"#minecraft:lava"}}}}, \
    {block:"minecraft:powder_snow",bucket:"minecraft:powder_snow_bucket",standable:1b, \
        inTerm:{condition:"minecraft:location_check",predicate:{block:{blocks:"minecraft:powder_snow"}}}}]
function kalic0re:parkour/course/refresh_all_predicate_texts
