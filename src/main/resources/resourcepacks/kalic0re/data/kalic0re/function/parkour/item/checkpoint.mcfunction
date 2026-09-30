$execute unless data storage kalic0re:parkour players[{uuid:$(uuid)}].checkpoint run \
    return run function kalic0re:parkour/player/goto {uuid:$(uuid),point:"start"}
$function kalic0re:parkour/pos/tp with storage kalic0re:parkour players[{uuid:$(uuid)}].checkpoint
