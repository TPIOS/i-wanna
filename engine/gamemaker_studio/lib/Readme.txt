ACTION LIBRARIES
================

This folder contains the default action libraries. To create and change these you must need to use the
special version of the library maker (compiled with the conditional SPECIALMODE). Only that version can
edit library files with id's below 1000. All default libraries sofar use library id 1.

Note that all actions in the default library simply call a function in Game Maker. They do not use
additional files nor do they have initialization code. 

When creating the distribution, the files in this folder (except for this readme file) must be copied
to the correct lib folder in the Distribution\Files folder.

Action IDs
----------
Each action in a library must have a unique id. Below you find a list of these ids. This can be
used to make sure all action ids are unique when adding new actions to the default libraries. It does
not matter how they are numbered, as long as they are all unique. Also, only use id's below 1000. If
there is not enough id's left it is possible to create a new default library with a library id 2 or
higher. All library id's below 1000 are reserved for default libraries and cannot be used for user
created libraries.

101	Move
102	Set Motion
103	Set Hspeed
104	Set Vspeed
105	Move towards Point

107	Set Gravity
108	Set Friction
109	Move To
110	Move to Start
111	Move Random
112   Wrap
113	Reverse Xdir
114	Reverse YDir
115	Bounce
116	Move Contact
117	Snap
118	Path (OLD)
119	Path
120	Step Linear
121	Step Potential
122	Path Position
123	Path Speed
124	Path End
______________________________________

201	Create Object
202	Change Object
203	Kill Object
204	Kill at Position
205	Set Sprite (OLD)
206	Create Object Motion
207   Create Object Random

211	Play Sound
212	End Sound
213	If Sound

221	Previous Room
222	Next Room
223	Current Room
224	Another Room
225	If Previous Room
226	If Next Room
______________________________________

301	Set Alarm
302	Sleep
303	Set Time Line (OLD)
304	Position Time Line
305   Set Time Line
306   Start Time Line
307   Pause Time Line
308   Stop Time Line
309   Time Line Speed

321	Message
322	Show Info
323	Show Video OLD
324	Splash Text
325	Splash Image
326	Splash Website
327 	Splash Video
328 	Splash Settings

331	Restart Game
332	End Game
333	Save Game
334	Load Game
______________________________________

401	If Empty
402	If Collision
403	If Object
404	If Number
405	If Dice

407	If Question
408	If
409	If Mouse
410	If Aligned

421 	Else
422	Start Block
423	Repeat
424	End Block
425	Exit
______________________________________

501	Draw Sprite
502	Draw Background

511	Draw Rectangle
512	Draw Ellipse
513	Draw Line
514	Draw Text
515	Draw Arrow
516	Draw Horizontal Gradient
517	Draw Vertical Gradient
518	Draw Ellipse Gradient
519	Draw Text Transformed

521	Fill Color (OLD)
522	Line Color (OLD)
523	Draw Font (OLD)
524	Set Color
525	Draw Font (OLD)
526   Set Font

531	Full Screen
532	Explosion

541   Set Sprite
542	Transform Sprite
543   Color Sprite

______________________________________

601	Execute Script

603	Code
604	Inherited
605	Comment

611	Variable
612	If Variable
613	Draw Variable
______________________________________

701	Set Score
702	If Score
703	Draw Score

706 	Highscore OLD
707 	Highscore Clear

709	Highscore Show

711	Set Life
712	If Life
713	Draw Life
714	Draw Life Images

721	Set Health
722	If Health
723	Draw Health

731	Set Caption
______________________________________

801	Set Mouse
802	Take Snapshot
803	Replace Sprite
804	Replace Sound
805	Replace Background
806	Gradient Background
807 	Open Webpage
808	CD Play
809	CD Stop
810	CD Pause
811	CD Resume
812	CD If Exists
813	CD If Playing

820	Part Syst Create
821	Part Syst Destroy
822	Part Syst Clear

823	Part Type Create
824	Part Type Color
825	Part Type Create (OLD)
826	Part Type Life
827	Part Type Speed
828	Part Type Gravity
829	Part Type Secondary

831	Part Emit Create
832	Part Emit Destroy
833	Part Emit Burst
834	Part Emit Stream
______________________________________

