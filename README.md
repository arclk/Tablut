# TablutCompetition
Software for the Tablut Students Competition

## Documentation
https://arclk.github.io/Tablut/

## Installation on Ubuntu/Debian 

From console, run these commands to install JDK 8 e ANT:

```
sudo apt update
sudo apt install openjdk-8-jdk -y
sudo apt install ant -y
```

Now, clone the project repository.

## Run the Server without Eclipse

The easiest way is to utilize the ANT configuration script from console.
Go into the project folder (the folder with the `build.xml` file):
```
cd TablutCompetition/Tablut
```

Compile the project:

```
ant clean
ant compile
```

The compiled project is in  the `build` folder.

In order to generate the `.jar` into the `dist` folder, type in the project folder:

```
ant jar
```

Run the server with:

```
ant server
```

Check the behaviour using the random players in two different console windows:

```
ant randomwhite

ant randomblack
```

At this point, a window with the game state should appear.

To be able to run other classes, change the `build.xml` file and re-compile everything

In order to run the student player with the algorithm MinMax with AlphaBeta pruning run in two different console windows:

```
ant studentwhite

ant studentblack
```

In order to run the `.jar` file go into the `dist` folder and run:

```
java -jar StudentPlayer.java
```
It accepts 3 arguments (in order): the role (“White” or “Black”), the timeout time (in seconds), the server IP address.
To make sure that the player doesn't exceed the timeout, the last three seconds are left to process and send the move.
