# For save and preview: the boxes in storage kalic0re:firework input as a firework at edited; 1 when every box
# reads, else the player is told the first that doesn't, and it fails.
data modify storage kalic0re:firework edited set value {}
execute unless function kalic0re:firework/editor/read_values run return fail
execute unless function kalic0re:firework/editor/read_position run return run function kalic0re:firework/editor/refuse {box:"position"}
execute unless function kalic0re:firework/editor/read_colors run return run function kalic0re:firework/editor/refuse {box:"colors"}
execute unless function kalic0re:firework/editor/read_fade_colors run return run function kalic0re:firework/editor/refuse {box:"fadeColors"}
execute unless function kalic0re:firework/editor/read_rise_color run return run function kalic0re:firework/editor/refuse {box:"riseColor"}
execute unless function kalic0re:firework/editor/read_particle run return run function kalic0re:firework/editor/refuse {box:"sparkParticle"}
scoreboard players set #read kalic0re.firework.tmp 0
execute store result score #read kalic0re.firework.tmp run function kalic0re:firework/editor/read_sound {box:"launchSound"}
execute if score #read kalic0re.firework.tmp matches 0 run return run function kalic0re:firework/editor/refuse {box:"launchSound"}
scoreboard players set #read kalic0re.firework.tmp 0
execute store result score #read kalic0re.firework.tmp run function kalic0re:firework/editor/read_sound {box:"blastSound"}
execute if score #read kalic0re.firework.tmp matches 0 run return run function kalic0re:firework/editor/refuse {box:"blastSound"}
scoreboard players set #read kalic0re.firework.tmp 0
execute store result score #read kalic0re.firework.tmp run function kalic0re:firework/editor/read_sound {box:"twinkleSound"}
execute if score #read kalic0re.firework.tmp matches 0 run return run function kalic0re:firework/editor/refuse {box:"twinkleSound"}
return 1
