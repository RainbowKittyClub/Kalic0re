# As the board edit_nearest found: its course's name and any title of its own into kalic0re:dialog args, and its
# size as #leaderboardDisplaySize in thousandths, to the slider's quarter steps from 0.25 to 4, outside which it
# won't open. A title of the course's own leaves Title empty, so Apply keeps it following the course.
execute if entity @s[tag=kalic0re.parkour.leaderboardDisplayCustomTitle] run \
    data modify storage kalic0re:dialog args.current_title set from entity @s text.text
execute store result storage kalic0re:parkour tmp.leaderboardDisplayEdit.id int 1 run \
    scoreboard players get @s kalic0re.parkour.leaderboardDisplayCourseId
function kalic0re:parkour/admin/displays/edit_nearest_course with storage kalic0re:parkour tmp.leaderboardDisplayEdit
data remove storage kalic0re:parkour tmp.leaderboardDisplayEdit
execute store result score #leaderboardDisplaySize kalic0re.parkour.tmp run data get entity @s transformation.scale[1] 1000
scoreboard players add #leaderboardDisplaySize kalic0re.parkour.tmp 125
scoreboard players operation #leaderboardDisplaySize kalic0re.parkour.tmp /= #250 kalic0re.parkour.tmp
scoreboard players operation #leaderboardDisplaySize kalic0re.parkour.tmp *= #250 kalic0re.parkour.tmp
execute if score #leaderboardDisplaySize kalic0re.parkour.tmp matches ..249 run \
    scoreboard players set #leaderboardDisplaySize kalic0re.parkour.tmp 250
execute if score #leaderboardDisplaySize kalic0re.parkour.tmp matches 4001.. run \
    scoreboard players set #leaderboardDisplaySize kalic0re.parkour.tmp 4000
