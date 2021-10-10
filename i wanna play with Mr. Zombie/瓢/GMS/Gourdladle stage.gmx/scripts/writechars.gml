/*
Writes a sequence of characters to the buffer.
Argument0: String to write.
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufR, argument0, argument1);
