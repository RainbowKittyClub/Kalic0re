# Run as a player at a finish line, e.g. from its command block: function kalic0re:parkour/finish
# Ends the run as quitting does, but its time is recorded.
execute unless score @s kalic0re.parkour.isActive matches 1 run return fail
function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/mark_finished"}
scoreboard players set @s kalic0re.parkour.isActive 0
