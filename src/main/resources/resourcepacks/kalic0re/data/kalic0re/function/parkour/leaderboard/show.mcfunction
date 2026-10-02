# Shows course $(id)'s leaderboard to the player with $(uuid): their time with the 10 above and 10
# below, the window shifted to stay inside the list; from the top if they have no time there.
$execute unless data storage kalic0re:parkour courses[{id:$(id)}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_course_number",with:["$(id)"],color:"red"}
data modify storage kalic0re:dialog args set value {}
$data modify storage kalic0re:dialog args.course set from storage kalic0re:parkour courses[{id:$(id)}].name
function kalic0re:parkour/leaderboard/leaderboard
data modify storage kalic0re:parkour tmp.board.times set value []
$data modify storage kalic0re:parkour tmp.board.times set from storage kalic0re:parkour courses[{id:$(id)}].times
execute unless data storage kalic0re:parkour tmp.board.times[0] run \
    data modify storage kalic0re:dialog built.leaderboard.body.contents set value \
    {translate:"kalic0re.parkour.menu.leaderboard.empty"}
$function kalic0re:parkour/leaderboard/find_viewer {uuid:$(uuid)}
execute store result score #count kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.board.times
execute if score #index kalic0re.parkour.tmp = #count kalic0re.parkour.tmp run scoreboard players set #index kalic0re.parkour.tmp 0
scoreboard players operation #start kalic0re.parkour.tmp = #index kalic0re.parkour.tmp
scoreboard players remove #start kalic0re.parkour.tmp 10
scoreboard players operation #lastStart kalic0re.parkour.tmp = #count kalic0re.parkour.tmp
scoreboard players remove #lastStart kalic0re.parkour.tmp 21
scoreboard players operation #start kalic0re.parkour.tmp < #lastStart kalic0re.parkour.tmp
execute if score #start kalic0re.parkour.tmp matches ..-1 run scoreboard players set #start kalic0re.parkour.tmp 0
# Ranks count from the top of the whole list, not the window.
scoreboard players operation #rank kalic0re.parkour.tmp = #start kalic0re.parkour.tmp
scoreboard players add #rank kalic0re.parkour.tmp 1
function kalic0re:parkour/leaderboard/skip
scoreboard players set #lines kalic0re.parkour.tmp 0
$function kalic0re:parkour/leaderboard/add_lines {uuid:$(uuid)}
function kalic0re:dialog/show {name:"leaderboard"}
