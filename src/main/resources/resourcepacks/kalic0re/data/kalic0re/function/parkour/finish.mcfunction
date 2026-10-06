# Run as a player at a finish line, e.g. from its command block: function kalic0re:parkour/finish
# Stops their timer and records the time; they stay on the course to restart or quit.
execute unless score @s kalic0re.parkour.isActive matches 1 run return fail
execute unless score @s kalic0re.parkour.isTimerRunning matches 1 run return fail
scoreboard players set @s kalic0re.parkour.isTimerRunning 0
# At the runner, not the command block: reading their name summons a display where they stand.
execute at @s run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/timer/record"}
