# For read: the player told the $(box) box doesn't read, with what's in it; fails, so read does.
$tellraw @s {translate:"kalic0re.create_firework.refused",color:"red",with:[ \
    {nbt:"labels.$(box)",storage:"kalic0re:firework",interpret:true}, \
    {nbt:"input.texts.$(box)",storage:"kalic0re:firework",interpret:true}]}
return fail
