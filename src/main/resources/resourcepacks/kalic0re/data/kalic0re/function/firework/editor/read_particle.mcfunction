# For read: the Spark Particle box, empty for the default or a particle that takes no options, as edited.sparkParticle.
data modify storage kalic0re:firework edited.sparkParticle set value ""
execute if data storage kalic0re:firework input.texts{sparkParticle:""} run return 1
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:firework input.texts.sparkParticle
execute unless function kalic0re:text/only_resource_id run return fail
scoreboard players set #reads kalic0re.firework.tmp 0
execute store result score #reads kalic0re.firework.tmp run \
    function kalic0re:firework/editor/particle_reads with storage kalic0re:firework input.texts
execute if score #reads kalic0re.firework.tmp matches 0 run return fail
data modify storage kalic0re:firework edited.sparkParticle set from storage kalic0re:firework input.texts.sparkParticle
return 1
