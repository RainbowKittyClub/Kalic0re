# Run by the exhaustion-cancel enchantment each tick, as its wearer, just before its cancel effects:
# exhaustion gained since the run began, in hundredths, as the bits those effects each cancel.
execute store result score @s kalic0re.parkour.foodExhaustionSinceRunStart run \
    data get entity @s foodExhaustionLevel 100
scoreboard players operation @s kalic0re.parkour.foodExhaustionSinceRunStart -= @s kalic0re.parkour.foodExhaustionAtRunStart
# Nothing gained, nothing to split: the cancels also need something gained, so old bits stay idle.
execute if score @s kalic0re.parkour.foodExhaustionSinceRunStart matches ..0 run return 0
scoreboard players operation #foodExhaustionToSplit kalic0re.parkour.tmp = @s kalic0re.parkour.foodExhaustionSinceRunStart
# A tick can't gain 20.47 in vanilla; past it, the rest waits a tick.
execute if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 2048.. run \
    scoreboard players set #foodExhaustionToSplit kalic0re.parkour.tmp 2047
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit1024 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 1024..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit1024 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 1024
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit512 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 512..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit512 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 512
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit256 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 256..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit256 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 256
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit128 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 128..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit128 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 128
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit64 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 64..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit64 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 64
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit32 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 32..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit32 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 32
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit16 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 16..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit16 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 16
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit8 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 8..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit8 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 8
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit4 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 4..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit4 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 4
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit2 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 2..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit2 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 2
execute store success score @s kalic0re.parkour.foodExhaustionCancelBit1 if score #foodExhaustionToSplit kalic0re.parkour.tmp matches 1..
execute if score @s kalic0re.parkour.foodExhaustionCancelBit1 matches 1 run scoreboard players remove #foodExhaustionToSplit kalic0re.parkour.tmp 1
