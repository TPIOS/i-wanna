/*
  Writes the data in a buffer into the open file.
  argument0 = file id
  [Argument1]: The buffer id to read from. Leave out this argument or set
it to 0 to use the default buffer.
*/

return external_call(global._FilC, argument0, argument1);