# For read_values: values.$(key) as a whole number of ticks, at least 1.
$execute unless data storage kalic0re:firework input.values.$(key) run return 0
$execute store result score #ticks kalic0re.firework.tmp run data get storage kalic0re:firework input.values.$(key)
execute if score #ticks kalic0re.firework.tmp matches ..0 run scoreboard players set #ticks kalic0re.firework.tmp 1
$execute store result storage kalic0re:firework edited.$(key) int 1 run scoreboard players get #ticks kalic0re.firework.tmp
