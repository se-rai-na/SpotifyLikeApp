package com.spotify;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;
import org.json.simple.*;
import org.json.simple.parser.*;

// declares a class for the app
public class App {

  // prviate variables for the app
  private static Clip audioClip;
  private static String basePath =
  "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify/wav";
  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    // test reading audio library from json file
    JSONArray library = readAudioLibrary();

    // create a scanner for user input
    Scanner input = new Scanner(System.in);

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

    for (Integer i = 0; i < library.size(); i++) {
      JSONObject obj = (JSONObject) library.get(i);
      String name = (String) obj.get("name");
      System.out.printf("[%d] %s\n", i + 1, name);
    }

    System.out.println("[P]ause");

    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:");
  }

  /*
   * handles the user input for the app
   */
  public static void handleMenu(String userInput, JSONArray library) {
    // check if the input is a number, if not if it is q, then quit
    try {
      Integer number = Integer.parseInt(userInput);

      // subtract 1 from user input to match the indices
      number -= 1;

      // if number is 1-5 play the song
      if (number < library.size()) {
        play(library, number);
      }
    } catch (Exception e) {
      if (userInput.equals("q")) {
        System.out.println("Thank you for using the app.");
      } else {
        System.out.printf("Error: %s is not a command\n", userInput);
      }
    }
  }

  // plays an audio file
  public static void play(JSONArray library, Integer songIndex) {
    // get the filePath and open the audio file
    JSONObject obj = (JSONObject) library.get(songIndex);
    final String fileName = (String) obj.get("fileName");
    final String filePath = basePath + "/" + fileName;
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
      "/Users/serainaburge/Documents/GitHub/SpotifyApp/spotify/src/main/java/com/spotify/audio-library.json";
    JSONArray jsonData = readJSONArrayFile(pathToFile);

    // loop over list
    String name, artist, fileName;
    JSONObject obj;

    System.out.println("Reading the file " + pathToFile);

    for (Integer i = 0; i < jsonData.size(); i++) {
      // parse the object and pull out the name and birthday
      obj = (JSONObject) jsonData.get(i);
      name = (String) obj.get("name");
      artist = (String) obj.get("artist");
      fileName = (String) obj.get("filePath");

      System.out.println("\tname = " + name);
      System.out.println("\tartist = " + artist);
      System.out.println("\tfilePath = " + fileName);
    }

    return jsonData;
  }
}
