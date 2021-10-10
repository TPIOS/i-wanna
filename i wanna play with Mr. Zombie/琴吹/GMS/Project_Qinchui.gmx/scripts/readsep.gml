/*
Finds a string in a buffer and returns the data before the string and increments the
buffers readpos.
Argument0: Seperator string.
[Argument1]: The buffer id to read from. Leave out this argument or set
it to 0 to use the default buffer.

Example:
writechars("Hello:World:");
show_message(readsep(":")); //will display "Hello"
show_message(readsep(":")); //will display "World"
*/
return external_call(global._BufAG, argument0, argument1);