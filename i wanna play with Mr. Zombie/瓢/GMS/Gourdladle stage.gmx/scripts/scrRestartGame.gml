///restarts the game

if (surface_exists(global.pauseSurf))
    surface_free(global.pauseSurf);  //free pause surface in case the game is currently paused

//stop death music and sound
FMODAllStop();

//destroy objects
with (all)
{
    if (id != other.id)
        instance_destroy();
}
effect_clear();

//reload globals
scrInitializeGlobals();
scrLoadConfig();

//go to title menu
room_goto(room_first);



