/*
Turns on/off the naggle (TCP_NODELAY) algorithm.
argument0 = socket id to affect
argument1 = true/false (true means NODELAY, false means DELAY)
*/
return external_call(global._SokT, argument0, argument1);
