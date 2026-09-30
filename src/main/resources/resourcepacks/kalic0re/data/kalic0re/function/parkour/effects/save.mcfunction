# Never overwrite saved effects: saving again before a restore would store the run's effects over them.
$execute if data storage kalic0re:parkour players[{uuid:$(uuid)}].effects run return 0
# Storage does not tick, so durations pause for the run. Each entry keeps its hidden_effect chain.
$data modify storage kalic0re:parkour players[{uuid:$(uuid)}].effects set value []
$data modify storage kalic0re:parkour players[{uuid:$(uuid)}].effects set from entity @s active_effects
effect clear @s
