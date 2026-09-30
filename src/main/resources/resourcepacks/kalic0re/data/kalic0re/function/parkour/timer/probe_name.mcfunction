# Puts the runner's name in tmp.record.player. Commands can't read a player's name directly, but a
# player head filled with their profile holds it as plain text.
tag @s add kalic0re.parkour.nameProbeSubject
execute summon item_display run function kalic0re:parkour/timer/read_name
tag @s remove kalic0re.parkour.nameProbeSubject
