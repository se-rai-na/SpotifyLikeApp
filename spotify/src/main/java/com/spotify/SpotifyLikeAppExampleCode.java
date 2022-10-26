package com.spotify;
import static javax.sound.sampled.AudioSystem.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

/*
    To compile: javac SpotifyLikeApp.java
    To run: java SpotifyLikeApp
 */

// declares a class for the app
public class SpotifyLikeAppExampleCode {

  // global variables for the app
  String status;
  Long position;
  static Clip audioClip;
  
  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    // create a scanner for user input
    Scanner input = new Scanner(System.in);

    String userInput = "";
    while (!userInput.equals("q")) {
      menu();
    
      // get input
      userInput = input.nextLine();

      // accept upper or lower case commands
      userInput = userInput.toLowerCase();

      // do something
      handleMenu(userInput);
    }

    // close the scanner
    input.close();
  }

  /*
   * displays the menu for the app
   */
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[P]lay");
    System.out.println("[R]ewind");
    System.out.println("[Q]uit");
    

    System.out.println("");
    System.out.print("Enter q to Quit:");
  }

  /*
   * handles the user input for the app
   */
  public static void handleMenu(String userInput) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        break;
      case "s":
        System.out.println("-->Search by title<--");
        break;
      case "l":
        System.out.println("-->Library<--");
        break;
      case "p":
        System.out.println("-->Play<--");
        play();
        break;
      /*case "stop":
        System.out.println("-->Stop<--");
        stop();*/
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    }
  }

  /*
   * plays an audio file
   */
  public static void play() {
    // open the audio file

    /*
    *** IMPORTANT NOTE FOR ALL STUDENTS *******

    This next line of code is a "path" that students will need to change in order to play music on their
    computer.  The current path is for my laptop, not yours.
    
    If students who do not understand whre files are located on their computer or how paths work on their computer, 
    should immediately complete the extra credit on "Folders and Directories" in the canvas modules.  
    
    Knowing how paths work is fundamental knowledge for using a computer as a technical person.

    Students who do not know what a path are often not able complete this assignment succesfullly.  Please
    do the extra credit if you are confused. :)  
    
    Thank you!  -Gabriel

    */
    String path =
      "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify-example/wav";

    // get a handle to the file
    final File fileHandle = new File(path);

    try {
      // create clip
      audioClip = AudioSystem.getClip();

      // get input stream
      final AudioInputStream in = getAudioInputStream(fileHandle);

      // play the audio
      audioClip.open(in);
      audioClip.setMicrosecondPosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  /*public static void stop(){
    audioClip
}*/
}
