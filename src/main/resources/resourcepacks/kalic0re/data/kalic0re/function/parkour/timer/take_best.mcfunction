# Keeps the player's saved best in #best when it's no slower than this run, then drops their entry
# and its score, which sits under the name they had when it was saved.
$execute store result score #savedBest kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].times[{uuid:$(uuid)}].ticks
execute if score #savedBest kalic0re.parkour.tmp <= #best kalic0re.parkour.tmp run scoreboard players set #newBest kalic0re.parkour.tmp 0
scoreboard players operation #best kalic0re.parkour.tmp < #savedBest kalic0re.parkour.tmp
data modify storage kalic0re:parkour tmp.forget set value {}
$data modify storage kalic0re:parkour tmp.forget.player set from storage kalic0re:parkour courses[{name:"$(course)"}].times[{uuid:$(uuid)}].player
$data modify storage kalic0re:parkour tmp.forget.id set from storage kalic0re:parkour courses[{name:"$(course)"}].id
function kalic0re:parkour/timer/reset_score with storage kalic0re:parkour tmp.forget
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].times[{uuid:$(uuid)}]
data remove storage kalic0re:parkour tmp.forget
