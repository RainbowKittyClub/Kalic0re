# add_from_dialog's answer to $(corner)'s text that's no position, shown as typed, with what it read so far cleared.
$tellraw @s {translate:"kalic0re.parkour.chat.not_a_block_position",color:"red",with:[ \
    {nbt:"tmp.dialogTexts.$(corner)",storage:"kalic0re:parkour",interpret:true}]}
data remove storage kalic0re:parkour tmp.boundaryDialog
data remove storage kalic0re:parkour tmp.dialogTexts
data remove storage kalic0re:parkour tmp.textCheck
