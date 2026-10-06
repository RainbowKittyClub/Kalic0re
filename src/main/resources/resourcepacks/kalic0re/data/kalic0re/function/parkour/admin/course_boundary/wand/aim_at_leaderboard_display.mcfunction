# As the wand's holder: 1 when they aim at a leaderboard display within 32 with no block before it, its title then
# tagged leaderboardDisplayToEdit; else 0. Worked out in each board's own frame, so turned and resized ones count.
execute unless entity @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..40] run return 0
execute anchored eyes positioned ^ ^ ^ summon minecraft:marker run \
    function kalic0re:parkour/admin/course_boundary/wand/aim_read_eye
execute anchored eyes positioned ^ ^ ^10 summon minecraft:marker run \
    function kalic0re:parkour/admin/course_boundary/wand/aim_read_look
scoreboard players set #aimBestDistance kalic0re.parkour.tmp 3201
execute as @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplay, \
    scores={kalic0re.parkour.leaderboardDisplayLine=0},distance=..40] at @s run \
    function kalic0re:parkour/admin/course_boundary/wand/aim_test_board
execute if score #aimBestDistance kalic0re.parkour.tmp matches 3201 run return 0
# A block before the board takes the click instead: the corner ray, a tenth of a block a step, stopped at the board.
data remove storage kalic0re:parkour tmp.wand.pos
scoreboard players operation #rayStepsLeft kalic0re.parkour.tmp = #aimBestDistance kalic0re.parkour.tmp
scoreboard players operation #rayStepsLeft kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
execute anchored eyes positioned ^ ^ ^ anchored feet run \
    function kalic0re:parkour/admin/look_ray/step {hit:"kalic0re:parkour/admin/course_boundary/wand/mark_here"}
execute store success score #aimBlocked kalic0re.parkour.tmp if data storage kalic0re:parkour tmp.wand.pos
data remove storage kalic0re:parkour tmp.wand.pos
execute if score #aimBlocked kalic0re.parkour.tmp matches 1 run tag @e[type=minecraft:text_display, \
    tag=kalic0re.parkour.leaderboardDisplayToEdit] remove kalic0re.parkour.leaderboardDisplayToEdit
execute if score #aimBlocked kalic0re.parkour.tmp matches 1 run return 0
return 1
