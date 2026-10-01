# As a player who used the leaderboard trigger. It rests at -1, so a bare /trigger, which adds 1,
# leaves 0 and can't pass for course 1. Enabled again after, since using a trigger disables it.
execute store result score #course kalic0re.parkour.tmp run scoreboard players get @s kalic0re.parkour.leaderboard
scoreboard players set @s kalic0re.parkour.leaderboard -1
scoreboard players enable @s kalic0re.parkour.leaderboard
execute unless score #course kalic0re.parkour.tmp matches 1.. run return run tellraw @s \
    {translate:"kalic0re.parkour.chat.leaderboard_needs_number", \
    fallback:"The leaderboard needs a course number. Use the link in a finish message, which has it.",color:"red"}
execute store result storage kalic0re:parkour tmp.board.id int 1 run scoreboard players get #course kalic0re.parkour.tmp
data modify storage kalic0re:parkour tmp.board.uuid set from entity @s UUID
function kalic0re:parkour/leaderboard/show with storage kalic0re:parkour tmp.board
data remove storage kalic0re:parkour tmp.board
