# The answer to a position and rotation that place_readable turned down, shown as typed; the dialog's texts cleared.
tellraw @s {translate:"kalic0re.parkour.chat.not_a_place",color:"red",with:[ \
    {nbt:"tmp.dialogTexts.position",storage:"kalic0re:parkour",interpret:true}, \
    {nbt:"tmp.dialogTexts.rotation",storage:"kalic0re:parkour",interpret:true}]}
data remove storage kalic0re:parkour tmp.dialogTexts
