# Splits #ticks kalic0re.parkour.tmp into the digits of mm:ss.d: #tensOfMinutes, #onesOfMinutes,
# #tensOfSeconds, #onesOfSeconds and #tenthsOfSecond. Each holds a running total until its last step.
scoreboard players operation #tenthsOfSecond kalic0re.parkour.tmp = #ticks kalic0re.parkour.tmp
scoreboard players operation #tenthsOfSecond kalic0re.parkour.tmp /= #2 kalic0re.parkour.tmp
scoreboard players operation #onesOfSeconds kalic0re.parkour.tmp = #tenthsOfSecond kalic0re.parkour.tmp
scoreboard players operation #onesOfSeconds kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
scoreboard players operation #tenthsOfSecond kalic0re.parkour.tmp %= #10 kalic0re.parkour.tmp
scoreboard players operation #onesOfMinutes kalic0re.parkour.tmp = #onesOfSeconds kalic0re.parkour.tmp
scoreboard players operation #onesOfMinutes kalic0re.parkour.tmp /= #60 kalic0re.parkour.tmp
scoreboard players operation #onesOfSeconds kalic0re.parkour.tmp %= #60 kalic0re.parkour.tmp
scoreboard players operation #tensOfSeconds kalic0re.parkour.tmp = #onesOfSeconds kalic0re.parkour.tmp
scoreboard players operation #tensOfSeconds kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
scoreboard players operation #onesOfSeconds kalic0re.parkour.tmp %= #10 kalic0re.parkour.tmp
scoreboard players operation #tensOfMinutes kalic0re.parkour.tmp = #onesOfMinutes kalic0re.parkour.tmp
scoreboard players operation #tensOfMinutes kalic0re.parkour.tmp /= #10 kalic0re.parkour.tmp
scoreboard players operation #onesOfMinutes kalic0re.parkour.tmp %= #10 kalic0re.parkour.tmp
