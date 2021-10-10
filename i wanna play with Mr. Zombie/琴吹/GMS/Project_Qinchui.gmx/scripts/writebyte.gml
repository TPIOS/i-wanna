/*
Write 1 byte. The value can be between 0 and 255
Argument0 : value;
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufA, argument0, argument1);