$data remove storage kalic0re:parkour players[{uuid:$(uuid)}].checkpoint
$function kalic0re:parkour/player/goto {uuid:$(uuid),point:"start"}
# Restarting starts the timer over, after a finish too; going back to a checkpoint doesn't.
scoreboard players set @s kalic0re.parkour.runTicks 0
scoreboard players set @s kalic0re.parkour.isTimerRunning 1
