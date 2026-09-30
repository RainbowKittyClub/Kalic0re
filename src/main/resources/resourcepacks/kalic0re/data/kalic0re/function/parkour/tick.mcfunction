execute as @a[scores={kalic0re.parkour.isActive=1}] at @s run function kalic0re:parkour/player/tick
# isActive alone ends a run, so the quit item, a finish line or an op can all set it to 0. Either
# saved step starts the exit, which restores and untags both, so the second line then skips them.
execute as @a[tag=kalic0re.parkour.playerInventorySaved] \
    unless score @s kalic0re.parkour.isActive matches 1 \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
execute as @a[tag=kalic0re.parkour.playerEffectsSaved] \
    unless score @s kalic0re.parkour.isActive matches 1 \
    at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/exit"}
