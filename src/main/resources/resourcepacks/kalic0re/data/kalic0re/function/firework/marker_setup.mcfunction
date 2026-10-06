# As a firework's new marker, at its burst point: its settings as data, the game time of each stage (the burst
# riseTicks on, then the shell, any twinkle, the end) and the rocket's way here in tmp.rise.
data modify entity @s data set from storage kalic0re:firework tmp.firework
tag @s add kalic0re.firework
execute store result score @s kalic0re.firework.burstGameTime run time query gametime
execute store result score #stageTicks kalic0re.firework.tmp run data get storage kalic0re:firework tmp.firework.riseTicks
scoreboard players operation @s kalic0re.firework.burstGameTime += #stageTicks kalic0re.firework.tmp
execute store result score #stageTicks kalic0re.firework.tmp run data get storage kalic0re:firework tmp.firework.burstTicks
scoreboard players operation @s kalic0re.firework.shellGameTime = @s kalic0re.firework.burstGameTime
scoreboard players operation @s kalic0re.firework.shellGameTime += #stageTicks kalic0re.firework.tmp
scoreboard players set #stageTicks kalic0re.firework.tmp 0
execute if data storage kalic0re:firework tmp.firework{hasTwinkle:1b} store result score #stageTicks kalic0re.firework.tmp run \
    data get storage kalic0re:firework tmp.firework.twinkleDelayTicks
scoreboard players operation @s kalic0re.firework.endGameTime = @s kalic0re.firework.shellGameTime
scoreboard players operation @s kalic0re.firework.endGameTime += #stageTicks kalic0re.firework.tmp
execute if data storage kalic0re:firework tmp.firework{hasTwinkle:1b} run \
    scoreboard players operation @s kalic0re.firework.twinkleGameTime = @s kalic0re.firework.endGameTime
data modify storage kalic0re:firework tmp.rise set value {}
data modify storage kalic0re:firework tmp.rise.x set from entity @s Pos[0]
data modify storage kalic0re:firework tmp.rise.y set from entity @s Pos[1]
data modify storage kalic0re:firework tmp.rise.z set from entity @s Pos[2]
data modify storage kalic0re:firework tmp.rise.color set from storage kalic0re:firework tmp.firework.riseColor
data modify storage kalic0re:firework tmp.rise.duration set from storage kalic0re:firework tmp.firework.riseTicks
data modify storage kalic0re:firework tmp.rise.particleMode set from storage kalic0re:firework tmp.firework.particleMode
