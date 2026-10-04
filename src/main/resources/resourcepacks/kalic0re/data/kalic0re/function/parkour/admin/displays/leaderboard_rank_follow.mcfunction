# As a rank riding its title, for leaderboard_ranks_follow_title: the title's facing and transformation, its
# translation 0.3 per rank down from the title's, against #rotated, the title's up.
scoreboard players operation #leaderboardDisplayRankOffset kalic0re.parkour.tmp = @s kalic0re.parkour.leaderboardDisplayLine
scoreboard players operation #leaderboardDisplayRankOffset kalic0re.parkour.tmp *= #3 kalic0re.parkour.tmp
function kalic0re:parkour/admin/displays/leaderboard_rank_follow_axis {axis:"X",index:0}
function kalic0re:parkour/admin/displays/leaderboard_rank_follow_axis {axis:"Y",index:1}
function kalic0re:parkour/admin/displays/leaderboard_rank_follow_axis {axis:"Z",index:2}
data modify entity @s transformation set from storage kalic0re:parkour tmp.leaderboardDisplayFollow.transformation
data modify entity @s Rotation set from storage kalic0re:parkour tmp.leaderboardDisplayFollow.Rotation
