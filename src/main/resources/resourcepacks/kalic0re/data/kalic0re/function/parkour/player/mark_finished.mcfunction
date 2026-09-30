# The exit that follows records a run's time only when it's marked here. Guarded: a list filter
# that matches nothing adds a match, which would be a record for a run that never entered.
$execute if data storage kalic0re:parkour players[{uuid:$(uuid)}].course run \
    data modify storage kalic0re:parkour players[{uuid:$(uuid)}].finished set value 1b
