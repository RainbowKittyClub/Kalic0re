# Back to the checkpoint, then put out there: a player's Fire can't be written, so a splash of water.
# Dropped from just over their head, its first move enters them; a projectile only hits what its path
# enters, so one spawned inside would fall to the floor and douse any candle or campfire there.
function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
execute at @s run summon minecraft:splash_potion ~ ~1.9 ~ \
    {Item:{id:"minecraft:splash_potion",components:{"minecraft:potion_contents":{potion:"minecraft:water"}}},Motion:[0.0,-0.5,0.0]}
