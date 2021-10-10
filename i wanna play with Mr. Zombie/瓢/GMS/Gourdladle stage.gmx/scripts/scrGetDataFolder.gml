///scrGetDataFolder()
///get the data folder path and check exists

if (!directory_exists(global.dataFolder))
    directory_create(global.dataFolder); //create the data folder if it doesn't exist

return global.dataFolder;
 

