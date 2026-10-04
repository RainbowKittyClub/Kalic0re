# As a board's title, at it, for aim_at_leaderboard_display: where the look meets the board's face, in hundredths
# across (R) and up (U) from the title, kept if it's on the board and nearer than the best so far. The board is 4
# across, from its last rank's foot 3 below the title to the title's top 0.25 above, all times its size.
execute store result score #aimTitleX kalic0re.parkour.tmp run data get entity @s Pos[0] 100
execute store result score #aimTitleY kalic0re.parkour.tmp run data get entity @s Pos[1] 100
execute store result score #aimTitleZ kalic0re.parkour.tmp run data get entity @s Pos[2] 100
execute positioned ^ ^ ^10 summon minecraft:marker run function kalic0re:parkour/admin/course_boundary/wand/aim_read_axis {axis:"F"}
execute positioned ^10 ^ ^ summon minecraft:marker run function kalic0re:parkour/admin/course_boundary/wand/aim_read_axis {axis:"R"}
execute positioned ^ ^10 ^ summon minecraft:marker run function kalic0re:parkour/admin/course_boundary/wand/aim_read_axis {axis:"U"}
# P, from the eye to the title.
scoreboard players operation #aimPX kalic0re.parkour.tmp = #aimTitleX kalic0re.parkour.tmp
scoreboard players operation #aimPX kalic0re.parkour.tmp -= #aimEyeX kalic0re.parkour.tmp
scoreboard players operation #aimPY kalic0re.parkour.tmp = #aimTitleY kalic0re.parkour.tmp
scoreboard players operation #aimPY kalic0re.parkour.tmp -= #aimEyeY kalic0re.parkour.tmp
scoreboard players operation #aimPZ kalic0re.parkour.tmp = #aimTitleZ kalic0re.parkour.tmp
scoreboard players operation #aimPZ kalic0re.parkour.tmp -= #aimEyeZ kalic0re.parkour.tmp
# The look against the face's normal F, in hundred-thousandths; nearly along the face, under 1 in 20, it never meets it.
scoreboard players operation #aimLookAcross kalic0re.parkour.tmp = #aimDX kalic0re.parkour.tmp
scoreboard players operation #aimLookAcross kalic0re.parkour.tmp *= #aimFX kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp = #aimDY kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp *= #aimFY kalic0re.parkour.tmp
scoreboard players operation #aimLookAcross kalic0re.parkour.tmp += #aimTerm kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp = #aimDZ kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp *= #aimFZ kalic0re.parkour.tmp
scoreboard players operation #aimLookAcross kalic0re.parkour.tmp += #aimTerm kalic0re.parkour.tmp
scoreboard players operation #aimLookAcross kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
execute if score #aimLookAcross kalic0re.parkour.tmp matches -5000..5000 run return 0
# t, how far along the look it meets the face: (P·F) / (D·F), in hundredths; ahead, within 32 and nearer than the best.
scoreboard players operation #aimT kalic0re.parkour.tmp = #aimPX kalic0re.parkour.tmp
scoreboard players operation #aimT kalic0re.parkour.tmp *= #aimFX kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp = #aimPY kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp *= #aimFY kalic0re.parkour.tmp
scoreboard players operation #aimT kalic0re.parkour.tmp += #aimTerm kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp = #aimPZ kalic0re.parkour.tmp
scoreboard players operation #aimTerm kalic0re.parkour.tmp *= #aimFZ kalic0re.parkour.tmp
scoreboard players operation #aimT kalic0re.parkour.tmp += #aimTerm kalic0re.parkour.tmp
scoreboard players operation #aimT kalic0re.parkour.tmp *= #100 kalic0re.parkour.tmp
scoreboard players operation #aimT kalic0re.parkour.tmp /= #aimLookAcross kalic0re.parkour.tmp
execute unless score #aimT kalic0re.parkour.tmp matches 1..3200 run return 0
execute if score #aimT kalic0re.parkour.tmp >= #aimBestDistance kalic0re.parkour.tmp run return 0
# Q, from the title to where the look meets the face: t D - P.
function kalic0re:parkour/admin/course_boundary/wand/aim_meet_point
# Across and up the face, in hundredths, against the board's bounds at its size.
function kalic0re:parkour/admin/course_boundary/wand/aim_along {axis:"R"}
function kalic0re:parkour/admin/course_boundary/wand/aim_along {axis:"U"}
execute store result score #aimSize kalic0re.parkour.tmp run data get entity @s transformation.scale[1] 1000
scoreboard players operation #aimHalfWidth kalic0re.parkour.tmp = #aimSize kalic0re.parkour.tmp
scoreboard players operation #aimHalfWidth kalic0re.parkour.tmp *= #2 kalic0re.parkour.tmp
scoreboard players operation #aimHalfWidth kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
scoreboard players operation #aimTop kalic0re.parkour.tmp = #aimSize kalic0re.parkour.tmp
scoreboard players operation #aimTop kalic0re.parkour.tmp /= #4 kalic0re.parkour.tmp
scoreboard players operation #aimTop kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
scoreboard players operation #aimBottom kalic0re.parkour.tmp = #aimSize kalic0re.parkour.tmp
scoreboard players operation #aimBottom kalic0re.parkour.tmp *= #3 kalic0re.parkour.tmp
scoreboard players operation #aimBottom kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
scoreboard players operation #aimAcross kalic0re.parkour.tmp = #aimAlongR kalic0re.parkour.tmp
execute if score #aimAcross kalic0re.parkour.tmp matches ..-1 run scoreboard players operation #aimAcross kalic0re.parkour.tmp *= #-1 kalic0re.parkour.tmp
execute if score #aimAcross kalic0re.parkour.tmp > #aimHalfWidth kalic0re.parkour.tmp run return 0
execute if score #aimAlongU kalic0re.parkour.tmp > #aimTop kalic0re.parkour.tmp run return 0
scoreboard players operation #aimAlongU kalic0re.parkour.tmp *= #-1 kalic0re.parkour.tmp
execute if score #aimAlongU kalic0re.parkour.tmp > #aimBottom kalic0re.parkour.tmp run return 0
scoreboard players operation #aimBestDistance kalic0re.parkour.tmp = #aimT kalic0re.parkour.tmp
tag @e[type=minecraft:text_display,tag=kalic0re.parkour.leaderboardDisplayToEdit] remove kalic0re.parkour.leaderboardDisplayToEdit
tag @s add kalic0re.parkour.leaderboardDisplayToEdit
