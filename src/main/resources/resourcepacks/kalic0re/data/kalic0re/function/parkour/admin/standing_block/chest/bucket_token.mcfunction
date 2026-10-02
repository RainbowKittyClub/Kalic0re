$data modify storage kalic0re:parkour tmp.chest.bucketToken set value {slot:$(slot),block:"$(block)"}
$data modify storage kalic0re:parkour tmp.chest.bucketToken.bucket set from storage kalic0re:parkour enterableStandingBlocks[{block:"$(block)"}].bucket
function kalic0re:parkour/admin/standing_block/chest/bucket_item with storage kalic0re:parkour tmp.chest.bucketToken
data remove storage kalic0re:parkour tmp.chest.bucketToken
