import java.io.*;
import java.util.*;

class ExternalMergeSort {
  static List<String> dataChunks = new ArrayList<String>();
  static BufferedReader bufferedReader;
  static File file;
  static int pageCount = 0;

  public static void main(String args[]) {
    file = obtainFile(args[0]);
    obtainBufferedReader(file);
    readAndWriteChunks(512);
  }

  /**
  * Obtains the file containing the data. Checks if file exists
  * and then returns file.
  *
  * @param fileName The path to the file.
  * @return file The File object obtained from the fileName path.
  */
  public static File obtainFile(String fileName) {
    File file = new File(fileName);
    try {
      if (!file.isFile())
        throw new IOException("File does not exist");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return file;
  }

  public static void obtainBufferedReader(File file) {
    try {
      bufferedReader = new BufferedReader(new FileReader(file));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void readAndWriteChunks(int size) {
    List<Character> chunk = new ArrayList<Character>();
    StringBuilder sortedChunk = new StringBuilder();
    int read;
    char character;
    try {
      while ((read = bufferedReader.read()) != -1) {
        character = (char) read;
        chunk.add(character);
        if (chunk.size() > 512 && character == ',') {
          sortedChunk.append(sortChunk(charListToString(chunk)));
          dataChunks.add(sortChunk(charListToString(chunk)));
          chunk.clear();
          pageCount++;
          writeFile("page" + pageCount, sortedChunk.toString());
          sortedChunk = new StringBuilder();
        }
      }
      //dataChunks.add(charListToString(chunk));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String charListToString(List<Character> chunk) {
    StringBuilder string = new StringBuilder();
    for (char character : chunk) {
      string.append(character);
    }
    return string.toString();
  }

  public static String sortChunk(String chunk) {
    int[] intArray = parseIntArray(chunk);
    Arrays.sort(intArray);
    return Arrays.toString(intArray);
  }

  public static int[] parseIntArray(String chunk) {
    String[] stringArray = chunk.split(",");
    List<Integer> integers = new ArrayList<Integer>();
    for (String entry : stringArray) {
      try {
        integers.add(Integer.parseInt(entry));
      } catch (Exception e) {
        System.out.println("Not a number");
      }
    }
    return integers.stream().mapToInt(Integer::intValue).toArray();
  }

  public static void writeFile(String fileName, String sortedChunk) {
    try {
      File file = new File("pages/" + fileName);
      FileWriter writer = new FileWriter(file);
      file.createNewFile();
      writer.write(sortedChunk);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
