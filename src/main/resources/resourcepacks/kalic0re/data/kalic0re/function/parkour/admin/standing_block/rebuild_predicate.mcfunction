# $(course)'s forbiddenStandingBlockPredicate from its standing blocks and mode, and its text; none
# without standing blocks, so an empty allowlist forbids nothing.
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicate
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].standingBlocks[0] run \
    function kalic0re:parkour/admin/standing_block/build_predicate {course:"$(course)"}
$function kalic0re:parkour/course/refresh_predicate_texts {course:"$(course)"}
