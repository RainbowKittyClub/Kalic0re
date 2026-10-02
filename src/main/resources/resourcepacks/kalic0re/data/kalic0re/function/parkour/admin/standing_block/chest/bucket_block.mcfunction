# $(block), an item's id, becomes the block it's the bucket of, if it's one of enterableStandingBlocks'.
$data modify storage kalic0re:parkour tmp.chest.entry.block set from storage kalic0re:parkour enterableStandingBlocks[{bucket:"$(block)"}].block
