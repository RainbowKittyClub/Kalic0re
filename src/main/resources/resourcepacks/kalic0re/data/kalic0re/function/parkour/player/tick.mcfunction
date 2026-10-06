# As a runner, entered: kalic0re:parkour/tick enters them first.
execute unless score @s kalic0re.parkour.runnerTickPhaseOf16 matches 0.. run function kalic0re:parkour/player/first_tick
# A run begun before the timer had its own flag (an older pack) keeps timing.
execute unless score @s kalic0re.parkour.isTimerRunning matches -2147483648.. run scoreboard players set @s kalic0re.parkour.isTimerRunning 1
execute if score @s kalic0re.parkour.isTimerRunning matches 1 run scoreboard players add @s kalic0re.parkour.runTicks 1
execute if score @s kalic0re.parkour.click matches 1.. run function kalic0re:parkour/player/click
# The rest on the runner's own turn, so runners' share of it lands on different ticks.
execute if score @s kalic0re.parkour.runnerTickPhaseOf4 = #4ticks kalic0re.timer run function kalic0re:parkour/player/refill
# Back to the checkpoint when out of bounds, tested every tick; burning, on the runner's own turn of 16.
function kalic0re:parkour/course_boundary/check_runner
# Parkour items only exist on active players, so their drops are always near one. Every tick: a
# runner who dies drops theirs and respawns away from them a tick or two later.
execute as @e[type=item,distance=..8] if items entity @s contents *[custom_data~{kalic0re:{parkour:{}}}] run kill @s
# Runners pick nothing else up: items within reach (2.7 plus a tick's flight) keep a pickup delay. At @s again,
# as the check above may have sent them back.
execute at @s as @e[type=item,distance=..5] run function kalic0re:parkour/player/hold_off_pickup
# A safety net for what still gets in, a pearl or a portal landing on an item, a /give: back onto the ground.
# Only once their own inventory is saved and cleared, so nothing of theirs is ever thrown down.
execute if entity @s[tag=kalic0re.parkour.playerInventorySaved] at @s run function kalic0re:parkour/player/sweep_strays
# The shown time moves in tenths of a second, every 2 ticks. After the clicks, so a restart's reset
# shows within a tick.
execute if score @s kalic0re.parkour.runnerTickPhaseOf2 = #2ticks kalic0re.timer run function kalic0re:parkour/timer/show
