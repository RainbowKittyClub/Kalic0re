# $(course)'s exclusion terms rebuilt from its exclusion boundaries, at load, as add_exclusion makes them: terms
# saved before they held a runner's whole footprint catch up. Each stays at its boundary's index.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaries[0] run return 0
data modify storage kalic0re:parkour tmp.exclusionTerms set value {terms:[]}
$data modify storage kalic0re:parkour tmp.exclusionTerms.boundaries set from \
    storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaries
function kalic0re:parkour/admin/course_boundary/rebuild_exclusion_terms_next
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicate set value \
    {condition:"minecraft:any_of",terms:[]}
$data modify storage kalic0re:parkour courses[{name:"$(course)"}].courseExclusionBoundaryPredicate.terms set from \
    storage kalic0re:parkour tmp.exclusionTerms.terms
data remove storage kalic0re:parkour tmp.exclusionTerms
data remove storage kalic0re:parkour tmp.newCourseBoundary
