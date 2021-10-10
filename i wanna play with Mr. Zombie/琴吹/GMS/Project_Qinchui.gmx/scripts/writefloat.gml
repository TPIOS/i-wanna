/*
Writes a 4 byte real number (number that can contain a decimal place).
Argument0: Value;
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufE, argument0, argument1);