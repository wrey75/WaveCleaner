[![Build Status](https://travis-ci.org/wrey75/WaveCleaner.svg?branch=master)](https://travis-ci.org/wrey75/WaveCleaner)

# WaveCleaner
A project to clean the recorded sound of vinyl records.

This project is based on the [Minim](http://code.compartmental.net/tools/minim/) library
that provide very good stuff to manipulate the sound. I started to code with the excellent
[Processing](https://processing.org/) language but I found limitations to create a user
interface.

By the way, I created a library named [XML4swing](https://github.com/wrey75/xml4swing)
a long time ago to create JAVA GUI with Swing. Even if this technology is out-of-date
and replaced by the JavaFX, I kept it because of the simplicity of programming and modifying
the user interface.

This is the main reason the software is written in JAVA (the other response is: you can run
it everywhere from Unix, MacOS and Windows).


## Why another software?

The question is correct: why not using the excellent [Audacity](http://www.audacityteam.org/)
program? In fact, I use it and it is a really good software for audio edition. But, the fact is
an editing program is not the perfect way to manage the complexity of removing the crackles and 
other issues from vinyl records.

I know there are some function in Audacity, especially for this type of work ("Suppression de
clic" by CraigDeForest) and a noise reduction by Dominic Mazzoni. They are good piece of software 
(and I will look into them because it is under GPL licence).

There is a excellent software called [Wave Repair](http://www.delback.co.uk/wavrep/) but only
working on Windows and it is a shareware. In addition it takes a long time to get excellent
results (but you should buy it, it is really amazing).

There is another one called [ClickRepair](http://www.clickrepair.net/), it works under
JAVA and it is NOT open sourced. As a surprise, only Audacity and GNU Wave Cleaner seems
to have open sourced the algorithms.


## Why a GNU Licence?

Well, I suppose i'm forced to use it mainly because I would like to take piece of code from
Audacity. I am not sure I can with another licence. But it a very quick choice and can be changed
at any time.

## What the program does

Well, currently, nothing. Except loading a song in memory. When I say a song, it is a vinyl
recorded audio file. But it is intended to modify and enhance the audio file you recoreded.
Be patient.

## The software asks for big storage

Yes. Indeed. Mainly because the Minim library uses _float_ numbers to store the samples. Then one
sample takes 32 bits, I mean 4 bytes. On a CD, the recording is saved on 16 bits only, then 2 bytes
only. Then the record takes twice the size of a CD. That's mean about 1.2 GB for one hour of recording
on 48kHz and 2.4 GB for 96kHz sampling (weel, 4.8GB for 192kHz).

This is a normal behaviour. You can also work with 24 bits samples if you want.

## Limitations

There are 2 limitations in the coding I have made and nothing will change this:
 - A project (all the files used in a project) MUST have the same sampling rate.
 - The sampling rates are only 48kHz, 96kHz and 192kHz. Nothing else (don't expect 44.1kHz!).
 - We are working on mono and stereo files only (no 5:1 files and other stuff).

The limitations come from the fact the software has been creating to restore files,
not for editing or other stuff.

NOTE: if you have files recorded in 44.1kHz, use Audacity to change the sampling rate.
 



 



 
