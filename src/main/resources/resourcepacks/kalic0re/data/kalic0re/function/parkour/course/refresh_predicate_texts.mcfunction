# Keeps $(course)'s rules as one predicate, as text, which the runner check passes to a macro: any_of a
# forbidden block underfoot, an exclusion boundary holding them, or no course boundary holding them. A
# compound argument is written out as text on every call (40 µs for 27 boundaries); text is taken as it is.
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].runnerOutOfBoundsPredicateText
# Texts of each rule alone, which the check no longer reads.
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicateText
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicateText
$data remove storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicateText
$data modify storage kalic0re:parkour tmp.runnerOutOfBounds set value \
    {course:"$(course)",predicate:{condition:"minecraft:any_of",terms:[]}}
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicate run \
    data modify storage kalic0re:parkour tmp.runnerOutOfBounds.predicate.terms append from \
    storage kalic0re:parkour courses[{name:"$(course)"}].forbiddenStandingBlockPredicate
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicate run \
    data modify storage kalic0re:parkour tmp.runnerOutOfBounds.predicate.terms append from \
    storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicate
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate run \
    data modify storage kalic0re:parkour tmp.runnerOutOfBounds.predicate.terms append value \
    {condition:"minecraft:inverted"}
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate run \
    data modify storage kalic0re:parkour tmp.runnerOutOfBounds.predicate.terms[-1].term set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].courseBoundaryPredicate
execute if data storage kalic0re:parkour tmp.runnerOutOfBounds.predicate.terms[0] run \
    function kalic0re:parkour/course/runner_out_of_bounds_predicate_text with storage kalic0re:parkour tmp.runnerOutOfBounds
data remove storage kalic0re:parkour tmp.runnerOutOfBounds
