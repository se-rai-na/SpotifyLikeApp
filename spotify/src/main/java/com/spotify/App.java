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
  "/Users/serainaburge/Documents/GitHub/SpotifyApp/SpotifyLikeApp";
  private static Scanner input = new Scanner(System.in);
  //stores song name and reference to array position with filepath, artist, year and genre
  private static HashMap <String, String> musicLibrary = new HashMap<String, String>();

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    // test reading audio library from json file
    JSONArray library = readAudioLibrary();

    String userInput = "";
    while (!userInput.equals("q")) {
      menu(library);

      // get input
      userInput = input.nextLine();

      // accept upper or lower case commands
      userInput = userInput.toLowerCase();

      // do something
      handleMenu(userInput, library);
    }

    // close the scanner
    input.close();
  }


  // print the menu
  public static void menu(JSONArray library) {
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
  public static void handleMenu(String userInput, JSONArray library) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        break;
      case "s":
        System.out.println("-->Search by title<--");
        searchByTitle(library);
        break;
      case "l":
        System.out.println("-->Library<--");
        libraryDisplay(library);
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
  public static void libraryDisplay(JSONArray musicLibrary){
    for (Integer i = 0; i < musicLibrary.size(); i++) {
      JSONObject obj = (JSONObject) musicLibrary.get(i);
      String name = (String) obj.get("name");
      System.out.printf("[%d] %s\n", i + 1, name);
    }

    // get input
    String userInput = input.nextLine();
    
    //get the number from the input
    Integer userChoice = Integer.parseInt(userInput);

    //subtract one to match the indices
    userChoice -= 1;

    //get the name of the song to pass to the play function as argument
    JSONObject obj = (JSONObject) musicLibrary.get(userChoice);
    String name = (String) obj.get("name");

    //pass name to play function to play the song
    play(name);

  }

  //looks for a specific song the user wants to play
  public static void searchByTitle (JSONArray library){
    System.out.println("Which song are you looking for?");
    
    // get input
    String userChoice = input.nextLine();

    //see if the song exists
    musicLibrary.get(userChoice);

    //send song choice to play function
    play(userChoice);
  }

  // plays an audio file
  public static void play(String songName) {
    // get the filePath and open the audio file
    final String fileName = songName.toLowerCase();
    final String filePath = basePath + "/" + fileName + ".wav";
    final File file = new File(filePath);

    // stop the current song from playing, before playing the next one
    if (audioClip != null) {
      audioClip.close();
    }

    try {
      // create clip
      audioClip = AudioSystem.getClip();

      // get input stream
      final AudioInputStream in = AudioSystem.getAudioInputStream(file);

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
  public static JSONArray readAudioLibrary() {
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

      musicLibrary.put(name, "%s, %s, %s" + artist + year + genre);

      System.out.println("\tname = " + name);
      System.out.println("\tartist = " + artist);
      System.out.println("\tyear = " + year);
      System.out.println("\tgenre = " + genre);
    }

    return jsonData;
  }
}
