# As a runner, each tick: "| Current Time: 00:00.0 | Best: 00:00.0 |" above the hotbar. The list
# starts with an empty element because the rest inherit the first element's style.
execute if score @s kalic0re.parkour.courseBestTicks matches 0.. run function kalic0re:parkour/timer/split_best
scoreboard players operation #ticks kalic0re.parkour.tmp = @s kalic0re.parkour.runTicks
function kalic0re:parkour/timer/split
execute if score @s kalic0re.parkour.courseBestTicks matches 0.. run title @s actionbar [{text:""}, \
    {text:"| ",color:"dark_gray"},{translate:"kalic0re.parkour.timer.current",fallback:"Current Time:",color:"yellow",bold:true},{text:" "}, \
    {score:{name:"#tensOfMinutes",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfMinutes",objective:"kalic0re.parkour.tmp"}}, \
    {text:":"},{score:{name:"#tensOfSeconds",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfSeconds",objective:"kalic0re.parkour.tmp"}}, \
    {text:"."},{score:{name:"#tenthsOfSecond",objective:"kalic0re.parkour.tmp"}}, \
    {text:" | ",color:"dark_gray"},{translate:"kalic0re.parkour.timer.best",fallback:"Best:",color:"yellow",bold:true},{text:" "}, \
    {score:{name:"#bestTensOfMinutes",objective:"kalic0re.parkour.tmp"}},{score:{name:"#bestOnesOfMinutes",objective:"kalic0re.parkour.tmp"}}, \
    {text:":"},{score:{name:"#bestTensOfSeconds",objective:"kalic0re.parkour.tmp"}},{score:{name:"#bestOnesOfSeconds",objective:"kalic0re.parkour.tmp"}}, \
    {text:"."},{score:{name:"#bestTenthsOfSecond",objective:"kalic0re.parkour.tmp"}}, \
    {text:" |",color:"dark_gray"}]
execute unless score @s kalic0re.parkour.courseBestTicks matches 0.. run title @s actionbar [{text:""}, \
    {text:"| ",color:"dark_gray"},{translate:"kalic0re.parkour.timer.current",fallback:"Current Time:",color:"yellow",bold:true},{text:" "}, \
    {score:{name:"#tensOfMinutes",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfMinutes",objective:"kalic0re.parkour.tmp"}}, \
    {text:":"},{score:{name:"#tensOfSeconds",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfSeconds",objective:"kalic0re.parkour.tmp"}}, \
    {text:"."},{score:{name:"#tenthsOfSecond",objective:"kalic0re.parkour.tmp"}}, \
    {text:" | ",color:"dark_gray"},{translate:"kalic0re.parkour.timer.best",fallback:"Best:",color:"yellow",bold:true},{text:" "},{translate:"kalic0re.parkour.timer.no_best",fallback:"--:--.-"}, \
    {text:" |",color:"dark_gray"}]
