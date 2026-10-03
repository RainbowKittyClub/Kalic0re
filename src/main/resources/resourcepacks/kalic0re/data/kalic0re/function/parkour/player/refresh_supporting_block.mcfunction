# As a runner who changed dimension, at their feet. A portal keeps the block they stood on before it, which
# stepping_on then tests at the same coordinates in the new dimension until the client moves, seconds later
# as it loads. A tp in place looks again, finding the block under them now.
tp @s ~ ~ ~
tag @s remove kalic0re.parkour.runnerChangedDimension
