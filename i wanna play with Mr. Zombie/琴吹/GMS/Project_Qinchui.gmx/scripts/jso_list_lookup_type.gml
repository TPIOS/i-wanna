///jso_list_lookup_type(list, key1, [key2], [...])
{
    /**
    jso_list_lookup_type(list, key1, key2, ...): Recursively look up keys/indices in the top-level list <list>, return the type of value that exists there.
    */
    
    //Catch empty calls
    if (argument_count < 2) {
        show_error("Expected at least 2 arguments, got " + string(argument_count) + ".", true);
    }
    
    //Build list of keys/indices
    var i, key_list;
    key_list = ds_list_create();
    for (i=1; i<argument_count; i+=1) {
        ds_list_add(key_list, argument[i]);
    }
    
    //Call lookup kernel and cleanup
    var result;
    result = _jso_lookup_kernel(argument[0], jso_type_list, 2, key_list);
    ds_list_destroy(key_list);
    
    //Done
    return result;
}
