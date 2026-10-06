# As a board's title: tmp.leaderboardDisplayRetitle, bold and yellow like the course's own, which an empty or
# missing one goes back to. Copied from storage, never through a macro, so it's kept exactly, quotes and all.
data modify storage kalic0re:parkour tmp.leaderboardDisplayTitle set value {text:"",bold:true,color:"yellow"}
data modify storage kalic0re:parkour tmp.leaderboardDisplayTitle.text set from storage kalic0re:parkour tmp.leaderboardDisplayRetitle
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
