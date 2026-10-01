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
