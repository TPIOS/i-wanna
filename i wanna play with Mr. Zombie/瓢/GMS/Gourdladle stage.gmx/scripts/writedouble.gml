/*
Same as writefloat but the number can be much larger. It can write any number GM supports.
Warning: It uses 8 bytes!!
Argument0: value.
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufF, argument0, argument1);
