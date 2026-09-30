execute unless data storage kalic0re:parkour tmp.scan[0] run return 0
# Writing the same value over a tag reports no change, so success 0 means this entry is the viewer's.
data modify storage kalic0re:parkour tmp.compare set from storage kalic0re:parkour tmp.scan[0].uuid
$execute store success score #notViewer kalic0re.parkour.tmp run data modify storage kalic0re:parkour tmp.compare set value $(uuid)
execute if score #notViewer kalic0re.parkour.tmp matches 0 run return 0
scoreboard players add #index kalic0re.parkour.tmp 1
data remove storage kalic0re:parkour tmp.scan[0]
$function kalic0re:parkour/leaderboard/find_viewer_step {uuid:$(uuid)}
