/*
Initializes the dll. 
Argument0: The dll filename. Set this argument to 0 if you want to use "39dll.dll"
Argument1: Set to true to load socket functions
Argument2: Set to true to load utilities.
*/
global._39dll = argument0;
if(is_real(argument0))global._39dll= "39dll.dll";
external_call(external_define(global._39dll, "dllInit", dll_cdecl, ty_real, 0));
//Buffer
global._BufA = external_define(global._39dll, "writebyte", dll_cdecl, ty_real, 2, ty_real,ty_real);
global._BufB = external_define(global._39dll, "writestring", dll_cdecl, ty_real, 2, ty_string, ty_real);
global._BufC = external_define(global._39dll, "writeshort", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufD = external_define(global._39dll, "writeint", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufE = external_define(global._39dll, "writefloat", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufF = external_define(global._39dll, "writedouble", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufG = external_define(global._39dll, "readbyte", dll_cdecl, ty_real, 1, ty_real);
global._BufH = external_define(global._39dll, "readstring", dll_cdecl, ty_string, 1, ty_real);
global._BufI = external_define(global._39dll, "readshort", dll_cdecl, ty_real, 1, ty_real);
global._BufJ = external_define(global._39dll, "readint", dll_cdecl, ty_real, 1, ty_real);
global._BufK = external_define(global._39dll, "readfloat", dll_cdecl, ty_real, 1, ty_real);
global._BufL = external_define(global._39dll, "readdouble", dll_cdecl, ty_real, 1, ty_real);
global._BufM = external_define(global._39dll, "clearbuffer", dll_cdecl, ty_real, 1, ty_real);
global._BufN = external_define(global._39dll, "setpos", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufO = external_define(global._39dll, "getpos", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufQ = external_define(global._39dll, "buffsize", dll_cdecl, ty_real, 1, ty_real);
global._BufR = external_define(global._39dll, "writechars", dll_cdecl, ty_real, 2, ty_string, ty_real);
global._BufS = external_define(global._39dll, "readchars", dll_cdecl, ty_string, 2, ty_real, ty_real);
global._BufU = external_define(global._39dll, "createbuffer", dll_cdecl, ty_real, 0);
global._BufX = external_define(global._39dll, "freebuffer", dll_cdecl, ty_real, 1, ty_real);
global._BufY = external_define(global._39dll, "writeushort", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufZ = external_define(global._39dll, "writeuint", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufAA = external_define(global._39dll, "readushort", dll_cdecl, ty_real, 1, ty_real);
global._BufAB = external_define(global._39dll, "readuint", dll_cdecl, ty_real, 1, ty_real);
global._BufAC = external_define(global._39dll, "copybuffer", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._BufAD = external_define(global._39dll, "copybuffer2", dll_cdecl, ty_real,4 , ty_real, ty_real, ty_real, ty_real);
global._BufAE = external_define(global._39dll, "bytesleft", dll_cdecl, ty_real, 1, ty_real);
global._BufAF = external_define(global._39dll, "bufferexists", dll_cdecl, ty_real, 1, ty_real);
global._BufAG = external_define(global._39dll, "readsep", dll_cdecl, ty_string, 2, ty_string, ty_real);
if(argument1)
{
//Sockets
global._SokA = external_define(global._39dll, "tcpconnect", dll_cdecl, ty_real, 3, ty_string, ty_real, ty_real);
global._SokB = external_define(global._39dll, "tcplisten", dll_cdecl, ty_real, 3, ty_real, ty_real, ty_real);
global._SokC = external_define(global._39dll, "tcpaccept", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._SokD = external_define(global._39dll, "sendmessage", dll_cdecl, ty_real, 4, ty_real, ty_string, ty_real, ty_real);
global._SokE = external_define(global._39dll, "receivemessage", dll_cdecl, ty_real, 3, ty_real, ty_real, ty_real);
global._SokF = external_define(global._39dll, "setsync", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._SokG = external_define(global._39dll, "setformat", dll_cdecl, ty_real, 3, ty_real, ty_real, ty_string);
global._SokH = external_define(global._39dll, "udpconnect", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._SokI = external_define(global._39dll, "hostip", dll_cdecl, ty_string, 1, ty_string);
global._SokJ = external_define(global._39dll, "lastinIP", dll_cdecl, ty_string, 0);
global._SokK = external_define(global._39dll, "closesock", dll_cdecl, ty_real, 1, ty_real);
global._SokL = external_define(global._39dll, "tcpip", dll_cdecl, ty_string, 1, ty_real);
global._SokM = external_define(global._39dll, "socklasterror", dll_cdecl, ty_real, 0);
global._SokN = external_define(global._39dll, "myhost", dll_cdecl, ty_string, 0);
global._SokO = external_define(global._39dll, "compareip", dll_cdecl, ty_real, 2, ty_string, ty_string);
global._SokP = external_define(global._39dll, "sockexit", dll_cdecl, ty_real, 0);
global._SokQ = external_define(global._39dll, "sockstart", dll_cdecl, ty_real, 0);
global._SokR = external_define(global._39dll, "getmacaddress", dll_cdecl, ty_string, 0);
global._SokS = external_define(global._39dll, "peekmessage", dll_cdecl, ty_real, 3, ty_real, ty_real, ty_real);
global._SokT = external_define(global._39dll, "setnagle", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._SokU = external_define(global._39dll, "tcpconnected", dll_cdecl, ty_real, 1, ty_real);
global._SokX = external_define(global._39dll, "getsocketid", dll_cdecl, ty_real, 1, ty_real);
global._SokY = external_define(global._39dll, "lastinPort", dll_cdecl, ty_real, 0);
}
if(argument2)
{
//File functions

global._FilA = external_define(global._39dll, "fileopen", dll_cdecl, ty_real, 2, ty_string, ty_real);
global._FilB = external_define(global._39dll, "fileclose", dll_cdecl, ty_real, 1, ty_real);
global._FilC = external_define(global._39dll, "filewrite", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._FilD = external_define(global._39dll, "fileread", dll_cdecl, ty_real, 3, ty_real, ty_real, ty_real);
global._FilE = external_define(global._39dll, "filepos", dll_cdecl, ty_real, 1, ty_real);
global._FilF = external_define(global._39dll, "filesetpos", dll_cdecl, ty_real, 2, ty_real, ty_real);
global._FilG = external_define(global._39dll, "filesize", dll_cdecl, ty_real, 1, ty_real);

global._UtilA = external_define(global._39dll, "md5string", dll_cdecl, ty_string, 1, ty_string);
global._UtilB = external_define(global._39dll, "md5buffer", dll_cdecl, ty_string, 1, ty_real);
global._UtilC = external_define(global._39dll, "adler32", dll_cdecl, ty_real, 1, ty_real);
global._UtilD = external_define(global._39dll, "bufferencrypt", dll_cdecl, ty_real, 2, ty_string, ty_real);
global._UtilE = external_define(global._39dll, "iptouint", dll_cdecl, ty_real, 1, ty_string);
global._UtilF = external_define(global._39dll, "uinttoip", dll_cdecl, ty_string, 1, ty_real);
global._UtilG = external_define(global._39dll, "netconnected", dll_cdecl, ty_real, 0);

}
