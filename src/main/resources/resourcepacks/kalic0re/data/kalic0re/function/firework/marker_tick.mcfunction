# As a firework's marker, at its burst point: gone unseen if its show passed while it was unloaded, or it has no end
# (set down by an older pack); else each stage due by #now not yet played (its time then reset), late rather than lost.
execute unless score @s kalic0re.firework.endGameTime matches -2147483648.. run return run kill @s
execute if score @s kalic0re.firework.endGameTime < #staleBefore kalic0re.firework.tmp run return run kill @s
execute if score @s kalic0re.firework.burstGameTime <= #now kalic0re.firework.tmp run function kalic0re:firework/burst with entity @s data
execute if score @s kalic0re.firework.burstGameTime <= #now kalic0re.firework.tmp run scoreboard players reset @s kalic0re.firework.burstGameTime
execute if score @s kalic0re.firework.shellGameTime <= #now kalic0re.firework.tmp run function kalic0re:firework/sparks {stage:"shell_point"}
execute if score @s kalic0re.firework.shellGameTime <= #now kalic0re.firework.tmp run scoreboard players reset @s kalic0re.firework.shellGameTime
execute if score @s kalic0re.firework.twinkleGameTime <= #now kalic0re.firework.tmp run function kalic0re:firework/twinkle with entity @s data
execute if score @s kalic0re.firework.twinkleGameTime <= #now kalic0re.firework.tmp run scoreboard players reset @s kalic0re.firework.twinkleGameTime
execute if score @s kalic0re.firework.endGameTime <= #now kalic0re.firework.tmp run kill @s
