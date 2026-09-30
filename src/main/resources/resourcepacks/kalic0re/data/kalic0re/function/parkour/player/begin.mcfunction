# Starting (or re-starting) a course forgets any checkpoint from the previous one.
$data modify storage kalic0re:parkour players[{uuid:$(uuid)}].course set from storage kalic0re:parkour tmp.course
$data remove storage kalic0re:parkour players[{uuid:$(uuid)}].checkpoint
