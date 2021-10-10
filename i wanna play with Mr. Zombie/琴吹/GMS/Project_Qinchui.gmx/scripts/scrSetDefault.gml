///scrSetDefault(variableName,default,original{optional})
///this function is used to initialize a variable in create event
///if the value of the variable is "originalginal(default is 0)", it is set to the default value (useful for built-in variables)
///return: need default?


var localName,def,original,globalName;
localName = argument0;
def = argument1;
original = argument2;

if (is_string(def))
    original = string(def);

if (is_real(localName))
{
    show_error("error: variable name is not a string#> scrSetDefault()",true);
}

if (string_pos("global.",localName) != 0) //is a global variable
{
    globalName = string_delete(localName,1,7);
}
else
    globalName = false;
 
if (is_string(globalName)) //set global variable
{
    if (!variable_global_exists(globalName))
    {
        variable_global_set(globalName,def);
        return 1;
    }
    else
    {
        if (variable_global_get(globalName) == original)
        {
            variable_global_set(globalName,def);
            return 1;
        }
        else
        {
            return 0;
        }
    }
}

else //set instance(local) variable
{
    if (!variable_local_exists(localName))
    {
        variable_local_set(localName,def);
        return 1;
    }
    else
    {
        if (variable_local_get(localName) == original)
        {
            variable_local_set(localName,def);
            return 1;
        }
        else
        {
            return 0;
        }
    }   
}
