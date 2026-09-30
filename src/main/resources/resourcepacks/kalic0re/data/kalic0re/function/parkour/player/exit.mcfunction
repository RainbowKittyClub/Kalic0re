$function kalic0re:parkour/inv/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.playerInventorySaved
$function kalic0re:parkour/effects/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.playerEffectsSaved
$function kalic0re:parkour/player/goto {uuid:$(uuid),point:"lobby"}
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}]
