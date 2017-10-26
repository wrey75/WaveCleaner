[![Build Status](https://travis-ci.org/wrey75/WaveCleaner.svg?branch=master)](https://travis-ci.org/wrey75/WaveCleaner)

# WaveCleaner

A project to clean the recorded sound of vinyl records. Started in October 2017 from scratch.

## Why a new record cleaner?

You can find several software to record and clean the sound of vinyl records:

- [Audacity](http://www.audacityteam.org/home/): a very good (and well-known) software to read sound files,
modify them and repair them. Including a decrackling, a declicking and a noise reduction.
- [WaveRepair](http://www.delback.co.uk/wavrep/): I used it a long time ago and does
very good job. Even on really bad records. The noise removal is exceptional but it's
a Windows shareware. And it takes time to get the best values.
- [ClickRepair](http://www.clickrepair.net/): another software to remove clicks and
denoise your records but not tested. Works with Java then can be used on all platforms. Note
there are two software.
- [GoldWave](https://www.goldwave.com/goldwave.php): for Windows, a extreme editor I used
a long time ago. Now includes filters for noise reduction on your records.
- [GTK Wave Cleaner](http://gwc.sourceforge.net/): formely "Gnome Wave Cleaner", this is an open
sourced software now maintained by Alister Hood (https://github.com/AlisterH/gwc).  

All of them works great. But the main issue I got by using them is losing time to get the
right values for each of my records. It takes take to have the correct parameters for each record.
You have to test, listen and test again.

This software is diffrent, because it works in "real-time".

## The philosophy

Rather than selecting a piece of sound and processing it, the software is processing in real-time.
If you change a value in the filters, you will listen the change immediately. This is a different behavior
compared to non-linear software above.

The software plays the original file and push the sound from a filter to another in a pipeline. The
last stage is the "pre-amplifier". The Preamplifier has 2 functionalities: you can master the volume and
the source. Then you can listen the original source untouched, the sound filtered or the difference
between the original and filtered sound (to check you are not removing music but only clics and noise).

# The technical background

This project is based on the [Minim](http://code.compartmental.net/tools/minim/) library
that provide very good stuff to manipulate the sound. I started to code with the excellent
[Processing](https://processing.org/) language but I found limitations to create a user
interface.

By the way, I created a library named [XML4swing](https://github.com/wrey75/xml4swing)
a long time ago to create JAVA GUI with Swing. Even if this technology is out-of-date
and replaced by the JavaFX, I kept it because of the simplicity of programming and modifying
the user interface.

This is the main reason the software is written in JAVA (the other response is: you can run
it everywhere from Unix, MacOS and Windows). I have planned to create a native package for Windows
and Mac but not force the first official release


## Why a GNU Licence?

Well, I suppose I'm forced to use it mainly because I would like to take piece of code from
Audacity. I am not sure I can with another license. But it's a very quick choice and can be changed
at any time.

## CAn I use it?

Well, currently, not really. But, for each new new feature, I do a preview release. Do not use for
production: there is neither a save option neither a record button. But you can load your file and
check how the software can evolve.
 

## Requirements

Basically, a dual CPU is needed but to live refresh of the graphical components and live filtering but
a basic Windows PC or a Mac should be fine. I tried on both without any issue.

Disk storage is required. We use about 700 MB for 30 minutes of playback. This is because we work internally
on floating point numbers (with a precision of 24 bits). For 192kHz sampling, the size grows to 3 Gb for 30 minutes.

## Limitations

There are 2 limitations in the coding I have made and nothing will change this in the future:
 - The sampling rates are only 48kHz, 96kHz and 192kHz. Nothing else (44.1kHz is possible but the results
 are not expected to be as good as for other sampling rates).
 - We are working on STEREO files only (no 5:1 files and other stuff). We don't expect to have a MONO
 input but you will be able to have a enhanced MONO output. 

The limitations come from the fact the software has been creating to restore files,
not for editing or other stuff. The limitation about the sampling rate is due to mathematics. We use
a FFT formulae on a multiple of 1024 samples (1024 for 48kHz, 2048 for 96kHz). Then the size of
the samples will not match the same duration for 44.1kHz, meaning you could have some difference on
the output.

