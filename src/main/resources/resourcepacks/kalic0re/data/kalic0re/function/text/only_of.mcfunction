# 1 when storage kalic0re:text check holds only characters of class $(class) (text/load), so it can go into a command
# and do nothing else there; check is then cleared. The only_<class> functions run this for `execute if function`.
scoreboard players set #textOnly kalic0re.text.tmp 0
$execute store result score #textOnly kalic0re.text.tmp run function kalic0re:text/only {class:"$(class)"}
data remove storage kalic0re:text check
data remove storage kalic0re:text step
return run scoreboard players get #textOnly kalic0re.text.tmp
