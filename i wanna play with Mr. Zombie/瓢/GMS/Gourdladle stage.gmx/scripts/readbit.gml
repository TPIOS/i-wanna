/*
  Reads the bit at a certain position in a number
  argument0 = number
  argument1 = bit position (0-7)
  returns the bit value (true/false)
*/
return (argument0 & power(2, argument1))>0;
