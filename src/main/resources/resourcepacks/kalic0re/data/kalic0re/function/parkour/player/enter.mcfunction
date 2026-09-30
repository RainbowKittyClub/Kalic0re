$execute unless data storage kalic0re:parkour players[{uuid:$(uuid)}].course run return run \
    function kalic0re:parkour/player/refuse
# Each tag marks a step whose copy is in storage and must be restored when the run ends.
$function kalic0re:parkour/inv/save {uuid:$(uuid)}
$execute if data storage kalic0re:parkour players[{uuid:$(uuid)}].inventory run \
    tag @s add kalic0re.parkour.playerInventorySaved
$function kalic0re:parkour/effects/save {uuid:$(uuid)}
$execute if data storage kalic0re:parkour players[{uuid:$(uuid)}].effects run \
    tag @s add kalic0re.parkour.playerEffectsSaved
# Clicks made while inactive would otherwise fire an item on the first tick of the run.
scoreboard players reset @s kalic0re.parkour.click
