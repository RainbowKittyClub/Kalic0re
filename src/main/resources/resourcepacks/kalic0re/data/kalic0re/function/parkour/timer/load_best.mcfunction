# The runner's best on $(course), for the timer; left at -1 when they have none. Looked up first
# because execute store writes 0 when its command fails.
$execute if data storage kalic0re:parkour courses[{name:"$(course)"}].times[{uuid:$(uuid)}] \
    store result score @s kalic0re.parkour.courseBestTicks run \
    data get storage kalic0re:parkour courses[{name:"$(course)"}].times[{uuid:$(uuid)}].ticks
