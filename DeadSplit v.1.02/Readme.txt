DeadSplit v1.19

This is a program for tracking your death count and other stats in fangames.
=============================================================

How to use:
Open the counter and start playing one of the supported fangames. When you reach a boss, the tracking should start automatically.
Click on attack stats to change the name/icon or edit the stats manually.
You can open a specific fangame yourself by using the Open Fangame icon in the bottom left.
Program behaviour, appearance and hotkeys can be changed in the Settings menu by pressing the Gear icon in the bottom left.

=============================================================

List of supported fangames:

- I Wanna Kill The Kamilia 3
- I Wanna Break The Series Z3 ( up to Boss Rush )
- I Wanna Buy The Crayon ( final boss )
- I Wanna Call Me It ( extra boss )
- I Wanna Touch The Entrance
- I Wanna Grand Of The Perfect Bear
- I Wanna Be The C/O/S/M/O
- I Wanna Be The Contrary
- I Wanna KiraKira
- I Wanna Bye The Bye
- I Wanna Bye The Entrance
- I Wanna Be The Snow Drive
- I Wanna Sunspike
- I Wanna Appreciate Meteor Stream
- I Wanna Break Things Into Pieces
- I Wanna Be The Fish
- I Wanna Paradoxx
- I Wanna Be The Rubik's Cube

=============================================================

Redire made this :)
For feedback and bug reports please message me on Twitch ( https://www.twitch.tv/redire ) or Discord ( Redire#3559 ).

Special Thanks to:

- Danil2332 for layouts and testing.
- IWBTB_Hype for layouts and testing.
- Kamilia for his help with 1.50 integration.
- PrincessKennyTheBest, you know for what.
- Rasty for layouts and testing.
- SUDALV for layouts and testing.

Thanks to:

- All of those who tested and gave feedback for the early versions.
- The Russian fangame community.

=============================================================

F.A.Q.

Q: How does it work?
A: The fangame is detected by looking for opened windows with a specific title. Most of the necessary data for detecting attacks is read directly from the game memory. If you'd like to dwell into specifics, check out the layout.xml files for any game in the FangameInfo folder.
	
Q: How do I add new fangames or change existing bosses?
A: Check out BossEditIntructions.txt for the information on how to add fangames and edit boss attacks.

Q: I'm running into performance issues. Can I make DeadSplit run faster?
A: There are three things you can try: changing the renderer type, turning off real time stat updates and lowering the FPS. All of these things can be changed in the settings.

Q: Why is the counter not reacting to an opened fangame?
A: The fangame is captured as soon as your death count appears in the window title. You will need to actually start playing for the counter to find your game. Also, if you open the game as Administrator, make sure DeadSplit is opened as Administrator as well.

Q: I try to capture the counter for my stream and the window shows up as a black rectangle. What do?
A: Seems to be a problem with Window Capture in OBS Studio. Switch to Software renderer in DeadSplit settings, use Game Capture or turn off Windows Aero.

Q: I find the splits and attack progress tracking to be distracting/unfair. Can I disable them but keep the stats?
A: You can! Progress tracking for each boss is controlled in the boss settings ( click the boss name ). You can also disable progress tracking for specific attacks in attack settings ( click the attack stats ).

Q: Can I use a different button to restart the game so that the counter properly understands it?
A: Yes. You will need to set the button you use for restarting in the counter settings.

=============================================================

Copyright (c) 2017 Alexey Nikiforov a.k.a. Redire

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

-------------------------------------------------------------

This software uses libcurl:

Copyright (c) 1996 - 2017, Daniel Stenberg, <daniel@haxx.se>, and many
contributors, see the THANKS file.

All rights reserved.

Permission to use, copy, modify, and distribute this software for any purpose
with or without fee is hereby granted, provided that the above copyright
notice and this permission notice appear in all copies.

-------------------------------------------------------------

This software uses RapidXml:

Copyright (c) 2006, 2007 Marcin Kalicinski

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the "Software"), to deal 
in the Software without restriction, including without limitation the rights 
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.
