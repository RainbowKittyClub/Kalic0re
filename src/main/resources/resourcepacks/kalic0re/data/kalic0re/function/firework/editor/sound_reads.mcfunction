# For read_sound: 1 when $(sound) reads as a sound, never played; alone, so one that doesn't fails only this.
$execute if entity @s[tag=kalic0re.firework.neverTagged] run playsound $(sound) master @s
return 1
