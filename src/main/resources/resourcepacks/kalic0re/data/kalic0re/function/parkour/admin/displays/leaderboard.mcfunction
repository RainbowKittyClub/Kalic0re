# /function kalic0re:parkour/admin/displays/leaderboard {course:"<name>",position:"<x y z>",rotation:"<yaw pitch>"}
# e.g. {course:"Church of the Verdant Light",position:"~ ~2 ~",rotation:"~180 0"}
# A board of the course's ten fastest times at size 1; leaderboard_sized takes a size too.
$function kalic0re:parkour/admin/displays/leaderboard_sized \
    {course:"$(course)",position:"$(position)",rotation:"$(rotation)",size:1}
