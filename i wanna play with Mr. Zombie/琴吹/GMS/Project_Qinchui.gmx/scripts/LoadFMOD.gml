//Call this when the game starts to create the dll interface
//returns nothing... GM will stop if the dll could not be linked

//NOTE: You must have GMFMODSimple.dll and fmodex.dll in your game directory

//Example call
//When the game starts
//LoadFMOD();
var dll;
dll = 'GMFMODSimple.dll'
if(is_string(argument0)) {
    if(string_length(argument0) > 0) {
        dll = argument0
    }
}
global.__FMOD_DLL_PATH = dll
if !file_exists(dll) show_message("File not found: " + dll);

var WTF;
WTF = false;
//export double FMODfree(void)
global.dll_FMODfree=external_define(dll,"FMODfree",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODfree")
//export double FMODinit(double maxsounds, supportwebmusic)
global.dll_FMODinit=external_define(dll,"FMODinit",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODinit")
//export double FMODSoundSetEffects(double sound, double effects)
global.dll_FMODSoundSetEffects=external_define(dll,"FMODSoundSetEffects",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSetEffects")
//export double FMODSoundSetGroup(double sound, double group)
global.dll_FMODSoundSetGroup=external_define(dll,"FMODSoundSetGroup",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSetGroup")
//export double FMODGroupSetVolume(double group, double volume)
global.dll_FMODGroupSetVolume=external_define(dll,"FMODGroupSetVolume",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupSetVolume")
//export double FMODSoundSetMaxVolume(double sound, double volume)
global.dll_FMODSoundSetMaxVolume=external_define(dll,"FMODSoundSetMaxVolume",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSetMaxVolume")
//export double FMODSoundLoop(double sound,paused)
global.dll_FMODSoundLoop=external_define(dll,"FMODSoundLoop",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundLoop")
//export double FMODSoundPlay(double sound,paused)
global.dll_FMODSoundPlay=external_define(dll,"FMODSoundPlay",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundPlay")
//export double FMODSoundLoop3d(double sound, double x, double y, double z,paused)
global.dll_FMODSoundLoop3d=external_define(dll,"FMODSoundLoop3d",dll_stdcall,ty_real,5,ty_real,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundLoop3d")
//export double FMODSoundPlay3d(double sound, double x, double y, double z,paused)
global.dll_FMODSoundPlay3d=external_define(dll,"FMODSoundPlay3d",dll_stdcall,ty_real,5,ty_real,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundPlay3d")
//export double FMODInstanceSet3dPosition(double channel,double x,double y,double z)
global.dll_FMODInstanceSet3dPosition=external_define(dll,"FMODInstanceSet3dPosition",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3dPosition")

//export double FMODSoundAdd(LPCSTR soundfile, double threed, double streamed)
global.dll_FMODSoundAdd=external_define(dll,"FMODSoundAdd",dll_stdcall,ty_real,3,ty_string,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundAdd")


//export double FMODMasterSetVolume(double volume)
global.dll_FMODMasterSetVolume=external_define(dll,"FMODMasterSetVolume",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODMasterSetVolume")
//export double FMODListenerSetNumber(double number)
global.dll_FMODListenerSetNumber=external_define(dll,"FMODListenerSetNumber",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODListenerSetNumber")
//export double FMODListenerSet3dPosition(double number, double x, double y, double z)
global.dll_FMODListenerSet3dPosition=external_define(dll,"FMODListenerSet3dPosition",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODListenerSet3dPosition")
//export double FMODSetWorldScale(double scale)
global.dll_FMODSetWorldScale=external_define(dll,"FMODSetWorldScale",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSetWorldScale")
//export double FMODSoundSet3dMinMaxDistance(double sound, double Min, double Max)
global.dll_FMODSoundSet3dMinMaxDistance=external_define(dll,"FMODSoundSet3dMinMaxDistance",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSet3dMinMaxDistance")
//export double FMODUpdate()
global.dll_FMODUpdate=external_define(dll,"FMODUpdate",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODUpdate")
//export double FMODSoundFree(double sound)
global.dll_FMODSoundFree=external_define(dll,"FMODSoundFree",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundFree")
//export double FMODGroupStop(double group)
global.dll_FMODGroupStop=external_define(dll,"FMODGroupStop",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODGroupStop")
//export double FMODAllStop()
global.dll_FMODAllStop=external_define(dll,"FMODAllStop",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODAllStop")
//export double FMODInstanceStop(double instance)
global.dll_FMODInstanceStop=external_define(dll,"FMODInstanceStop",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceStop")
//export double FMODInstanceIsPlaying(double instance)
global.dll_FMODInstanceIsPlaying=external_define(dll,"FMODInstanceIsPlaying",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceIsPlaying")
//export double FMODGroupSetMuted(double group, double mute)
global.dll_FMODGroupSetMuted=external_define(dll,"FMODGroupSetMuted",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupSetMuted")
//export double FMODInstanceSetMuted(double instance, double mute)
global.dll_FMODInstanceSetMuted=external_define(dll,"FMODInstanceSetMuted",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetMuted")
//export double FMODInstanceSetVolume(double instance, double volume)
global.dll_FMODInstanceSetVolume=external_define(dll,"FMODInstanceSetVolume",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetVolume")
//export double FMODGroupSetPaused(double group, double mute)
global.dll_FMODGroupSetPaused=external_define(dll,"FMODGroupSetPaused",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupSetPaused")
//export double FMODInstanceSetPaused(double instance, double mute)
global.dll_FMODInstanceSetPaused=external_define(dll,"FMODInstanceSetPaused",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetPaused")
//export double FMODGetLastError(void)
global.dll_FMODGetLastError=external_define(dll,"FMODGetLastError",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODGetLastError")
//export double FMODListenerHearsDistanceOnly(double number, double t)
global.dll_FMODListenerHearsDistanceOnly=external_define(dll,"FMODListenerHearsDistanceOnly",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODListenerHearsDistanceOnly")
//export double FMODSetDopplerFPS(double fps)
global.dll_FMODSetDopplerFPS=external_define(dll,"FMODSetDopplerFPS",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSetDopplerFPS")
//export double FMODListenerSet3dPositionEx(double number, double x, double y, double z, double fx, double fy, double fz, double ux, double uy, double uz)
global.dll_FMODListenerSet3dPositionEx=external_define(dll,"FMODListenerSet3dPositionEx",dll_stdcall,ty_real,10,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODListenerSet3dPositionEx")
//export double FMODSoundSet3dDopplerMax(double sound, double max)
global.dll_FMODSoundSet3dDopplerMax=external_define(dll,"FMODSoundSet3dDopplerMax",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSet3dDopplerMax")
//export double FMODInstanceSetFrequency(double instance, double freq)
global.dll_FMODInstanceSetFrequency=external_define(dll,"FMODInstanceSetFrequency",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetFrequency")
//export double FMODInstanceGetFrequency(double instance)
global.dll_FMODInstanceGetFrequency=external_define(dll,"FMODInstanceGetFrequency",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetFrequency")
//export double FMODSoundSet3dCone(double sound, double insideconeangle, double outsideconeangle, double outsidevolume)
global.dll_FMODSoundSet3dCone=external_define(dll,"FMODSoundSet3dCone",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSet3dCone")
//export double FMODInstanceSet3dConeOrientation(double instance, double x, double y, double z)
global.dll_FMODInstanceSet3dConeOrientation=external_define(dll,"FMODInstanceSet3dConeOrientation",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3dConeOrientation")
//export double FMODGroupSetFrequency(double group, double freq)
global.dll_FMODGroupSetFrequency=external_define(dll,"FMODGroupSetFrequency",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupSetFrequency")

//export double FMODGroupGetVolume(double group)
global.dll_FMODGroupGetVolume=external_define(dll,"FMODGroupGetVolume",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODGroupGetVolume")
//export double FMODGroupGetPitch(double group)
global.dll_FMODGroupGetPitch=external_define(dll,"FMODGroupGetPitch",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODGroupGetPitch")
//export double FMODGroupGetPaused(double group)
global.dll_FMODGroupGetPaused=external_define(dll,"FMODGroupGetPaused",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODGroupGetPaused")
//export double FMODGroupGetMuted(double group)
global.dll_FMODGroupGetMuted=external_define(dll,"FMODGroupGetMuted",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODGroupGetMuted")
//export double FMODGroupSetPitch(double group,double pitch)
global.dll_FMODGroupSetPitch=external_define(dll,"FMODGroupSetPitch",dll_stdcall,ty_real,2,ty_real, ty_real);
if(WTF) show_message("Defined: FMODGroupSetPitch")
//export double FMODInstanceGetVolume(double instance)
global.dll_FMODInstanceGetVolume=external_define(dll,"FMODInstanceGetVolume",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetVolume")

//export double FMODInstanceGetSound(double instance)
global.dll_FMODInstanceGetSound=external_define(dll,"FMODInstanceGetSound",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetSound")

//export double FMODSoundGetMaxVolume(double sound)
global.dll_FMODSoundGetMaxVolume=external_define(dll,"FMODSoundGetMaxVolume",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundGetMaxVolume")
//export double FMODInstanceGetPaused(double instance)
global.dll_FMODInstanceGetPaused=external_define(dll,"FMODInstanceGetPaused",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetPaused")
//export double FMODInstanceGetMuted(double instance)
global.dll_FMODInstanceGetMuted=external_define(dll,"FMODInstanceGetMuted",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetMuted")

//export double FMODSoundAddEffect(LPCSTR soundfile, double effect, double pos)
global.dll_FMODSoundAddEffect=external_define(dll,"FMODSoundAddEffect",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundAddEffect")

//export double FMODInstanceGetPosition(double instance)
global.dll_FMODInstanceGetPosition=external_define(dll,"FMODInstanceGetPosition",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetPosition")

//export double FMODInstanceSetPosition(double instance, double p)
global.dll_FMODInstanceSetPosition=external_define(dll,"FMODInstanceSetPosition",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetPosition")


//export double FMODGroupSetPan(double group, double pan)
global.dll_FMODGroupSetPan=external_define(dll,"FMODGroupSetPan",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupSetPan")

//export double FMODInstanceSetPan(double instance, double p)
global.dll_FMODInstanceSetPan=external_define(dll,"FMODInstanceSetPan",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetPan")

//export double FMODInstanceGetPan(double instance)
global.dll_FMODInstanceGetPan=external_define(dll,"FMODInstanceGetPan",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetPan")

//export double FMODInstanceSetLoopCount(double instance, double p)
global.dll_FMODInstanceSetLoopCount=external_define(dll,"FMODInstanceSetLoopCount",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetLoopCount")

//export double FMODInstanceGetLoopCount(double instance)
global.dll_FMODInstanceGetLoopCount=external_define(dll,"FMODInstanceGetLoopCount",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetLoopCount")

//export double FMODBlockersInit(double NumBlockers, double xs, double ys, double zs)
global.dll_FMODBlockersInit=external_define(dll,"FMODBlockersInit",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockersInit")

//export double FMODBlockersFree()
global.dll_FMODBlockersFree=external_define(dll,"FMODBlockersFree",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODBlockersFree")

//export double FMODBlockerAdd(double x, double y, double z, double xs, double ys, double zs, double xe, double ye, double ze)
global.dll_FMODBlockerAdd=external_define(dll,"FMODBlockerAdd",dll_stdcall,ty_real,9,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockerAdd")

//export double FMODBlockerSet3dPosition(double blocker, double x, double y, double z)
global.dll_FMODBlockerSet3dPosition=external_define(dll,"FMODBlockerSet3dPosition",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockerSet3dPosition")

//export double FMODBlockerSet3dOrientation(double blocker, double fx, double fy, double fz, double ux, double uy, double uz)
global.dll_FMODBlockerSet3dOrientation=external_define(dll,"FMODBlockerSet3dOrientation",dll_stdcall,ty_real,7,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockerSet3dOrientation")

//export double FMODBlockerSet3dScale(double blocker, double sx, double sy, double sz)
global.dll_FMODBlockerSet3dScale=external_define(dll,"FMODBlockerSet3dScale",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockerSet3dScale")

//export double FMODBlockerSetEnabled(double blocker, double enabled)
global.dll_FMODBlockerSetEnabled=external_define(dll,"FMODBlockerSetEnabled",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockerSetEnabled")

//export double FMODBlockerGetEnabled(double blocker)
global.dll_FMODBlockerGetEnabled=external_define(dll,"FMODBlockerSetEnabled",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODBlockerGetEnabled")

//export double FMODBlockerGetStrength(double blocker)
global.dll_FMODBlockerGetStrength=external_define(dll,"FMODBlockerGetStrength",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODBlockerGetStrength")

//export double FMODBlockerSetStrength(double blocker,double strength)
global.dll_FMODBlockerSetStrength=external_define(dll,"FMODBlockerSetStrength",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODBlockerSetStrength")

//export double FMODGetNumInstances(void)
global.dll_FMODGetNumInstances=external_define(dll,"FMODGetNumInstances",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODGetNumInstances")

//export double FMODSetPassword(LPCSTR password)
global.dll_FMODSetPassword=external_define(dll,"FMODSetPassword",dll_stdcall,ty_real,1,ty_string);
if(WTF) show_message("Defined: FMODSetPassword")

//export double FMODSoundIsStreamed(double sound)
global.dll_FMODSoundIsStreamed=external_define(dll,"FMODSoundIsStreamed",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundIsStreamed")

//export double FMODSoundIs3d(double sound)
global.dll_FMODSoundIs3d=external_define(dll,"FMODSoundIs3d",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundIs3d")

//double FMODSoundInstanciate(double sound)
global.dll_FMODSoundInstanciate=external_define(dll,"FMODSoundInstanciate",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundInstanciate")

//export double FMODSoundGetMaxDist(double sound)
global.dll_FMODSoundGetMaxDist=external_define(dll,"FMODSoundGetMaxDist",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundGetMaxDist")

//export double FMODInstanceSet3dMinMaxDistance(double instance, double Min, double Max)
global.dll_FMODInstanceSet3dMinMaxDistance=external_define(dll,"FMODInstanceSet3dMinMaxDistance",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3dMinMaxDistance")

//export double FMODInstanceSet3dDopplerMax(double instance, double doppler)
global.dll_FMODInstanceSet3dDopplerMax=external_define(dll,"FMODInstanceSet3dDopplerMax",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3dDopplerMax")

//export double FMODInstanceSet3dCone(double instance, double insideconeangle, double outsideconeangle, double outsidevolume)
global.dll_FMODInstanceSet3dCone=external_define(dll,"FMODInstanceSet3dCone",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3dCone")


//export double FMODSoundGetNumChannels(double sound)
global.dll_FMODSoundGetNumChannels=external_define(dll,"FMODSoundGetNumChannels",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundGetNumChannels")

//export double FMODInstanceGetMaxDist(double instance)
global.dll_FMODInstanceGetMaxDist=external_define(dll,"FMODInstanceGetMaxDist",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetMaxDist")

//export double FMODInstanceGetWaveSnapshot(double instance, double channel, double size, LPSTR Buffer)
global.dll_FMODInstanceGetWaveSnapshot=external_define(dll,"FMODInstanceGetWaveSnapshot",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODInstanceGetWaveSnapshot")

//export double FMODInstanceGetSpectrumSnapshot(double instance, double channel, double size, LPSTR Buffer)
global.dll_FMODInstanceGetSpectrumSnapshot=external_define(dll,"FMODInstanceGetSpectrumSnapshot",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODInstanceGetSpectrumSnapshot")

//export double FMODGroupGetWaveSnapshot(double group, double channel, double size, LPSTR Buffer)
global.dll_FMODGroupGetWaveSnapshot=external_define(dll,"FMODGroupGetWaveSnapshot",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODGroupGetWaveSnapshot")

//export double FMODGroupGetSpectrumSnapshot(double group, double channel, double size, LPSTR Buffer)
global.dll_FMODGroupGetSpectrumSnapshot=external_define(dll,"FMODGroupGetSpectrumSnapshot",dll_stdcall,ty_real,4,ty_real,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODGroupGetSpectrumSnapshot")




//export double FMODInstanceGetWaveSnapshot2(double instance, double channel, double size)
global.dll_FMODInstanceGetWaveSnapshot2=external_define(dll,"FMODInstanceGetWaveSnapshot2",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetWaveSnapshot2")

//export double FMODInstanceGetSpectrumSnapshot2(double instance, double channel, double size)
global.dll_FMODInstanceGetSpectrumSnapshot2=external_define(dll,"FMODInstanceGetSpectrumSnapshot2",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetSpectrumSnapshot2")

//export double FMODGroupGetWaveSnapshot2(double group, double channel, double size)
global.dll_FMODGroupGetWaveSnapshot2=external_define(dll,"FMODGroupGetWaveSnapshot2",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupGetWaveSnapshot2")

//export double FMODGroupGetSpectrumSnapshot2(double group, double channel, double size)
global.dll_FMODGroupGetSpectrumSnapshot2=external_define(dll,"FMODGroupGetSpectrumSnapshot2",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupGetSpectrumSnapshot2")

//export double FMODNormalizeSpectrumData(double startpos, double size)
global.dll_FMODNormalizeSpectrumData=external_define(dll,"FMODNormalizeSpectrumData",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODNormalizeSpectrumData")

//export double FMODNormalizeWaveData(double startpos, double size)
global.dll_FMODNormalizeWaveData=external_define(dll,"FMODNormalizeWaveData",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODNormalizeWaveData")

//export double FMODGetSnapshotEntry(double pos)
global.dll_FMODGetSnapshotEntry=external_define(dll,"FMODGetSnapshotEntry",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODGetSnapshotEntry")

//export double FMODGetWaveBuffer(double startpos, double size, LPSTR Buffer)
global.dll_FMODGetWaveBuffer=external_define(dll,"FMODGetWaveBuffer",dll_stdcall,ty_real,3,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODGetWaveBuffer")

//export double FMODGetSpectrumBuffer(double startpos, double size, LPSTR Buffer)
global.dll_FMODGetSpectrumBuffer=external_define(dll,"FMODGetSpectrumBuffer",dll_stdcall,ty_real,3,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODGetSpectrumBuffer")

//extern double FMODEncryptFile(LPCSTR sourcename, LPCSTR destname, LPCSTR password)
global.dll_FMODEncryptFile=external_define(dll,"FMODEncryptFile",dll_stdcall,ty_real,3,ty_string,ty_string,ty_string);
if(WTF) show_message("Defined: FMODEncryptFile")

//export double FMODSoundGetLength(double sound)
global.dll_FMODSoundGetLength=external_define(dll,"FMODSoundGetLength",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundGetLength")

//export double FMODInstanceSoundGetLength(double instance)
global.dll_FMODInstanceSoundGetLength=external_define(dll,"FMODInstanceSoundGetLength",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceSoundGetLength")

//export double FMODInstanceAddEffect(double instance, double effect)
global.dll_FMODInstanceAddEffect=external_define(dll,"FMODInstanceAddEffect",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceAddEffect")

//export double FMODGroupAddEffect(double group, double effect)
global.dll_FMODGroupAddEffect=external_define(dll,"FMODGroupAddEffect",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODGroupAddEffect")

//export double FMODEffectFree(double effect)
global.dll_FMODEffectFree=external_define(dll,"FMODEffectFree",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectFree")


//export double FMODSoundSetLoopCount(double sound, double count)
global.dll_FMODSoundSetLoopCount=external_define(dll,"FMODSoundSetLoopCount",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSetLoopCount")

//export double FMODSoundGetLoopCount(double sound)
global.dll_FMODSoundGetLoopCount=external_define(dll,"FMODSoundGetLoopCount",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundGetLoopCount")

//export double FMODSoundSetLoopPoints(double sound, double start, double end)
global.dll_FMODSoundSetLoopPoints=external_define(dll,"FMODSoundSetLoopPoints",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSetLoopPoints")


//export double FMODInstanceSetSpeakerMix(
//  double instance,  
//  double  frontleft, 
//  double  frontright, 
//  double  center, 
//  double  lfe, 
//  double  backleft, 
//  double  backright, 
//  double  sideleft, 
//  double  sideright)
  
global.dll_FMODInstanceSetSpeakerMix=external_define(dll,"FMODInstanceSetSpeakerMix",dll_stdcall,ty_real,9,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetSpeakerMix")


//export double FMODInstanceSetLoopPoints(double instance, double start, double end)
global.dll_FMODInstanceSetLoopPoints=external_define(dll,"FMODInstanceSetLoopPoints",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetLoopPoints")

//export double FMODInstanceGetAudibility(double instance)
global.dll_FMODInstanceGetAudibility=external_define(dll,"FMODInstanceGetAudibility",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetAudibility")

//export double FMODUpdateTakeOverWhileLocked()
global.dll_FMODUpdateTakeOverWhileLocked=external_define(dll,"FMODUpdateTakeOverWhileLocked",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODUpdateTakeOverWhileLocked")

//export double FMODUpdateTakeOverDone()
global.dll_FMODUpdateTakeOverDone=external_define(dll,"FMODUpdateTakeOverDone",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODUpdateTakeOverDone")

//export double FMODSpectrumSetSnapshotType(double snapshottype)
global.dll_FMODSpectrumSetSnapshotType=external_define(dll,"FMODSpectrumSetSnapshotType",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSpectrumSetSnapshotType")

//export double FMODInstanceGetNextTag(double instance)
global.dll_FMODInstanceGetNextTag=external_define(dll,"FMODInstanceGetNextTag",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetNextTag")

//export double FMODGetTagName(LPSTR buffer)
global.dll_FMODGetTagName=external_define(dll,"FMODGetTagName",dll_stdcall,ty_real,1,ty_string);
if(WTF) show_message("Defined: FMODGetTagName")

//export double FMODGetTagData(LPSTR buffer)
global.dll_FMODGetTagData=external_define(dll,"FMODGetTagData",dll_stdcall,ty_real,1,ty_string);
if(WTF) show_message("Defined: FMODGetTagData")


//export double FMODSoundAddAsyncStream(LPCSTR soundfile, double threed)
global.dll_FMODSoundAddAsyncStream=external_define(dll,"FMODSoundAddAsyncStream",dll_stdcall,ty_real,2,ty_string,ty_real);
if(WTF) show_message("Defined: FMODSoundAddAsyncStream")


//export double FMODSoundAsyncReady(double sound)
global.dll_FMODSoundAsyncReady=external_define(dll,"FMODSoundAsyncReady",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundAsyncReady")


//export double FMODInstanceAsyncOK(double instance)
global.dll_FMODInstanceAsyncOK=external_define(dll,"FMODInstanceAsyncOK",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceAsyncOK")

//export double FMODSoundGetMusicNumChannels(double sound)
global.dll_FMODSoundGetMusicNumChannels=external_define(dll,"FMODSoundGetMusicNumChannels",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODSoundGetMusicNumChannels")

//export double FMODSoundGetMusicChannelVolume(double sound, double channel)
global.dll_FMODSoundGetMusicChannelVolume=external_define(dll,"FMODSoundGetMusicChannelVolume",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundGetMusicChannelVolume")


//export double FMODSoundSetMusicChannelVolume(double sound, double channel, double volume)
global.dll_FMODSoundSetMusicChannelVolume=external_define(dll,"FMODSoundSetMusicChannelVolume",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSoundSetMusicChannelVolume")


//export double FMODEffectGetActive(double effect)
global.dll_FMODEffectGetActive=external_define(dll,"FMODEffectGetActive",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectGetActive")

//export double FMODEffectSetActive(double effect, double v)
global.dll_FMODEffectSetActive=external_define(dll,"FMODEffectSetActive",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetActive")

//export double FMODEffectGetBypass(double effect)
global.dll_FMODEffectGetBypass=external_define(dll,"FMODEffectGetBypass",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectGetBypass")

//export double FMODEffectSetBypass(double effect, double v)
global.dll_FMODEffectSetBypass=external_define(dll,"FMODEffectSetBypass",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetBypass")

//export double FMODEffectGetDefaultPan(double effect)
global.dll_FMODEffectGetDefaultPan=external_define(dll,"FMODEffectGetDefaultPan",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectGetDefaultPan")

//export double FMODEffectSetDefaultPan(double effect, double val)
global.dll_FMODEffectSetDefaultPan=external_define(dll,"FMODEffectSetDefaultPan",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetDefaultPan")


//export double FMODEffectGetDefaultVol(double effect)
global.dll_FMODEffectGetDefaultVol=external_define(dll,"FMODEffectGetDefaultVol",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectGetDefaultVol")

//export double FMODEffectSetDefaultVol(double effect, double val)
global.dll_FMODEffectSetDefaultVol=external_define(dll,"FMODEffectSetDefaultVol",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetDefaultVol")


//export double FMODEffectGetDefaultFr(double effect)
global.dll_FMODEffectGetDefaultFr=external_define(dll,"FMODEffectGetDefaultFr",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectGetDefaultFr")

//export double FMODEffectSetDefaultFr(double effect, double val)
global.dll_FMODEffectSetDefaultFr=external_define(dll,"FMODEffectSetDefaultFr",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetDefaultFr")



//export double FMODEffectGetNumParams(double effect)
global.dll_FMODEffectGetNumParams=external_define(dll,"FMODEffectGetNumParams",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectGetNumParams")

//export double FMODEffectGetParamValue(double effect, double p)
global.dll_FMODEffectGetParamValue=external_define(dll,"FMODEffectGetParamValue",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectGetParamValue")

//export double FMODEffectSetParamValue(double effect, double p, double v)
global.dll_FMODEffectSetParamValue=external_define(dll,"FMODEffectSetParamValue",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetParamValue")

//export double FMODEffectGetParamMin(double effect, double p)
global.dll_FMODEffectGetParamMin=external_define(dll,"FMODEffectGetParamMin",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectGetParamMin")


//export double FMODEffectGetParamMax(double effect, double p)
global.dll_FMODEffectGetParamMax=external_define(dll,"FMODEffectGetParamMax",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectGetParamMax")

//export double FMODEffectGetParamValueStr(double effect, double p, LPSTR str)
global.dll_FMODEffectGetParamValueStr=external_define(dll,"FMODEffectGetParamValueStr",dll_stdcall,ty_real,3,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODEffectGetParamValueStr")

//export double FMODEffectGetParamName(double effect, double p, LPSTR str)
global.dll_FMODEffectGetParamName=external_define(dll,"FMODEffectGetParamName",dll_stdcall,ty_real,3,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODEffectGetParamName")


//export double FMODEffectGetParamLabel(double effect, double p, LPSTR str)
global.dll_FMODEffectGetParamLabel=external_define(dll,"FMODEffectGetParamLabel",dll_stdcall,ty_real,3,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODEffectGetParamLabel")


//export double FMODEffectGetParamDesc(double effect, double p, LPSTR str)
global.dll_FMODEffectGetParamDesc=external_define(dll,"FMODEffectGetParamDesc",dll_stdcall,ty_real,3,ty_real,ty_real,ty_string);
if(WTF) show_message("Defined: FMODEffectGetParamDesc")



//export double FMODEffectGetSpeakerActive(double effect, double speaker)
global.dll_FMODEffectGetSpeakerActive=external_define(dll,"FMODEffectGetSpeakerActive",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectGetSpeakerActive")


//export double FMODEffectSetSpeakerActive(double effect, double speaker, double active)
global.dll_FMODEffectSetSpeakerActive=external_define(dll,"FMODEffectSetSpeakerActive",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODEffectSetSpeakerActive")

//export double FMODEffectReset(double effect)
global.dll_FMODEffectReset=external_define(dll,"FMODEffectReset",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODEffectReset")


//export double FMODInstanceGetPitch(double instance)
global.dll_FMODInstanceGetPitch=external_define(dll,"FMODInstanceGetPitch",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGetPitch")

//export double FMODInstanceSetPitch(double instance, double pitch)
global.dll_FMODInstanceSetPitch=external_define(dll,"FMODInstanceSetPitch",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSetPitch")

//export double FMODSnapShotToDsList(double startpos, double size, double ds)
global.dll_FMODSnapShotToDsList=external_define(dll,"FMODSnapShotToDsList",dll_stdcall,ty_real,3,ty_real,ty_real,ty_real);
if(WTF) show_message("Defined: FMODSnapShotToDsList")

//export double FMODCreateSoundFromMicInput()
global.dll_FMODCreateSoundFromMicInput=external_define(dll,"FMODCreateSoundFromMicInput",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODCreateSoundFromMicInput")

//export double FMODRecordStart(double sound)
global.dll_FMODRecordStart=external_define(dll,"FMODRecordStart",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODRecordStart")

//export double FMODRecordStop(double startpos, double size, double ds)
global.dll_FMODRecordStop=external_define(dll,"FMODRecordStop",dll_stdcall,ty_real,0);
if(WTF) show_message("Defined: FMODRecordStop")


//export double FMODInstanceSet3DSpread(double instance, double spreadangle)
global.dll_FMODInstanceSet3DSpread=external_define(dll,"FMODInstanceSet3DSpread",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3DSpread")

//export double FMODInstanceGet3DSpread(double instance)
global.dll_FMODInstanceGet3DSpread=external_define(dll,"FMODInstanceGet3DSpread",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGet3DSpread")

//export double FMODInstanceSet3DPanLevel(double instance, double panlevel)
global.dll_FMODInstanceSet3DPanLevel=external_define(dll,"FMODInstanceSet3DPanLevel",dll_stdcall,ty_real,2,ty_real,ty_real);
if(WTF) show_message("Defined: FMODInstanceSet3DPanLevel")

//export double FMODInstanceGet3DPanLevel(double instance)
global.dll_FMODInstanceGet3DPanLevel=external_define(dll,"FMODInstanceGet3DPanLevel",dll_stdcall,ty_real,1,ty_real);
if(WTF) show_message("Defined: FMODInstanceGet3DPanLevel")


