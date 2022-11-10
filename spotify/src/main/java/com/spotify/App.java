/*
 * Seraina Burge
 * Spotify Like App
 * October 2022
 * v1.0
 */
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
  //stores song name as key and hashmap with filepath, artist, year and genre as value
  private static HashMap <String, HashMap<String, Object>> musicLibrary = new HashMap<String, HashMap<String, Object>>();
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


  /*
  * Func: print the menu
  */
   public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    
    System.out.println("[H]ome");

    System.out.println("[S]earch by title");

    System.out.println("[L]ibrary");

    System.out.println("S[t]op playing");

    System.out.println("(R)ewind 10 seconds");

    System.out.println("(F)orward by 10 seconds")

    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:");

    System.out.println("");
  }

  /*
   * Func: handles the user input for the app
   * Desc: User input will be used to start the corresponding functions
   */
  public static void handleMenu(String userInput) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        home();
        break;
      case "s":
        System.out.println("-->Search by title<--");
        searchByTitle();
        break;
      case "l":
        System.out.println("-->Library<--");
        libraryDisplay();
        break;
      case "t":
        System.out.println("-->Stop<--");
        stop();
      case "r":
        System.out.println("-->Rewind<--");
        rewind();
      case "f":
        System.out.println("-->Forward<--");
        forward();
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    } 
  }

  /*
  *
  * Func: prints the entire music library the user can choose from
  * Desc: Read musicLibrary keys into an array. User inputs number that corresponds to 
  * the song name inside the keyMusicLibrary() array.
  */
  public static void libraryDisplay(){
    //to store keys in the hashmaps order and retrieve them
    String[] keyMusicLibrary = new String [musicLibrary.size()];

    //to account for indices in the keyMusicLibrary array
    Integer i = 0;

    for(String key : musicLibrary.keySet()){
      //create hashmap to read the song title information
      HashMap <String, Object> songCredits = new HashMap <String, Object>();
      //read current keys object into the variable songCredits
      songCredits = musicLibrary.get(key);
      //Display number, song title and information; i+1 so the output starts from 1
      System.out.print("["+(i+1)+"] " + key);

      //print artist, year, genre
      for(Object value : songCredits.values()){
        System.out.print(", " + value);
      }

      System.out.println(" ");
      System.out.println(" ");
      //Keys are saved in the array to be retrieved after receiving user input
      keyMusicLibrary[i] = key;
      i++;
    } //end for loop

    // get input
    String userInput = input.nextLine();

    /*
     Check if the input is valid
     */
    try{
    //get the number from the input
    Integer userChoice = Integer.parseInt(userInput);

    //subtract one to match the indices
    userChoice -= 1; 
    //get the name of the song to pass to the play function as argument
    String songTitle = keyMusicLibrary[userChoice]; 

      //test for valid input, number has to be smaller than the amount of songs in the library
      if(userChoice < musicLibrary.size()){
        //pass name to play function to play the song
        play(songTitle);
      }
    } catch (Exception e) {
      if (userInput.equals("q")) {
        System.out.println("Thank you for using the app.");
      } else {
        System.out.printf("Error: %s is not a command\n", userInput);
      }
    }
  }

  /*
  *
  *Func: looks for a specific song the user wants to play
  *Desc: uses the musicLibrary global variable to look up wether
  *  a specific song exists. The name then sent to the function audio()
  */
  public static void searchByTitle (){
    System.out.println("Which song are you looking for?");
    
    Boolean correctInput = false;
    //while loop repeats until valid song title is entered
    while(!correctInput){
    // get input
    String userChoice = input.nextLine();

    //test if the song exists in the library
      if(musicLibrary.containsKey(userChoice))
      {
        //to exit the while loop
        correctInput = true;
        //send song choice to play function
        play(userChoice);
      }
      else{
        System.out.println("This song is not in the library.");
      }
    }
  }

  /*
  *
  * plays an audio file
  *
  */ 
  public static void play(String songName) {
    //initialize hashmap used to read song information
    HashMap <String, Object> songInfo = new HashMap <String, Object>();
    //add song information in hashmap
    songInfo = musicLibrary.get(songName);
    //add song name and artistName 
    playHistory.add(songName +", "+ songInfo.get("artist"));
    

    //Display currently played song title to user
    System.out.println("You are listening to: " + songName);

    // get the filePath and open the audio file (filepath is located at indice 3 in the array)
    String filePath = basePath + "/" + songInfo.get("filepath");
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

  /*
   * Func: Stop currently played song
   * 
   */
  public static void stop(){
    if(audioClip != null){
      audioClip.stop();
    }
  }

  /*
   * Func: Rewind currently played song by 10 seconds
   */
  public static void rewind(){
    if(audioClip != null){
      audioClip.stop();
      long position = audioClip.getMicrosecondPosition();
      audioClip.setMicrosecondPosition(position-10000000);
      audioClip.start();
      }
    }
  
  /*
   * Func: Forward currently played song by 10 seconds
   */
  public static void forward(){
    if(audioClip != null){
      audioClip.stop();
      long position = audioClip.getMicrosecondPosition();
      audioClip.setMicrosecondPosition(position+10000000);
      audioClip.start();
      }
  }
  

  /* 
  *Func: Displays the recently played song
  *Desc: Displays the recently played song by using the global variable playHistory, 
  *which stores all the song titles that are passed to the play function
  */
  public static void home(){
    System.out.println("Your recently played songs:");
    for(int i = 0; i < playHistory.size(); i++){
      //display a number starting with 1 with the song tiile and artist
      System.out.println((i+1) + " " + playHistory.get(i));
    }
    System.out.println("");
  }

  /* 
  * Func: readJSONFile
  * Desc: Reads a json file storing an array and returns an object
  * that can be iterated over
  */
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

  /*
  *
  * read the audio library of music
  *
  */ 
  public static void readAudioLibrary() {
    String pathToFile =
      "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify/src/main/java/com/spotify/spotifyLibrary.json";
    JSONArray jsonData = readJSONArrayFile(pathToFile);

    // loop over list
    String name, artist, year, genre, filepath;
    JSONObject obj;

    for (Integer i = 0; i < jsonData.size(); i++) {
      // parse the object and pull out the name and birthday
      obj = (JSONObject) jsonData.get(i);
      name = (String) obj.get("name");
      artist = (String) obj.get("artist");
      year = (String) obj.get("year");
      genre = (String) obj.get("genre");
      filepath = (String) obj.get("filepath");

      // Hashmap that stores artist, year and genre
      HashMap <String, Object> songInfo = new HashMap <String, Object>();
      songInfo.put("artist", artist);
      songInfo.put("year", year);
      songInfo.put("genre", genre);
      songInfo.put("filepath", filepath);
      songInfo.put("favorite", false);

      musicLibrary.put(name, songInfo);
    }
  }  
}
