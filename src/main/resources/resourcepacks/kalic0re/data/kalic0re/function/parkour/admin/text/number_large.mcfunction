# For number, from 10 million out: as a macro writes it, plus the .0 it drops from a whole number, without which
# positioned would take it as the block's centre.
$data modify storage kalic0re:parkour tmp.numberText.text set value "$(value)"
$execute if data storage kalic0re:parkour tmp.numberText{text:"$(whole)"} run \
    data modify storage kalic0re:parkour tmp.numberText.text set value "$(whole).0"
