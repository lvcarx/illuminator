# Illuminator
This is a simple photo editing app. You can set preset manipulations for specific photo categories, manipulate the color curve,
apply manipulations only to specific parts of the image and rotate your image accordingly to the horizon. 

## System requirements
The app is primarily developed and tested on macOS 11. It works on Windows and Linux too, but further testing needs to be done.

## How to install the app
You need to install the Java JDK and Maven.
Instructions can be found here: https://www.oracle.com/de/java/technologies/javase/javase-jdk8-downloads.html (Java)  
http://maven.apache.org/install.html (Maven) 

If Java and Maven are installed, navigate to the root directory of the project in a terminal and execute the command "mvn clean install assembly:single".  
The executable jar "illuminator-0.1-jar-with-dependencies.jar" will be compiled in the "target" folder. Double-click on the .jar file to start the app.

There is also a precompiled version of the application in the "target" folder.

## Key features
  - Preset manipulations
  - Color curve
  - Applying image manipulations only to specific parts of the image
  - Zoom in and out of your image
  - Revert your changes

## Supported file formats
Currently, only .png files are supported by the app.

## When will a snapshot of the current image be saved?
To understand how to revert changes with the "Rückgängig machen" button it is important to understand, when a snapshot of the edited image is saved.

A snapshot of your image will always be saved if you rotate the image or switch between the side menus (brush- and global). To revert the changes made before, you have to click "Rückgängig machen".  
All changes made in one panel are reversible by setting the sliders, curves, etc. to their initial value.