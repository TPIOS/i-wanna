/*
When sending messages over a tcp socket the messages need to be seperated.
The dll provides two methods of seperation. 

Method 0: Binary mode. Setting this mode makes every tcp message have a 2 byte
integer attached at the front which represents the amount of data in that 
message. This is the default method and is reccomended in online games.

Method 1: Text mode. Setting this mode makes every tcp message have a delimeter
added at the end which is used to identify the end of the message. This is
reccomended for text based protocols like irc.

Method 2: No formatting. Only the data in the internal buffer is sent and
no seperation methods are included.

Argument0 = socket id
Argument1 = method number
[Argument2] = seperation string when using method 1.

MAKE SURE THE CORRECT FORMAT MODE IS SET WHEN RECIEVING A MESSAGE THAT IS IN A
SPECIFIC FORMAT.
returns argument0
*/
return external_call(global._SokG, argument0, argument1, argument2);