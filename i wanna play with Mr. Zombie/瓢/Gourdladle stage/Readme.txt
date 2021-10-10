//////////////////////////////////////////////
// I Wanna Be The GM8 Engine YoYoYo Edition //
//////////////////////////////////////////////

This engine saves in AppData/Local (like in GM:S, but you can change this by modifying the value of global.sandboxEnable to false).
Remember to change global.sandboxName too because otherwise there can be a mix of saves with other games and this can disrupt.

update content:
Ver1.52
* Fix sandbox mode path bug
* Change the judgment of loading the room
* Some default values are modified

Ver1.51
* Fix objGameOver position

Ver1.50
* Remove GMFocus & cleanMem due to some bugs
* Fix the depth of some objects
* Save uses json system, smaller save file, reducing the probability of read crash
* Added sandbox mode, the archive path can be like GMS
* will now check if the Data folder exists
* Added detection of DLL files
* Use memory loading when loading SFX
* Add missing objects such as objMiniBlock

Ver1.40
* Add GMFocus, 39dll
* Remove automatic DEBUG mode, because there will be uncertain factors leading to the normal occurrence of DEBUG
* Archive encryption changed to md5
* Restart game mechanism adjustment, save will not cause some problems when Save is invalid
* Removed VsyncMode and related useless global variables
* Fixed a collision detection issue

Ver1.35
* Fixed bug with abnormal music volume
* Modified some temporary variable execution

Ver1.34
* Fix the bug that the volume is reduced after restart

Ver1.33
* Repair the collision box of some objects, set part of the rectangle detection to improve efficiency

Ver1.32
* Fixed an error BUG for the slope
* Fixed a bug in the death state F2 music disappeared

Ver1.31
* Sound import automation
* Initialize variable method adjustments.
* Save mode uses base64+vigenere_ascii

��Used DLL:
GMFMODSimple
CleanMem
39DLL