$function kalic0re:parkour/inv/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.playerInventorySaved
$function kalic0re:parkour/effects/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.playerEffectsSaved
$function kalic0re:parkour/player/goto {uuid:$(uuid),point:"lobby"}
scoreboard players reset @s kalic0re.parkour.runTicks
scoreboard players reset @s kalic0re.parkour.courseBestTicks
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}]
