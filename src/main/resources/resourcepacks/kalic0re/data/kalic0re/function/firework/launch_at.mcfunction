# For launch: its marker set down at $(position), then from here the launch sound and the rocket rising to it.
$execute positioned $(position) summon minecraft:marker run function kalic0re:firework/marker_setup
$execute if data storage kalic0re:firework tmp.firework{playLaunchSound:1b} run \
    playsound $(launchSound) $(soundSource) @a ~ ~ ~ $(soundVolume)
function kalic0re:firework/rise with storage kalic0re:firework tmp.rise
data remove storage kalic0re:firework tmp.rise
