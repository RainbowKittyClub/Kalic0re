# As a board's title: $(title), bold and yellow like the course's own, which an empty one goes back to. A
# title holding " or \ fails whole, as course names do: a macro writes a string back unescaped.
$data modify storage kalic0re:parkour tmp.leaderboardDisplayTitle set value {text:"$(title)",bold:true,color:"yellow"}
execute store result score #leaderboardDisplayTitleLength kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.leaderboardDisplayTitle.text
execute if score #leaderboardDisplayTitleLength kalic0re.parkour.tmp matches 1.. run \
    data modify entity @s text set from storage kalic0re:parkour tmp.leaderboardDisplayTitle
execute if score #leaderboardDisplayTitleLength kalic0re.parkour.tmp matches 1.. run \
    tag @s add kalic0re.parkour.leaderboardDisplayCustomTitle
data remove storage kalic0re:parkour tmp.leaderboardDisplayTitle
execute if score #leaderboardDisplayTitleLength kalic0re.parkour.tmp matches 0 run \
    tag @s remove kalic0re.parkour.leaderboardDisplayCustomTitle
execute if score #leaderboardDisplayTitleLength kalic0re.parkour.tmp matches 0 run \
    function kalic0re:parkour/admin/displays/leaderboard_refresh_board
