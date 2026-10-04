# tmp.numberText.value as tmp.numberText.text that positioned and rotated read back the same: its own text, but
# never E notation, which they can't read (under a thousandth is 0.0; from 10 million, number_large).
execute store result score #numberTextMillionths kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.numberText.value 1000000
execute if score #numberTextMillionths kalic0re.parkour.tmp matches -1000..999 run return run \
    data modify storage kalic0re:parkour tmp.numberText.text set value "0.0"
execute store result score #numberTextWhole kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.numberText.value
execute store result storage kalic0re:parkour tmp.numberText.whole int 1 run scoreboard players get #numberTextWhole kalic0re.parkour.tmp
execute unless score #numberTextWhole kalic0re.parkour.tmp matches -9999999..9999999 run return run \
    function kalic0re:parkour/admin/text/number_large with storage kalic0re:parkour tmp.numberText
data modify storage kalic0re:parkour tmp.numberText.text set string storage kalic0re:parkour tmp.numberText.value 0 -1
