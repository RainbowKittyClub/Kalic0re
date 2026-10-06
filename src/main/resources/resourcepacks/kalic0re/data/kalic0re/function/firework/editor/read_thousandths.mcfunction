# For read_values: slider values.$(key) as edited.$(key), a $(type) rounded to the thousandth. The client's float steps
# land just under (0.29999998 for 0.3), which data get's flooring would keep.
$execute unless data storage kalic0re:firework input.values.$(key) run return 0
$execute store result score #thousandths kalic0re.firework.tmp run data get storage kalic0re:firework input.values.$(key) 10000
scoreboard players add #thousandths kalic0re.firework.tmp 5
scoreboard players operation #thousandths kalic0re.firework.tmp /= #10 kalic0re.firework.tmp
$execute store result storage kalic0re:firework edited.$(key) $(type) 0.001 run scoreboard players get #thousandths kalic0re.firework.tmp
