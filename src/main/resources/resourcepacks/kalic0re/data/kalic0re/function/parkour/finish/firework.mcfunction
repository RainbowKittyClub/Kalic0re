# As a runner who just finished, where they stand: kalic0re:firework/launch with the default course finishFirework
# (parkour/load) and their course's own over it; anything neither sets is the firework module's default.
data modify storage kalic0re:firework launch set value {}
data modify storage kalic0re:firework launch merge from storage kalic0re:parkour finishFirework
execute store result storage kalic0re:parkour tmp.finishFireworkCourse.id int 1 run \
    scoreboard players get @s kalic0re.parkour.runningCourseId
function kalic0re:parkour/finish/course_firework with storage kalic0re:parkour tmp.finishFireworkCourse
data remove storage kalic0re:parkour tmp.finishFireworkCourse
function kalic0re:firework/launch
data remove storage kalic0re:firework launch
