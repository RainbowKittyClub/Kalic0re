$data modify storage kalic0re:parkour courses[{name:"$(course)"}].times insert $(index) \
    value {uuid:$(uuid),player:"$(player)",ticks:$(best)}
$scoreboard players set $(player) kalic0re.parkour.bestTicks.$(id) $(best)
