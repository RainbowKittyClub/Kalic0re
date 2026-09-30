$execute unless data storage kalic0re:parkour players[{uuid:$(uuid)}].course run return run \
    function kalic0re:parkour/player/refuse
$function kalic0re:parkour/inv/save {uuid:$(uuid)}
$function kalic0re:parkour/effects/save {uuid:$(uuid)}
# Clicks made while inactive would otherwise fire an item on the first tick of the run.
scoreboard players reset @s kalic0re.parkour.click
tag @s add kalic0re.parkour.receivedParkourItems
