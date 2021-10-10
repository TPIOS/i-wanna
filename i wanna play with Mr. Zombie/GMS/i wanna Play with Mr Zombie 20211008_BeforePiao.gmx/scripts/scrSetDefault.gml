///scrSetDefault(var{string},default,original{optional})
/*
    This function is used to initialize a variable in create event
    If the value of the variable is "original(default is 0)", it is set to the default value (useful for built-in variables)
    return: need default?
*/

var varname,def,ori,gbname;
varname = argument0;
def = argument1;
//ori = argument2;

if (is_string(def))
    ori = string(def);

if (is_real(varname))
{
    show_error("error: variable name is not a string
    > scrSetDefault()",true);
}

if (string_pos("global.",varname) != 0) //is a global variable
{
    gbname = string_delete(varname,1,7);
}
else
    gbname = false;
 
if (is_string(gbname)) //set global variable
{
    if (!variable_global_exists(gbname))
    {
        variable_global_set(gbname,def);
        return 1;
    }
    else
    {
        if (variable_global_get(gbname) == ori)
        {
            variable_global_set(gbname,def);
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
    if (!variable_instance_exists(id, varname))
    {
        variable_instance_set(id, varname,def);
        return 1;
    }
    else
    {
        if (variable_instance_get(id, varname) == ori)
        {
            variable_instance_set(id, varname,def);
            return 1;
        }
        else
        {
            return 0;
        }
    }   
}
