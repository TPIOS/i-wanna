///scrCheckFileExists(filename)
///check if the file exists
///if it does not exist, display the error message

if (!file_exists(argument0))
{
    show_error('Error: file does not exist "' + filename_name(argument0) + '"', true);
    return false;
}
return true;
