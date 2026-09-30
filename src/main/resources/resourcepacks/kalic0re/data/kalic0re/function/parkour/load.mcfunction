scoreboard objectives add kalic0re.parkour.isActive dummy
scoreboard objectives add kalic0re.parkour.click minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add kalic0re.parkour.tmp dummy
# Ticks in the current run; and the runner's best on its course, -1 for none.
scoreboard objectives add kalic0re.parkour.runTicks dummy
scoreboard objectives add kalic0re.parkour.courseBestTicks dummy
# Divisors for splitting run ticks into minutes, seconds and tenths.
scoreboard players set #2 kalic0re.parkour.tmp 2
scoreboard players set #10 kalic0re.parkour.tmp 10
scoreboard players set #60 kalic0re.parkour.tmp 60
