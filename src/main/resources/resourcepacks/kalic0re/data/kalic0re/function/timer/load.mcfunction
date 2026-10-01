# Global tick timers, one per power of two: #<n>ticks counts 0 to n - 1 and starts over, so "matches 0"
# holds once every n ticks. Zeroed together here, they stay in step: each 0 of one is a 0 of all smaller.
scoreboard objectives add kalic0re.timer dummy
scoreboard players set #2ticks kalic0re.timer 0
scoreboard players set #4ticks kalic0re.timer 0
scoreboard players set #8ticks kalic0re.timer 0
scoreboard players set #16ticks kalic0re.timer 0
scoreboard players set #32ticks kalic0re.timer 0
scoreboard players set #64ticks kalic0re.timer 0
scoreboard players set #128ticks kalic0re.timer 0
scoreboard players set #256ticks kalic0re.timer 0
scoreboard players set #512ticks kalic0re.timer 0
scoreboard players set #1024ticks kalic0re.timer 0
