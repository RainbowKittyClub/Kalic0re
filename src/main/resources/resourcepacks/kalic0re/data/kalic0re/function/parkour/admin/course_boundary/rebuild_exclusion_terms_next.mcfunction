execute unless data storage kalic0re:parkour tmp.exclusionTerms.boundaries[0] run return 0
execute store result score #x1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].min[0]
execute store result score #y1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].min[1]
execute store result score #z1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].min[2]
execute store result score #x2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].max[0]
execute store result score #y2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].max[1]
execute store result score #z2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].max[2]
function kalic0re:parkour/admin/course_boundary/build_boundary
function kalic0re:parkour/admin/course_boundary/widen_term_to_footprint
# A boundary saved before boundaries had dimensions has none, and its term held every dimension.
data modify storage kalic0re:parkour tmp.newCourseBoundary.term.predicate.dimension set from \
    storage kalic0re:parkour tmp.exclusionTerms.boundaries[0].dim
data modify storage kalic0re:parkour tmp.exclusionTerms.terms append from storage kalic0re:parkour tmp.newCourseBoundary.term
data remove storage kalic0re:parkour tmp.exclusionTerms.boundaries[0]
function kalic0re:parkour/admin/course_boundary/rebuild_exclusion_terms_next
