# For firework: course $(id)'s own finishFirework, when it has one, over the pack's.
$execute if data storage kalic0re:parkour courses[{id:$(id)}].finishFirework run data modify storage kalic0re:firework \
    launch merge from storage kalic0re:parkour courses[{id:$(id)}].finishFirework
