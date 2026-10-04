# /function kalic0re:parkour/admin/course_boundary/add {course:"<name>",dim:"<dimension>",x1:<x>,y1:<y>,z1:<z>,x2:<x>,y2:<y>,z2:<z>}
# Block corners, either order, both taken in. Runners' feet must stay in some boundary, so together
# they must hold start, checkpoints, finish and the air jumped through.
$function kalic0re:parkour/admin/course_boundary/add_boundary {course:"$(course)",dim:"$(dim)", \
    x1:$(x1),y1:$(y1),z1:$(z1),x2:$(x2),y2:$(y2),z2:$(z2), \
    boundaries:"courseBoundaries",boundaryPredicate:"courseBoundaryPredicate",key:"course_boundary",widenTermToFootprint:0}
