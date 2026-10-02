# Keeps each of $(course)'s predicates also as text, which runner checks pass to a macro: a compound
# argument is written out as text on every call (40 µs for 27 boundaries), where text is taken as it is.
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicateText
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicateText
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicateText
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate run \
    function kalic0re:parkour/course/course_boundary_predicate_text with storage kalic0re:parkour courses[{name:"$(course)"}]
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicate run \
    function kalic0re:parkour/course/exclusion_boundary_predicate_text with storage kalic0re:parkour courses[{name:"$(course)"}]
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicate run \
    function kalic0re:parkour/course/forbidden_standing_block_predicate_text with storage kalic0re:parkour courses[{name:"$(course)"}]
