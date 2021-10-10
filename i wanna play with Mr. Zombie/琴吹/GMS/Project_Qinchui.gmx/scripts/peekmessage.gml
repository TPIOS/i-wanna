/*
Copies any data recieved on a specified socket to the internal buffer but
does not remove it from the recv buffer.
Argument0 = TCP or UDP socket to recieve from
[Argument1] = Bytes to receive. Optional (otherwise receives as much as
possible)
[Argument2]: The buffer id to receive to. Leave out this argument or set
it to 0 to use the default buffer.
Returns amount of bytes recieved.

NOTE:
If you dont want to save the data to the defualt buffer, than do it like this:
peekmessage(sockid, 0, bufferid);
or peekmessage(sockid, bytestoreceive, bufferid);
*/
return external_call(global._SokS, argument0, argument1, argument2);