
# Installation

## Building Clava

Download [eclipse-build](http://specs.fe.up.pt/tools/eclipse-build.jar) (source code can be found [here](https://github.com/specs-feup/specs-java-tools/tree/master/EclipseBuild)) and run the following command:

`java -jar eclipse-build.jar --config https://raw.githubusercontent.com/specs-feup/clava/master/ClavaWeaver/eclipse.build`

This should create the file ClavaWeaver.jar. 

## Downloading Clava

A zip file with a precompiled Clava JAR can be downloaded from this [link](http://specs.fe.up.pt/tools/clava.zip).

If you are using Linux there is an [instalation script](https://specs.fe.up.pt/tools/clava/clava-update). Running the script will install Clava in the folder where the script is, e.g. running the script from /usr/local/bin should make the command `clava` available system-wide. If you run the script with `sudo`, it will install the Clava package for CMake.

## Clava Online Demo Version

There is a demo online version of Clava available [here](http://specs.fe.up.pt/tools/clava).


# Running Clava


Clava can be run from CMake, using a GUI, or as a command-line tool.


## CMake

Instructions for the Clava CMake plugin can be found [here](https://github.com/specs-feup/clava/tree/master/CMake).



## GUI


Run the JAR with passing parameters, e.g.:

	java -jar Clava.jar


A video demonstrating the GUI can be found [here](https://www.youtube.com/watch?v=IFvNWYCivFA).

## Command Line


There are two main modes in command line, either passing all arguments (LARA file, parameters, etc...), or passing a configuration file that was built with the graphical user interface.



### Using parameters:

	java -jar Clava.jar <aspect.lara> -p <source_folder>

where <aspect.lara> is the LARA aspect you want to execute, and <source_folder> is the folder where the source code is.


There are more command-line options available, which can be consulted by running:

	java -jar Clava.jar --help


		
### Configuration file:

To pass a configuration file, use the flag -c:

	java -jar Clava.jar -c <config.clava>

where <config.clava> is the configuration file created with the GUI.


# configuring eclipse

Please check the README file in repository [specs-java-libs](https://github.com/specs-feup/specs-java-libs).



