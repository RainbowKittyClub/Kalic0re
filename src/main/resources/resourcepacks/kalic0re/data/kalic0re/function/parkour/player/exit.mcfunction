$function kalic0re:parkour/inv/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.receivedParkourItems
$function kalic0re:parkour/player/goto {uuid:$(uuid),point:"lobby"}
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}]
