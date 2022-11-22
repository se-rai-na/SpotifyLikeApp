/*
 * Seraina Burge
 * Spotify Like App
 * October 2022
 * v1.0
 * TO DO: use array list to save song names with favorite values that need to be changed inside json 
 */
package com.spotify;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import javax.lang.model.util.ElementScanner14;
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
  //stores songs as keys and favorite as value for changed favorite values
  private static HashMap <String, Boolean> favoriteSong = new HashMap<String, Boolean>();
  //array that saves song names for favorite values that have been changed 
  private static HashMap <String, Boolean> favoriteChanges = new HashMap<String, Boolean>();
  //json arrat that stores json data and is used at the end to update the file
  private static JSONArray jsonData;

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
      case "q":
        System.out.println("-->Quit<--");
        writeAudioLibrary();
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
      //a heart is printed if the song is a favorite
      if (favoriteSong.containsKey(key))
        System.out.print("<3 ");

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
    

    //Display currently played song title, artist, year to user
    System.out.printf("You are listening to: %s by %s (%s) \n", songName, songInfo.get("artist"), songInfo.get("year"));

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
    //once a song starts playing a submenu to navigate the song and favorite it is displayed
    displaySubMenu();
    String userInput = "";
    while(!userInput.equals("e"))
    {
      //get user input
      userInput = input.nextLine();
      //set it to lower case
      userInput.toLowerCase();
      //let function handle
      handleSubMenu(userInput, songName);
    }
  }

  public static void displaySubMenu(){
    System.out.println("[P]ause the song");

    System.out.println("[H]eart the song");

    System.out.println("S[t]op playing");

    System.out.println("[P]ause");

    System.out.println("P[l]ay");

    System.out.println("[R]ewind 10 seconds");

    System.out.println("[F]orward 10 seconds");

    System.out.println("[E]xit the submenu");
  }

  public static void handleSubMenu(String userInput, String songName){
    switch (userInput) { 
      case "h":
        System.out.println("-->Heart<--");
        favoriteDisplay(songName);
        break;
      case "t":
        System.out.println("-->Stop<--");
        stop();
        break;
      case "p":
        System.out.println("-->Pause<--");
        pause();
        break;
      case "l":
        System.out.println("-->Play<--");
        play();
        break;
      case "r":
        System.out.println("-->Rewind<--");
        rewind();
        break;
      case "f":
        System.out.println("-->Forward<--");
        forward();
        break;
      case "e":
        System.out.println("-->Exit<--");
        break;
      default:
        break;
    }
  }

  public static void favoriteDisplay(String songName){
    System.out.println("Your favorite songs:");
    //get user input on wether the currently played song should be hearted or not
    String userInput = "";
    //Display all the favorite songs
    for(String key : favoriteSong.keySet()){
      System.out.println(key);
    }
    if(favoriteSong.containsKey(songName))
      System.out.printf("Do you want to unfavorite '%s'? [y]es, any other key for no   ", songName);  
    else
      System.out.printf("Do you want to favorite '%s' [y]es, any other key for no    ", songName);
    
    //get user input
    userInput = input.nextLine();
    //set it to lower case
    userInput.toLowerCase();
    
    //switch statement to handle user input
    switch(userInput){
      case "y":
        favorite(songName);
        displaySubMenu();
        break;
      default:
        displaySubMenu();
        break;
    }   
  }


  public static void favorite(String songName){
    //if the key is already in the hashmap, it has to be changed to unfavorite; the key/value
    //pair is removed and added to the hashmap favoriteChanged for later reference
    if(favoriteSong.containsKey(songName))
    {
      //remove from hashmap favoriteSongs
      favoriteSong.remove(songName); 

      //check if song has been changed before in the current session
      if(favoriteChanges.containsKey(songName))
        //change key value pair to true
        favoriteChanges.replace(songName, true, false);
      else
        //add key/value pair            
        favoriteChanges.put(songName, false); 
    } 

    //if the value is not in favoriteSong, the song is not a favorite yet
    else{
      //add song to favoriteSong hashmap 
      favoriteSong.put(songName, true);
      
      //check if song has been changed in the current session
      if(favoriteChanges.containsKey(songName))
        //change the key/value pair to true
        favoriteChanges.replace(songName, false, true);
        //add song to favoriteChanges for later reference
        favoriteChanges.put(songName, true);
    }
  }
  /*
   * Func: Pause currently played song
   */
  public static void pause(){
    if(audioClip != null){
      audioClip.stop();
    }
  }

  /*
   * Func: continue playing currently paused song
   */
  public static void play(){
    if(audioClip != null){
      audioClip.start();
    }
  }

  /*
   * Func: Stop currently played song
   * 
   */
  public static void stop(){
    if(audioClip != null){
      audioClip.close();
    }
  }

   /*
   * Func: Forward currently played song by 10 seconds
   */
  public static void forward(){
    if(audioClip != null){
      audioClip.stop();
      long position = audioClip.getMicrosecondPosition();
      //get position to forward 10 sec
      position += 10000000;
      audioClip.setMicrosecondPosition(position);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } 
  }
  
  /*
   * Func: Rewind currently played song by 10 seconds
   * WHY does it now work?
   */
  public static void rewind(){
    if(audioClip != null){
      audioClip.stop();
      long Position = audioClip.getMicrosecondPosition();
      //get position to rewind 10 sec
      Position -= 10000000;
      audioClip.setMicrosecondPosition(Position);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
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

    jsonData = null;

    try (FileReader reader = new FileReader(fileName)) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);

      jsonData = (JSONArray) obj;
      // System.out.println(dataArray);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return jsonData;
  }

  /*
  *
  * read the audio library of music
  *
  */ 
  public static void readAudioLibrary() {
    String pathToFile =
      "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify/src/main/java/com/spotify/spotifyLibrary.json";
    readJSONArrayFile(pathToFile);

    // loop over list
    String name, artist, year, genre, filepath;
    Boolean favorite; 
    //Boolean favorite;
    JSONObject obj;

    for (Integer i = 0; i < jsonData.size(); i++) {
      // parse the object and pull out the name and birthday
      obj = (JSONObject) jsonData.get(i);
      name = (String) obj.get("name");
      artist = (String) obj.get("artist");
      year = (String) obj.get("year");
      genre = (String) obj.get("genre");
      filepath = (String) obj.get("filepath");
      //favorite = (Boolean) obj.get("favorite");
      favorite = (Boolean) obj.get("favorite");

      //only add favorite songs to hashmap favoriteSong
      if(favorite)
        favoriteSong.put(name, true);

      // Hashmap that stores artist, year and genre
      HashMap <String, Object> songInfo = new HashMap <String, Object>();
      songInfo.put("artist", artist);
      songInfo.put("year", year);
      songInfo.put("genre", genre);
      songInfo.put("filepath", filepath);
      //songInfo.put("favorite", favorite2);

      musicLibrary.put(name, songInfo);
    }
  }
    public static void writeAudioLibrary(){
      //update the jsonarray jsonData
      JSONObject obj;
      String name;
      for(Integer i = 0; i < jsonData.size(); i++){
        obj = (JSONObject) jsonData.get(i);
        name = (String) obj.get("name");
        //check if favorite of that song was changed during the program
        if(favoriteChanges.containsKey(name)){
          //if the value is true
          if(favoriteChanges.get(name)){
            obj.remove("favorite");
            obj.put("favorite", true);
          }
          //if the value is false
          else{
            obj.remove("favorite");
            obj.put("favorite", false);
          }
          System.out.println(obj);
          System.out.println(jsonData.get(i));
          //jsonData.remove(i);
          //jsonData.put(null);
        }
      } 

      //write changes onto the json file
      FileWriter file;
      String pathToFile =
      "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify/src/main/java/com/spotify/spotifyLibrary.json";

      try{
        //open file
        file = new FileWriter(pathToFile);
        //write changes onto file after tunring JSONArray into a JSONString
        file.write(jsonData.toJSONString());
        //flush the stream
        file.flush();
        //close the file
        file.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }  
}
