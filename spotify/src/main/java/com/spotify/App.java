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
  //the amount of elements stored insude the array in the hashmap
  private static Integer elements = 3;
  //stores song name and reference to array position with filepath, artist, year and genre
  private static HashMap <String, String[]> musicLibrary = new HashMap<String, String[]>();
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

    System.out.println("");
  }

  /*
   * handles the user input for the app
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
    //to only create and add to the key array once
    //Boolean firstShow;
    //to store keys in the hashmaps order and retrieve them
    String[] keyMusicLibrary = new String [musicLibrary.size()];

    //to account for indices in the keyMusicLibrary array
    Integer i = 0;

    for(String key : musicLibrary.keySet()){
      //create array to read the song title information
      String[] songCredits = new String [elements];
      //read current keys object into the variable songCredits
      songCredits = musicLibrary.get(key);

      //Display number, song title and information; i+1 so the output starts from 1
      System.out.println("["+(i+1)+"] " + key);
      //print artist, year, genre
      for(Integer y = 0; y < elements; y++){
        System.out.print(songCredits[y] + ", ");
      }

      System.out.println("");
      //Keys are saved in the array to be retrieved after receiving user input
      keyMusicLibrary[i] = key;
      i++;
    }

    // get input
    String userInput = input.nextLine();
    
    //get the number from the input
    Integer userChoice = Integer.parseInt(userInput);

    //subtract one to match the indices
    userChoice -= 1;

    //get the name of the song to pass to the play function as argument
    String songTitle = keyMusicLibrary[userChoice];

    System.out.println("This is the song: " + keyMusicLibrary[userChoice]);
    //pass name to play function to play the song
    play(songTitle);

  }

  //looks for a specific song the user wants to play
  public static void searchByTitle (){
    System.out.println("Which song are you looking for?");
    
    Boolean correctInput = false;
    while(!correctInput){
    // get input
    String userChoice = input.nextLine();
    //test if the song exists in the library
      if(musicLibrary.containsKey(userChoice))
      {
        correctInput = true;
        //send song choice to play function
        play(userChoice);
      }
      else{
        System.out.println("This song is not in the library.");
      }
    }
  }

  // plays an audio file
  public static void play(String songName) {
    //add song information in array
    String[] artistName = musicLibrary.get(songName);
    //add song name and: artistName[0] -> add only the artist to array
    playHistory.add(songName +", "+ artistName[0]);
    

    //Display currently played song title to user
    System.out.println("You are listening to: " + songName);

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

  //Func: Displays the recently played song
  //Desc: Displays the recently played song by using the global variable playHistory, 
  //which stores all the song titles that are passed to the play function
  //
  public static void home(){
    System.out.println("Your recently played songs:");
    for(int i = 0; i < playHistory.size(); i++){
      System.out.println("\t" + playHistory.get(i));
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
      String[] songInfo = new String [elements];
      songInfo[0] = artist;
      songInfo[1] = year;
      songInfo[2] = genre;

      musicLibrary.put(name, songInfo);
    }
    for(String key : musicLibrary.keySet()){
      System.out.println(key);
      System.out.println(musicLibrary.get(key));
    }
  }  
}
