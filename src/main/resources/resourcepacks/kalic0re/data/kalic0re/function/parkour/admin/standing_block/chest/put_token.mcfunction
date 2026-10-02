# The token for $(block) in slot $(slot): a bucket for one a runner can be inside, else its own item, else a paper.
# token_item fails to instantiate where there's no item, and paper_token fills only what it left empty.
$execute if data storage kalic0re:parkour enterableStandingBlocks[{block:"$(block)"}] run return run \
    function kalic0re:parkour/admin/standing_block/chest/bucket_token {slot:$(slot),block:"$(block)"}
$function kalic0re:parkour/admin/standing_block/chest/token_item {slot:$(slot),block:"$(block)"}
$function kalic0re:parkour/admin/standing_block/chest/paper_token {slot:$(slot),block:"$(block)"}
