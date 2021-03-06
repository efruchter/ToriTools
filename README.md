Downloadables
---------
[Snakemeleon](https://dl.dropbox.com/u/8669480/binaries/Snakemeleon%20v04.zip), a physics platformer.

[Kresendur](https://dl.dropbox.com/u/8669480/binaries/Kresendur.zip), the musical bullet-hell.

ToriTools
=========
A tiled xml level editor and set of classes to make 2D game-making easier.

The top level packages are:

1) toritools, the level editor and all the supporting classes for levels and entities. Includes a physics engine and control framework.

2) samplegame, a small top-down game with dialog bubbles and world doors.

3) audioProject, music powered bullet-hell for a class assignment. Included here as a code example of how to build a game without using level or entity files.

4) snakemeleon, a physics platformer that shows off the Box2D integration.

5) ttt, new draft of the engine, using Jython for scripting and a new XML project structure.

HOW-TO INSTALL for development:

Create a blank java project in eclipse called ToriTools, and drag the git repo onto it and merge. Add all the jars and their natives (if they have them) to the build path.

Just peek into the top level classes for each of the sample games to see how setting up entities and their behavior is done.

How to enable hardware acceleration:

-Dsun.java2d.opengl=True

Windows should activate the directx pipeline automatically.

Swing OpenGL hardware acceleration does not work on OSX. Based on what I've read, there is simply no support for it.

~toriscope
