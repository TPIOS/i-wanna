/*
Writes a 4 byte UNSIGNED integer to the internal buffer. The value can be between
0 and +4294967296
Argument0: value
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufD, argument0, argument1);