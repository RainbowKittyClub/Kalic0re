execute as @a[scores={kalic0re.parkour.isActive=1}] at @s run function kalic0re:parkour/player/tick
# isActive alone ends a run, so the quit item, kalic0re:parkour/finish or an op can set it to 0.
# Either saved step starts the exit, which restores and untags both, so the second line skips them.
execute as @a[tag=kalic0re.parkour.playerInventorySaved] \
    unless score @s kalic0re.parkour.isActive matches 1 \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
execute as @a[tag=kalic0re.parkour.playerEffectsSaved] \
    unless score @s kalic0re.parkour.isActive matches 1 \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
# The leaderboard trigger rests at -1, so any other value means it was used.
execute as @a[scores={kalic0re.parkour.leaderboard=0..}] run function kalic0re:parkour/leaderboard/requested
execute as @a[scores={kalic0re.parkour.leaderboard=..-2}] run function kalic0re:parkour/leaderboard/requested
execute as @a[scores={kalic0re.parkour.admin=1..}] run function kalic0re:parkour/admin/requested
# The course boundary wand's right-clicks; its left-clicks come through its enchantment instead.
execute as @a[scores={kalic0re.parkour.courseBoundaryWandRightClick=1..}] at @s run \
    function kalic0re:parkour/admin/course_boundary/wand/right_click
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
execute as @a[tag=!kalic0re.parkour.holdingBoundaryWand] \
    if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    at @s run function kalic0re:parkour/admin/course_boundary/wand/outline/wand_in_hand
execute as @a[tag=kalic0re.parkour.holdingBoundaryWand] \
    unless items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkourCourseBoundaryWand:{}}}] \
    run function kalic0re:parkour/admin/course_boundary/wand/outline/wand_out_of_hand
# Night vision while holding it, 6 s renewed every 16 ticks, so it ends soon after. Under 10 s left the
# client flickers it, which at 6 s is always.
execute if score #16ticks kalic0re.timer matches 0 as @a[tag=kalic0re.parkour.holdingBoundaryWand] run \
    effect give @s minecraft:night_vision 6 0 true
