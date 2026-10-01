scoreboard players set @s kalic0re.parkour.isActive 0
# Reached only when isActive was set without kalic0re:parkour/start, so there is no course to name.
tellraw @s {translate:"kalic0re.parkour.chat.run_needs_course", \
    fallback:"Parkour course is missing. This is an error, tell an admin.",color:"red"}
