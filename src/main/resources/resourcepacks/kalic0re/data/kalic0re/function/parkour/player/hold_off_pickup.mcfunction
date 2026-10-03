# As an item near a runner: a pickup delay of at least 2. Tick functions run before items count theirs down and
# before players pick up, so it's still 1 then. Never lowered: one never to be picked up (32767) stays so.
execute store result score #itemPickupDelay kalic0re.parkour.tmp run data get entity @s PickupDelay
execute if score #itemPickupDelay kalic0re.parkour.tmp matches ..1 run data modify entity @s PickupDelay set value 2s
