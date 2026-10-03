# Enter until both steps are saved; each save skips itself once its copy exists. Selected here, so a
# runner already in is never tested for it: one scan of the players costs less than a test per runner.
execute as @a[scores={kalic0re.parkour.isActive=1},tag=!kalic0re.parkour.playerInventorySaved] at @s run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/enter"}
execute as @a[scores={kalic0re.parkour.isActive=1},tag=kalic0re.parkour.playerInventorySaved, \
    tag=!kalic0re.parkour.playerEffectsSaved] at @s run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/enter"}
# enter refuses a run with no course and clears isActive, which leaves them out of this.
execute as @a[scores={kalic0re.parkour.isActive=1}] at @s run function kalic0re:parkour/player/tick
# The course id their checks pass on, removed once rather than per runner.
data remove storage kalic0re:parkour tmp.courseBoundaryCheck
# isActive alone ends a run, so the quit item, kalic0re:parkour/finish or an op can set it to 0.
# Either saved step starts the exit, which restores and untags both, so the second line skips them.
# The score is in the selector, so runners are passed over without a test each.
execute as @a[tag=kalic0re.parkour.playerInventorySaved,scores={kalic0re.parkour.isActive=..0}] \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
execute as @a[tag=kalic0re.parkour.playerEffectsSaved,scores={kalic0re.parkour.isActive=..0}] \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
# Any other value, or none, which no score range selects: caught on the 16-tick beat.
execute if score #16ticks kalic0re.timer matches 0 as @a[tag=kalic0re.parkour.playerInventorySaved] \
    unless score @s kalic0re.parkour.isActive matches 1 \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
execute if score #16ticks kalic0re.timer matches 0 as @a[tag=kalic0re.parkour.playerEffectsSaved] \
    unless score @s kalic0re.parkour.isActive matches 1 \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
# The leaderboard trigger rests at -1, so any other value means it was used.
execute as @a[scores={kalic0re.parkour.leaderboard=0..}] run function kalic0re:parkour/leaderboard/requested
execute as @a[scores={kalic0re.parkour.leaderboard=..-2}] run function kalic0re:parkour/leaderboard/requested
execute as @a[scores={kalic0re.parkour.admin=1..}] run function kalic0re:parkour/admin/requested
# At their feet: the boundaries removed are the ones holding them.
execute as @a[scores={kalic0re.admin.remove_course_boundary=1..}] at @s run \
    function kalic0re:parkour/admin/course_boundary/remove_requested
# The course boundary wand's right-clicks; its left-clicks come through its enchantment instead.
execute as @a[scores={kalic0re.parkour.courseBoundaryWandRightClick=1..}] at @s run \
    function kalic0re:parkour/admin/course_boundary/wand/right_click
# Standing-blocks chests and their lists, kept in step while any is open.
execute if data storage kalic0re:parkour standingBlockChests[0] run function kalic0re:parkour/admin/standing_block/chest/tick_all
# Displays outlines left behind go a few boundaries' worth a tick, as outlines are drawn.
execute if score #boundaryOutlineDisplaysToClear kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/clear_queued_displays
# Outlines still being drawn get their next few boundaries, before any outline starts this tick.
execute as @a[scores={kalic0re.parkour.boundaryOutlineNextBoundary=0..}] at @s run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/draw_queued_boundaries
# The outline follows the wand into or out of the main hand once it stays 10 ticks, so scrolling past
# it on the hotbar draws and clears nothing. A count stops once the hand matches the outline again.
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand,scores={kalic0re.parkour.boundaryWandHandChangeTicks=1..}] \
    if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    run scoreboard players reset @s kalic0re.parkour.boundaryWandHandChangeTicks
execute as @a[tag=!kalic0re.parkour.holdingBoundaryWand,scores={kalic0re.parkour.boundaryWandHandChangeTicks=1..}] \
    unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    run scoreboard players reset @s kalic0re.parkour.boundaryWandHandChangeTicks
# Every player's hand tested at once first, in one context: forking one per player costs four times as much,
# so that runs only while someone holds one.
execute if items entity @a[tag=!kalic0re.parkour.holdingBoundaryWand] weapon.mainhand \
    *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    as @a[tag=!kalic0re.parkour.holdingBoundaryWand] \
    if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    at @s run function kalic0re:parkour/admin/course_boundary/wand/outline/wand_in_hand
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand] \
    unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    run function kalic0re:parkour/admin/course_boundary/wand/outline/wand_out_of_hand
# Night vision while holding it, 6 s renewed every 16 ticks, so it ends soon after. Under 10 s left the
# client flickers it, which at 6 s is always.
execute if score #16ticks kalic0re.timer matches 0 as @a[tag=kalic0re.parkour.holdingBoundaryWand] run \
    effect give @s minecraft:night_vision 6 0 true
