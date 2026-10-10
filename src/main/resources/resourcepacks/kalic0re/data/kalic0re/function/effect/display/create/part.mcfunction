# As the root, at it: a part showing $(block_state), sized $(scale) and $(translation) from the root,
# riding it. Summoned with its data, not given it after: Bellows' NBT optimisation drops a data modify
# of block_state.
$summon minecraft:block_display ~ ~ ~ {Tags:["kalic0re.effect.displayPart","kalic0re.effect.displayNewPart"],block_state:$(block_state),transformation:{translation:$(translation),left_rotation:[0f,0f,0f,1f],scale:$(scale),right_rotation:[0f,0f,0f,1f]}}
ride @e[type=minecraft:block_display,tag=kalic0re.effect.displayNewPart,distance=..0.01,limit=1] mount @s
tag @e[type=minecraft:block_display,tag=kalic0re.effect.displayNewPart,distance=..0.01,limit=1] remove kalic0re.effect.displayNewPart
