import java.io.*;
import java.util.*;

class ExternalMergeSort {
  static BufferedReader inputBuffer1;
  static BufferedReader inputBuffer2;
  static File file;
  static int pageCount = 0;
  static Map<Integer, Integer> pageCount = new HashMap<>();

  // In Java, Each char is 2 Bytes. Therefore 4096B/2B = 2048
  public static void main(String args[]) {
    file = obtainFile(args[0]);
    obtainBufferedReader(file);
    readAndWriteChunks(2048);
    readAndMergeFileChunks();
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
      inputBuffer1 = new BufferedReader(new FileReader(file));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void readAndWriteChunks(int size) {
    List<Character> chunk = new ArrayList<Character>();
    StringBuilder sortedChunk = new StringBuilder();
    int count;
    int read;
    char character;
    try {
      while ((read = inputBuffer1.read()) != -1) {
        character = (char) read;
        chunk.add(character);
        if (chunk.size() > size && character == ',') {
          sortedChunk.append(sortChunk(charListToString(chunk)));
          chunk.clear();
          count++;
          writeFile("page" + count, sortedChunk.toString(), 0);
          sortedChunk = new StringBuilder();
        }
      }
      sortedChunk.append(sortChunk(charListToString(chunk)));
      count++;
      writeFile("page" + count, sortedChunk.toString(), 0);
      pageCount.put(0, count);
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

  /**
  * Uses built in Java library method for quick sorting the array.
  *
  * @param chunk contains the read 4KB chunk of string data from the text file.
  * @return intArray containing the sorted chunk of data as a string.
  */
  public static String sortChunk(String chunk) {
    int[] intArray = parseIntArray(chunk);
    Arrays.sort(intArray);
    return Arrays.toString(intArray).replaceAll("\\[|\\]", "");
  }

  /**
  * Parses an integer array from a string containing data.
  *
  * @param data contains the string of number data.
  * @return integerArray An array parsed from the string of data.
  */
  public static int[] parseIntArray(String data) {
    String[] stringArray = data.split(",");
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

  public static void writeFile(String fileName, String sortedChunk, int pass) {
    try {
      File file = new File("pass" + pass + "/" + fileName + ".txt");
      if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      }
      FileWriter writer = new FileWriter(file);
      file.createNewFile();
      writer.write(sortedChunk);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void readAndMergeFileChunks() {
    int pass = 1;
    while (true) {
      readSortAndWrite();
      if (pageCount.get(pass) == 1) {
        break
      };
      pass++;
    }
  }

  public static void readSortAndWrite(int pass, int numberOfPages) {
    StringBuilder output = new StringBuilder();
    int previousPassCount = pageCount.get(pass - 1);
    int count;
    int[] data1;
    int[] data2;
    for (int i = 1; i < previousPassCount; i += 2) {
      File file1 = obtainFile("pass" + (pass - 1) + "/page" + (i));
      File file2 = obtainFile("pass" + (pass - 1) + "/page" + (i + 1));
      inputBuffer1 = new BufferedReader(new FileReader(file1));
      inputBuffer2 = new BufferedReader(new FileReader(file2));
      data1 = parseIntArray(readFile(inputBuffer1));
      data2 = parseIntArray(readFile(inputBuffer2));
      output.append(mergeSort(data1, data2));
      count++;
      writeFile("page" + count, output.toString(), pass);
    }
    pageCount.put(pass, count);
  }

  /**
  * Takes input file buffer and returns the contents in a String.
  *
  * @param inputBuffer BufferedReader of the current file.
  * @return String containing the contents of the file.
  */
  public static String readFile(BufferedReader inputBuffer) {
    String data = '';
    while ((data = inputBuffer.readLine()) != null) {
      data += data;
    }
    return data;
  }

  public static String mergeSort(int[] contents1, int[] contents2) {
    List<Integer> sorted = new ArrayList<>();
    int iterator1 = 0;
    int iterator2 = 0;
    while (iterator1 < contents1.length || iterator2 < contents2.length) {
      if (iterator1 > contents1.length) {
        for (int i = iterator2; contents2.length, i++) {
          sorted.append(contents2[iterator2]);
        }
        iterator2 = contents2.length;
      }
      if (iterator2 > contents2.length) {
        for (int i = iterator1; contents1.length, i++) {
          sorted.append(contents1[iterator1]);
        }
        iterator1 = contents1.length;
      }
      if (contents1[iterator1] <= contents2[iterator2]) {
        sorted.append(contents1[iterator1]);
        iterator1++;
      } else {
        sorted.append(contents2[iterator2]);
        iterator2++;
      }
    }
  }

}
