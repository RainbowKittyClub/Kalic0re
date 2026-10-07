# As a new part, summoned at the root: its block, its place from the root, and a ride on the root. The
# root went into this spot before any part, so the search finds it without passing them.
data modify entity @s block_state set from storage kalic0re:block_state state
data modify entity @s transformation.translation set from storage kalic0re:effect displayCreate.translation
tag @s add kalic0re.effect.displayPart
ride @s mount @e[type=minecraft:block_display,tag=kalic0re.effect.displayNewRoot,distance=..0.01,limit=1]
