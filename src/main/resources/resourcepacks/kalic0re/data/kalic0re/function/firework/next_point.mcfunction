# For sparks: the first point left, as a spark's velocity (dx, dy, dz, in radii), its offset radius out (ox, oy, oz)
# and that place in the world (tx, ty, tz), both to a fiftieth of a block, with the next colour and fade colour; then
# $(stage) with them, and the rest.
execute unless data storage kalic0re:firework tmp.points[0] run return 0
execute store result score #alongX kalic0re.firework.tmp run data get storage kalic0re:firework tmp.points[0][0]
execute store result score #alongY kalic0re.firework.tmp run data get storage kalic0re:firework tmp.points[0][1]
execute store result score #alongZ kalic0re.firework.tmp run data get storage kalic0re:firework tmp.points[0][2]
execute store result storage kalic0re:firework tmp.spark.dx double 0.001 run scoreboard players get #alongX kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.dy double 0.001 run scoreboard players get #alongY kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.dz double 0.001 run scoreboard players get #alongZ kalic0re.firework.tmp
scoreboard players operation #alongX kalic0re.firework.tmp *= #radius kalic0re.firework.tmp
scoreboard players operation #alongY kalic0re.firework.tmp *= #radius kalic0re.firework.tmp
scoreboard players operation #alongZ kalic0re.firework.tmp *= #radius kalic0re.firework.tmp
scoreboard players operation #alongX kalic0re.firework.tmp /= #20000 kalic0re.firework.tmp
scoreboard players operation #alongY kalic0re.firework.tmp /= #20000 kalic0re.firework.tmp
scoreboard players operation #alongZ kalic0re.firework.tmp /= #20000 kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.ox double 0.02 run scoreboard players get #alongX kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.oy double 0.02 run scoreboard players get #alongY kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.oz double 0.02 run scoreboard players get #alongZ kalic0re.firework.tmp
scoreboard players operation #alongX kalic0re.firework.tmp += #centerX kalic0re.firework.tmp
scoreboard players operation #alongY kalic0re.firework.tmp += #centerY kalic0re.firework.tmp
scoreboard players operation #alongZ kalic0re.firework.tmp += #centerZ kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.tx double 0.02 run scoreboard players get #alongX kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.ty double 0.02 run scoreboard players get #alongY kalic0re.firework.tmp
execute store result storage kalic0re:firework tmp.spark.tz double 0.02 run scoreboard players get #alongZ kalic0re.firework.tmp
data modify storage kalic0re:firework tmp.spark.color set from storage kalic0re:firework tmp.spark.colors[0]
data modify storage kalic0re:firework tmp.spark.colors append from storage kalic0re:firework tmp.spark.colors[0]
data remove storage kalic0re:firework tmp.spark.colors[0]
data modify storage kalic0re:firework tmp.spark.fade set from storage kalic0re:firework tmp.spark.fadeColors[0]
data modify storage kalic0re:firework tmp.spark.fadeColors append from storage kalic0re:firework tmp.spark.fadeColors[0]
data remove storage kalic0re:firework tmp.spark.fadeColors[0]
$function kalic0re:firework/$(stage) with storage kalic0re:firework tmp.spark
data remove storage kalic0re:firework tmp.points[0]
$function kalic0re:firework/next_point {stage:"$(stage)"}
