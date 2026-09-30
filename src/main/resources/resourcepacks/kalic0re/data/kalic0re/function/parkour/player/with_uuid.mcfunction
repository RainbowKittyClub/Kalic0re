# Runs $(run) with {uuid:<player UUID>}, the filter for their record in kalic0re:parkour players.
# Keyed by UUID, not a score: player scores follow the name, which can change mid-run.
data modify storage kalic0re:parkour tmp.player.uuid set from entity @s UUID
$function $(run) with storage kalic0re:parkour tmp.player
