# As a runner on $(course), which is being deleted: exits now rather than on the next tick, when
# the course and its lobby are gone.
scoreboard players set @s kalic0re.parkour.isActive 0
$function kalic0re:parkour/player/exit {uuid:$(uuid)}
$tellraw @s {translate:"kalic0re.parkour.chat.run_ended_by_delete",with:["$(course)"],color:"yellow"}
