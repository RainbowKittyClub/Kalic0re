# As a runner whose own inventory is saved, at their feet. A clear of 0 only counts, and it sees the cursor
# and the crafting grid too; a stack is ejected only on the tick one turns up.
execute store result score #runnerStrayItems kalic0re.parkour.tmp run clear @s *[!custom_data~{kalic0re:{parkour:{}}}] 0
execute if score #runnerStrayItems kalic0re.parkour.tmp matches 1.. run function kalic0re:parkour/player/eject_strays
