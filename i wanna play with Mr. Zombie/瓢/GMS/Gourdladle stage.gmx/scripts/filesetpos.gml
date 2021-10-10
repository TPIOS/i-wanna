/*
  Sets the read/write pos in the open file. 
  argument0 = file id
  Argument1 = position to set to.
*/

return external_call(global._FilF, argument0, argument1);
