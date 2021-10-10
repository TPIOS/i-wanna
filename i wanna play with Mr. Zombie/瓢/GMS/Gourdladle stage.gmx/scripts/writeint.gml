/*
Writes a 4 byte integer to the internal buffer. The value can be between
-2147483648 and +2147483647
Argument0: value
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufD, argument0, argument1);
