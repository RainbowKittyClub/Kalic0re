# As a firework's marker, at its burst point: $(stage) once for each point of its pattern (shapes), radius out, its
# colours and fade colours taken in turn.
data modify storage kalic0re:firework tmp.spark set from entity @s data
data remove storage kalic0re:firework tmp.points
function kalic0re:firework/pattern_points with entity @s data
# In fiftieths of a block, so a burst anywhere inside the world border still fits a score.
execute store result score #centerX kalic0re.firework.tmp run data get entity @s Pos[0] 50
execute store result score #centerY kalic0re.firework.tmp run data get entity @s Pos[1] 50
execute store result score #centerZ kalic0re.firework.tmp run data get entity @s Pos[2] 50
execute store result score #radius kalic0re.firework.tmp run data get storage kalic0re:firework tmp.spark.radius 1000
$function kalic0re:firework/next_point {stage:"$(stage)"}
data remove storage kalic0re:firework tmp.spark
data remove storage kalic0re:firework tmp.points
