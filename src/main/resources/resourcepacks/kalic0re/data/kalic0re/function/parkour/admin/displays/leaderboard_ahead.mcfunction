# $(course)'s board of size $(size), $(distance) ahead along yaw $(yaw), level whatever your pitch, $(height)
# up, facing back.
$execute rotated $(yaw) 0 positioned ^ ^$(height) ^$(distance) run function kalic0re:parkour/admin/displays/leaderboard_sized \
    {course:"$(course)",position:"~ ~ ~",rotation:"~180 0",size:$(size)}
