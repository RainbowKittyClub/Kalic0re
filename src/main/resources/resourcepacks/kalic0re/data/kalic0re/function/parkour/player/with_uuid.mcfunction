# Runs $(run) with {uuid:<player UUID>}, the filter for their record in kalic0re:parkour players.
# Keyed by UUID, not a score: player scores follow the name, which can change mid-run.
# Cleared first: without an entity as @s the read fails, and a UUID left from the last call would pick
# someone else's record.
data remove storage kalic0re:parkour tmp.player
data modify storage kalic0re:parkour tmp.player.uuid set from entity @s UUID
$function $(run) with storage kalic0re:parkour tmp.player
