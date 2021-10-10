/*
This script compares an ip to a masked ip. Will return true if they match.
e.g 127.0.0.1 matches 127.0.0.*
e.g 127.1.0.1 does not match 127.0.0.*
Useful when banning hackers who have a dynamic ip.
Argument0 = Ip
Argument1 = Masked ip.
*/
return external_call(global._SokO, argument1, argument0);
