# For only_of: 1 when what's left of check is all class $(class); a character a call.
execute if data storage kalic0re:text {check:""} run return 1
$data modify storage kalic0re:text step set value {class:"$(class)"}
data modify storage kalic0re:text step.char set string storage kalic0re:text check 0 1
scoreboard players set #textCharIn kalic0re.text.tmp 0
execute store result score #textCharIn kalic0re.text.tmp run function kalic0re:text/char_in with storage kalic0re:text step
execute if score #textCharIn kalic0re.text.tmp matches 0 run return fail
data modify storage kalic0re:text check set string storage kalic0re:text check 1
$return run function kalic0re:text/only {class:"$(class)"}
