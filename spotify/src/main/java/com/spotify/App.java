package com.spotify;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;
import javax.swing.plaf.multi.MultiScrollBarUI;

import org.json.simple.*;
import org.json.simple.parser.*;

// declares a class for the app
public class App {

  // prviate variables for the app
  private static Clip audioClip;
  private static String basePath =
  "/Users/serainaburge/Documents/GitHub/SpotifyApp/wav";
  private static Scanner input = new Scanner(System.in);
  //stores song name and reference to array position with filepath, artist, year and genre
  private static HashMap <String, ArrayList<String>> musicLibrary = new HashMap<String, ArrayList<String>>();
  //stores the play history
  private static ArrayList <String> playHistory = new ArrayList<String>();

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    //reading audio library from json file into the global variable musicLibrary
    readAudioLibrary();

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


  // print the menu
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    
    System.out.println("[H]ome");

    System.out.println("[S]earch by title");

    System.out.println("[L]ibrary");

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
        searchByTitle();
        break;
      case "l":
        System.out.println("-->Library<--");
        libraryDisplay();
        break;
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    } 

    //check if the input is a number, if not if it is q, then quit
    /*try {
      
      Integer number = Integer.parseInt(songChoice);

      // subtract 1 from user input to match the indices
      number -= 1;

      // if number is 1-5 play the song
      /* if (number < library.size()) {
        play(library, number);
      }
    } catch (Exception e) {
      if (userInput.equals("q")) {
        System.out.println("Thank you for using the app.");
      } else {
        System.out.printf("Error: %s is not a command\n", userInput);
      } */ 
     
  }

  //prints the entire music library the user can choose from
  public static void libraryDisplay(){
    /*for (Integer i = 0; i < Library.size(); i++) {
      JSONObject obj = (JSONObject) Library.get(i);
      String name = (String) obj.get("name");
      System.out.printf("[%d] %s\n", i + 1, name);
    }*/

    for(String key : musicLibrary.keySet()){
      System.out.println(key);
      System.out.println(musicLibrary.get(key));
    }

    /*/ get input
    String userInput = input.nextLine();
    
    //get the number from the input
    Integer userChoice = Integer.parseInt(userInput);

    //subtract one to match the indices
    userChoice -= 1;

    //get the name of the song to pass to the play function as argument
    JSONObject obj = (JSONObject) Library.get(userChoice);
    String name = (String) obj.get("name");

    System.out.println("This is the song: " + name);
    //pass name to play function to play the song
    play(name);*/

  }

  //looks for a specific song the user wants to play
  public static void searchByTitle (){
    System.out.println("Which song are you looking for?");
    
    Boolean correctInput = false;
    while(!correctInput){
    // get input
    String userChoice = input.nextLine();
      if(musicLibrary.containsKey(userChoice))
      {
        correctInput = true;
        //send song choice to play function
        play(userChoice);
      }
      else{
        System.out.println("This song is not in the library. Enter another one or press q to quit.");
      }
    }
  }

  // plays an audio file
  public static void play(String songName) {
    // add the song to the song history
    playHistory.add(songName);

    // get the filePath and open the audio file
    String fileName = songName.toLowerCase();
    String filePath = basePath + "/" + fileName + ".wav";
    File file = new File(filePath);

    // stop the current song from playing, before playing the next one
    if (audioClip != null) {
      audioClip.close();
    }

    try {
      // create clip
      audioClip = AudioSystem.getClip();

      // get input stream
      AudioInputStream in = AudioSystem.getAudioInputStream(file);

      audioClip.open(in);
      audioClip.setMicrosecondPosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //
  // Func: readJSONFile
  // Desc: Reads a json file storing an array and returns an object
  // that can be iterated over
  //
  public static JSONArray readJSONArrayFile(String fileName) {
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    JSONArray dataArray = null;

    try (FileReader reader = new FileReader(fileName)) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);

      dataArray = (JSONArray) obj;
      // System.out.println(dataArray);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return dataArray;
  }

  // read the audio library of music
  public static void readAudioLibrary() {
    String pathToFile =
      "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify/src/main/java/com/spotify/spotifyLibrary.json";
    JSONArray jsonData = readJSONArrayFile(pathToFile);

    // loop over list
    String name, artist, year, genre;
    JSONObject obj;

  

    System.out.println("Reading the file " + pathToFile);

    for (Integer i = 0; i < jsonData.size(); i++) {
      // parse the object and pull out the name and birthday
      obj = (JSONObject) jsonData.get(i);
      name = (String) obj.get("name");
      artist = (String) obj.get("artist");
      year = (String) obj.get("year");
      genre = (String) obj.get("genre");

      // Array that stores artist, year and genre
      ArrayList <String> songInfo = new ArrayList <String>();
      songInfo.add(artist);
      songInfo.add(year);
      songInfo.add(genre);

      musicLibrary.put(name, songInfo);
    }
    for(String key : musicLibrary.keySet()){
      System.out.println(key);
      System.out.println(musicLibrary.get(key));
    }
  }  
}
