# A firework drawn with particles (firework/launch), its Create Firework dialog (firework/editor/open), its defaults
# and patterns. Each marker holds the game time of each of its stages (marker_setup), shared by every world.
scoreboard objectives add kalic0re.firework.burstGameTime dummy
scoreboard objectives add kalic0re.firework.shellGameTime dummy
scoreboard objectives add kalic0re.firework.twinkleGameTime dummy
scoreboard objectives add kalic0re.firework.endGameTime dummy
scoreboard objectives add kalic0re.firework.tmp dummy
scoreboard players set #10 kalic0re.firework.tmp 10
scoreboard players set #20000 kalic0re.firework.tmp 20000
function kalic0re:firework/shapes
# Any setting a firework leaves out. Colours are RGB; an empty blast sound is vanilla's for the pattern.
data modify storage kalic0re:firework defaults set value {pattern:"large_ball",colors:[I;16766720,16777045], \
    fadeColors:[I;16777215],radius:4.0d,position:"~ ~10 ~",riseTicks:16,riseColor:16777215,burstTicks:6,dustScale:2.0f, \
    hasTrail:1b,sparkSpeed:0.35d,sparkParticle:"minecraft:firework",hasTwinkle:1b,twinkleDelayTicks:12,twinkleSparks:40, \
    twinkleSpread:2.0d,playLaunchSound:1b,launchSound:"minecraft:entity.firework_rocket.launch",playBlastSound:1b, \
    blastSound:"",playTwinkleSound:1b,twinkleSound:"minecraft:entity.firework_rocket.twinkle",soundSource:"ambient", \
    soundVolume:3.0f,particleMode:"force"}
# Each text box's label, for saying which one doesn't read (editor/refuse).
data modify storage kalic0re:firework labels set value { \
    position:{translate:"kalic0re.create_firework.position.label"}, \
    colors:{translate:"kalic0re.create_firework.color.label"}, \
    fadeColors:{translate:"kalic0re.create_firework.fade_color.label"}, \
    riseColor:{translate:"kalic0re.create_firework.rise_color.label"}, \
    sparkParticle:{translate:"kalic0re.create_firework.spark_particle.label"}, \
    launchSound:{translate:"kalic0re.create_firework.launch_sound_id.label"}, \
    blastSound:{translate:"kalic0re.create_firework.blast_sound_id.label"}, \
    twinkleSound:{translate:"kalic0re.create_firework.twinkle_sound_id.label"}}
# After a /reload, fireworks under way carry on. At a world's start their markers aren't loaded yet; the next launch's
# advance finds them, and marker_tick drops any whose stages have passed.
execute if entity @e[type=minecraft:marker,tag=kalic0re.firework] run schedule function kalic0re:firework/advance 1t replace
