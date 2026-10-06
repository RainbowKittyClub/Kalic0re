# For open: the text boxes' starting text, copied exactly but for the rocket colour, a number written out.
$data modify storage kalic0re:dialog args.rise_color_text set value "$(riseColor)"
data modify storage kalic0re:dialog args.position_text set from storage kalic0re:firework tmp.editing.position
data modify storage kalic0re:dialog args.spark_particle_text set from storage kalic0re:firework tmp.editing.sparkParticle
data modify storage kalic0re:dialog args.launch_sound_text set from storage kalic0re:firework tmp.editing.launchSound
data modify storage kalic0re:dialog args.blast_sound_text set from storage kalic0re:firework tmp.editing.blastSound
data modify storage kalic0re:dialog args.twinkle_sound_text set from storage kalic0re:firework tmp.editing.twinkleSound
