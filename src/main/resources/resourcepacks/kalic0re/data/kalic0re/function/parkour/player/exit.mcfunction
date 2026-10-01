$function kalic0re:parkour/inv/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.playerInventorySaved
$function kalic0re:parkour/effects/restore {uuid:$(uuid)}
tag @s remove kalic0re.parkour.playerEffectsSaved
# Before the teleport: reading the runner's name summons a display where they stand.
$function kalic0re:parkour/timer/record {uuid:$(uuid)}
$function kalic0re:parkour/player/goto {uuid:$(uuid),point:"lobby"}
scoreboard players reset @s kalic0re.parkour.runTicks
scoreboard players reset @s kalic0re.parkour.courseBestTicks
scoreboard players reset @s kalic0re.parkour.runningCourseId
scoreboard players reset @s kalic0re.parkour.runnerTickPhaseOf2
scoreboard players reset @s kalic0re.parkour.runnerTickPhaseOf4
scoreboard players reset @s kalic0re.parkour.runnerTickPhaseOf16
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}]
