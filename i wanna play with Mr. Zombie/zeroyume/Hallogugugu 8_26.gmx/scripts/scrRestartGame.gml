///restarts the game

//hallo only, reset time
global.dayNight = 8 * 120;




if (surface_exists(global.pauseSurf))
    surface_free(global.pauseSurf);  //free pause surface in case the game is currently paused

game_restart();
