# /function kalic0re:effect/sound/play {effect:"<function>",targets:"<players>",location:"<x y z>"}
# e.g. {effect:"kalic0re:effect/sound/secret_discovery_01",targets:"@s",location:"~ ~ ~"}
# Plays a sound effect function's notes from location, in this dimension, to the players targets picks out
# now: ~, ^, @s and distance mean what they would in a playsound of your own. The effect function sets
# kalic0re:effect sound.notes; notes after the first play on later ticks, from a marker left at location.
scoreboard players add #lastSoundPlayback kalic0re.effect.tmp 1
data modify storage kalic0re:effect soundPlayback set value {}
execute store result storage kalic0re:effect soundPlayback.id int 1 run \
    scoreboard players get #lastSoundPlayback kalic0re.effect.tmp
$data modify storage kalic0re:effect soundPlayback.effect set value "$(effect)"
$execute positioned $(location) as $(targets) run \
    function kalic0re:effect/sound/tag_listener with storage kalic0re:effect soundPlayback
$execute positioned $(location) summon minecraft:marker run \
    function kalic0re:effect/sound/start with storage kalic0re:effect soundPlayback
data remove storage kalic0re:effect soundPlayback
