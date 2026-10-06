# For only: 1 when $(char) is one of class $(class)'s. A " or \ can't be written into the path, so this fails to load
# and returns nothing, which counts as not.
$execute if data storage kalic0re:text classes.$(class)."$(char)" run return 1
return fail
