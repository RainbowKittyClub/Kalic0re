# Sets #index to the viewer's place in tmp.board.times, or to its length if they have no time.
scoreboard players set #index kalic0re.parkour.tmp 0
data modify storage kalic0re:parkour tmp.scan set from storage kalic0re:parkour tmp.board.times
$function kalic0re:parkour/leaderboard/find_viewer_step {uuid:$(uuid)}
data remove storage kalic0re:parkour tmp.scan
data remove storage kalic0re:parkour tmp.compare
