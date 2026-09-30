# Adds a line for each of the first 21 entries of tmp.board.times, the viewer's time in yellow.
execute if score #lines kalic0re.parkour.tmp matches 21.. run return 0
execute unless data storage kalic0re:parkour tmp.board.times[0] run return 0
data modify storage kalic0re:dialog args set value {color:"white"}
data modify storage kalic0re:dialog args.player set from storage kalic0re:parkour tmp.board.times[0].player
data modify storage kalic0re:parkour tmp.compare set from storage kalic0re:parkour tmp.board.times[0].uuid
$execute store success score #notViewer kalic0re.parkour.tmp run data modify storage kalic0re:parkour tmp.compare set value $(uuid)
execute if score #notViewer kalic0re.parkour.tmp matches 0 run data modify storage kalic0re:dialog args.color set value "yellow"
execute store result storage kalic0re:dialog args.rank int 1 run scoreboard players get #rank kalic0re.parkour.tmp
execute store result score #ticks kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.board.times[0].ticks
function kalic0re:parkour/timer/split_into_args
function kalic0re:parkour/leaderboard/leaderboard_line
execute if score #lines kalic0re.parkour.tmp matches 1.. run \
    data modify storage kalic0re:dialog built.leaderboard.body.contents.extra append value {text:"\n"}
data modify storage kalic0re:dialog built.leaderboard.body.contents.extra append from storage kalic0re:dialog built.leaderboard_line
scoreboard players add #lines kalic0re.parkour.tmp 1
scoreboard players add #rank kalic0re.parkour.tmp 1
data remove storage kalic0re:parkour tmp.board.times[0]
data remove storage kalic0re:parkour tmp.compare
$function kalic0re:parkour/leaderboard/add_lines {uuid:$(uuid)}
