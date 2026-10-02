# Marks $(block) listed if it's one of enterableStandingBlocks. Returns 1 unless it's also one a runner can
# stand on, which is then sorted as a block too.
$execute unless data storage kalic0re:parkour tmp.standingBlockPredicate.enterables[{block:"$(block)"}] run return 0
$data modify storage kalic0re:parkour tmp.standingBlockPredicate.enterables[{block:"$(block)"}].listed set value 1b
$execute if data storage kalic0re:parkour tmp.standingBlockPredicate.enterables[{block:"$(block)",standable:1b}] run return 0
return 1
