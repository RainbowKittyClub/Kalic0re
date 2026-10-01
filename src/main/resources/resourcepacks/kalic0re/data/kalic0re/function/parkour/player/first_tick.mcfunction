# A runner's first tick: a turn of their own within every 16 ticks, handed out in order, and their
# food exhaustion to hold them at. Exit resets both, so each run starts here.
scoreboard players add #lastRunnerTickPhase kalic0re.parkour.runnerTickPhaseOf16 1
scoreboard players operation #lastRunnerTickPhase kalic0re.parkour.runnerTickPhaseOf16 %= #16 kalic0re.parkour.tmp
scoreboard players operation @s kalic0re.parkour.runnerTickPhaseOf16 = #lastRunnerTickPhase kalic0re.parkour.runnerTickPhaseOf16
scoreboard players operation @s kalic0re.parkour.runnerTickPhaseOf4 = @s kalic0re.parkour.runnerTickPhaseOf16
scoreboard players operation @s kalic0re.parkour.runnerTickPhaseOf4 %= #4 kalic0re.parkour.tmp
scoreboard players operation @s kalic0re.parkour.runnerTickPhaseOf2 = @s kalic0re.parkour.runnerTickPhaseOf16
scoreboard players operation @s kalic0re.parkour.runnerTickPhaseOf2 %= #2 kalic0re.parkour.tmp
# At most 3.99: the cancel can leave them 0.01 over it, and food is spent only past 4.00.
execute store result score @s kalic0re.parkour.foodExhaustionAtRunStart run data get entity @s foodExhaustionLevel 100
execute if score @s kalic0re.parkour.foodExhaustionAtRunStart matches 400.. run \
    scoreboard players set @s kalic0re.parkour.foodExhaustionAtRunStart 399
# Refill runs on the runner's turn after this, so their items and carrier come now.
function kalic0re:parkour/player/refill
