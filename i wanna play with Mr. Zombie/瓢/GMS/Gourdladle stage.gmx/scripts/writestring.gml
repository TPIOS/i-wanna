/*
Write a null terminated string to the buffer
Warning: DO NOT set argument1 to true like you would in the old method. Doing this
will make it try and write to a buffer with the ID of 1 (1 = true)

Argument0 = String to write.
[Argument1]: The buffer id to write too. Leave out this argument or set
it to 0 to use the default buffer.
*/
return external_call(global._BufB, argument0, argument1);
