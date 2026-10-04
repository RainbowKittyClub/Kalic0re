# As the title edit_board opens: its course's name, any title of its own and its position into kalic0re:dialog
# args, and its size as #leaderboardDisplaySize in thousandths, to the slider's quarter steps from 0.25 to 4,
# outside which it won't open. A title of the course's own leaves Title empty, so Apply keeps it following the course.
data modify storage kalic0re:dialog args.x set from entity @s Pos[0]
data modify storage kalic0re:dialog args.y set from entity @s Pos[1]
data modify storage kalic0re:dialog args.z set from entity @s Pos[2]
# Position's text, exactly: a double as text is "601.5d" or "-296.0d", the d cut off. A macro would write -296.0
# as -296, which positioned takes as the block's centre, so an unchanged Apply would move the board.
data modify storage kalic0re:parkour tmp.leaderboardDisplayPosition set value {}
data modify storage kalic0re:parkour tmp.leaderboardDisplayPosition.x set string entity @s Pos[0] 0 -1
data modify storage kalic0re:parkour tmp.leaderboardDisplayPosition.y set string entity @s Pos[1] 0 -1
data modify storage kalic0re:parkour tmp.leaderboardDisplayPosition.z set string entity @s Pos[2] 0 -1
function kalic0re:parkour/admin/displays/edit_board_position_text with storage kalic0re:parkour tmp.leaderboardDisplayPosition
data remove storage kalic0re:parkour tmp.leaderboardDisplayPosition
execute if entity @s[tag=kalic0re.parkour.leaderboardDisplayCustomTitle] run \
    data modify storage kalic0re:dialog args.current_title set from entity @s text.text
execute store result storage kalic0re:parkour tmp.leaderboardDisplayEdit.id int 1 run \
    scoreboard players get @s kalic0re.parkour.leaderboardDisplayCourseId
function kalic0re:parkour/admin/displays/edit_board_course with storage kalic0re:parkour tmp.leaderboardDisplayEdit
data remove storage kalic0re:parkour tmp.leaderboardDisplayEdit
execute store result score #leaderboardDisplaySize kalic0re.parkour.tmp run data get entity @s transformation.scale[1] 1000
scoreboard players add #leaderboardDisplaySize kalic0re.parkour.tmp 125
scoreboard players operation #leaderboardDisplaySize kalic0re.parkour.tmp /= #250 kalic0re.parkour.tmp
scoreboard players operation #leaderboardDisplaySize kalic0re.parkour.tmp *= #250 kalic0re.parkour.tmp
execute if score #leaderboardDisplaySize kalic0re.parkour.tmp matches ..249 run \
    scoreboard players set #leaderboardDisplaySize kalic0re.parkour.tmp 250
execute if score #leaderboardDisplaySize kalic0re.parkour.tmp matches 4001.. run \
    scoreboard players set #leaderboardDisplaySize kalic0re.parkour.tmp 4000
