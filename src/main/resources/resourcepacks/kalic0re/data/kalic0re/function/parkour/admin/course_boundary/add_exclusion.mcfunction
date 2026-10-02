# /function kalic0re:parkour/admin/course_boundary/add_exclusion {course:"<name>",dim:"<dimension>",x1:<x>,y1:<y>,z1:<z>,x2:<x>,y2:<y>,z2:<z>}
# Block corners, either order, both taken in. Runners' feet inside one are out, whatever course
# boundaries also hold them.
$function kalic0re:parkour/admin/course_boundary/add_boundary {course:"$(course)",dim:"$(dim)", \
    x1:$(x1),y1:$(y1),z1:$(z1),x2:$(x2),y2:$(y2),z2:$(z2), \
    boundaries:"courseExclusionBoundaries",boundaryPredicate:"courseExclusionBoundaryPredicate",key:"exclusion_boundary",name:"exclusion boundary"}
