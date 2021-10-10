///scrInitializeAudio()
//load music && sfx
//now you only need to put the music /sfx files in the \music|\sfx folder to load automatically

var path,f,withoutExt;

//load music
path = working_directory + "\Music\";

for (f = file_find_first(path+"*.*",false); f != ""; f = file_find_next())
{
    withoutExt = string_delete(f,string_pos(".",f),string_length(filename_ext(path+f)));
    execute_string("globalvar " + withoutExt + "; " + withoutExt + " = FMODSoundAdd('" + path + f + "', 0, 1);");
}

//load sfx
path = working_directory + "\SFX\";

for (f = file_find_first(path+"*.*",false); f != ""; f = file_find_next())
{
    withoutExt = string_delete(f,string_pos(".",f),string_length(filename_ext(path+f)));
    execute_string("globalvar " + withoutExt + "; " + withoutExt + " = FMODSoundAdd('" + path + f + "', 0, 0);");
}

file_find_close();
