# For marker_tick, at the burst point: the twinkle's sound, and $(sparkParticle) sparkles scattered through the shell.
$execute if data entity @s data{playTwinkleSound:1b} run playsound $(twinkleSound) $(soundSource) @a ~ ~ ~ $(soundVolume)
$execute unless data entity @s data{twinkleSparks:0} run \
    particle $(sparkParticle) ~ ~ ~ $(twinkleSpread) $(twinkleSpread) $(twinkleSpread) 0 $(twinkleSparks) $(particleMode)
