$execute unless data storage kalic0re:parkour players[{uuid:$(uuid),course:"$(course)"}] run return 0
# Exit now rather than on the next tick, when the course and its lobby are gone.
scoreboard players set @s kalic0re.parkour.isActive 0
$function kalic0re:parkour/player/exit {uuid:$(uuid)}
$tellraw @s {text:"Parkour course \"$(course)\" was deleted, so your run has ended.",color:"yellow"}
