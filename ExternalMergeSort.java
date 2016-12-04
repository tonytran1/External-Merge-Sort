import java.io.*;
import java.util.*;
/**
* # External Merge Sort
* # Author: Tony Tran
*
* This program uses Java to implement the External Merge Sort.
*
* In Java, each char is 2 Bytes.
* Therefore 4096B/2B = 2048 will be the size we use in our readWriteChunks(2048)
* method below. You may adjust this accordingly. It will adjust the
* numbers and size of txt files outputed in each pass.

* This program uses the filename passed in through the command line.
* You can manually set the file name in the main method if you prefer.
*
* In pass 0, the program reads the file and writes a new txt file
* for every 4KB worth of character data read. It will still write a
* last file if 4KB is not reached.
*
* In sequential passes, the program uses the merge sort algorithm to
* merge seperated sorted chunks of data.
*
* The end result will be one sorted txt file.
*
* Each pass will create a folder and hold all the files merged at that point.
* The folder pass0/ will contain all the subfiles that were split from
* the original read.
*
*/
class ExternalMergeSort {
  static BufferedReader reader1;
  static BufferedReader reader2;
  static File file;
  static Integer count = 0;
  static Map<Integer, Integer> pageCount = new HashMap<>();

  static final int bufferSize = 2048; // char size. You may change this value.

  // Two-Way External Merge Sort
  public static void main(String args[]) {
    try {
      file = obtainFile(args[0]);
      readWriteChunks(bufferSize); // Pass 0
      readMergeFileChunks(); // Pass 1+
    } catch (Exception e) {
      System.out.println("Please input a file path.");
      System.out.println("Example:\njava ExternalMergeSort \"age.txt\" ");
      System.exit(1);
    }
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

  /**
  * Reads the full file while spliting the file up to subfiles each time the size
  * limit has been reached.
  *
  * Also known as Pass 0 from the two-way external merge sort algorithm.
  *
  * @param size The number of characters that should be read before writing out.
  */
  public static void readWriteChunks(int size) {
    List<Character> chunk = new ArrayList<Character>();
    StringBuilder sortedChunk = new StringBuilder();
    Integer count = 0;
    int read;
    char character;
    obtainBufferedReader(file);
    try {
      while ((read = reader1.read()) != -1) {
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
      if (!chunk.isEmpty()) {
        sortedChunk.append(sortChunk(charListToString(chunk)));
        count++;
        reader1.close();
        writeFile("page" + count, sortedChunk.toString(), 0);
      }
      pageCount.put(0, count);
      System.out.println("Pass 0 has completed.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
  * Assigning a buffered reader for the file.
  *
  * @param file The file object obtained from a file name.
  */
  public static void obtainBufferedReader(File file) {
    try {
      reader1 = new BufferedReader(new FileReader(file));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
  * Convert character list to string.
  *
  * @param list The character list that will be converted to string.
  * @return The list in string form.
  */
  public static String charListToString(List<Character> list) {
    StringBuilder string = new StringBuilder();
    for (char character : list) {
      string.append(character);
    }
    return string.toString();
  }

  /**
  * Uses built in Java library method for quick-sorting the array.
  * Regular expression used to remove brackets and spaces.
  *
  * @param chunk contains the read 4KB chunk of string data from the text file.
  * @return intArray containing the sorted chunk of data as a string.
  */
  public static String sortChunk(String chunk) {
    int[] intArray = parseIntArray(chunk);
    Arrays.sort(intArray);
    return Arrays.toString(intArray).replaceAll("\\[|\\]|\\s", "");
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

  /**
  * Writes a file using the information given.
  *
  * @param fileName The filename of the new file to be written.
  * @param sortedChunk The chunk of data sorted and in String form.
  * @param pass The current pass in the loop.
  */
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

  /**
  * The main loop merging all the files.
  * The loop will continue until only 1 txt file is produced in the end.
  *
  * Also the start method for Passes 1+ in the Two-Way Merge Sort Algorithm.
  *
  * This method calls on the readSortWrite method which will be the
  * major code content of the passes.
  *
  */
  public static void readMergeFileChunks() {
    int pass = 1;
    while (true) {
      readSortWrite(pass);
      if (pageCount.get(pass) == 1) {
        break;
      };
      System.out.println("Pass " + pass + " has completed.");
      pass++;
    }
    System.out.println("Pass " + pass + " is the last pass! Complete!\nCheck the folder.");
  }

  /**
  * Assigns the file readers to the correct location to start
  * merging the files via the merge sort algorithm.
  *
  * The files are written out to their respective locations each time
  * a merge sort is completed.
  *
  * @param pass The current pass within the Two-Way Merge Sort Algorithm.
  */
  public static void readSortWrite(int pass) {
    StringBuilder output = new StringBuilder();
    int previousPassCount = pageCount.get(pass - 1);
    Integer count = 0;
    int[] data1;
    int[] data2;
    try {
      for (int i = 1; i < previousPassCount; i += 2) {
        File file1 = obtainFile("pass" + (pass - 1) + "/page" + (i) + ".txt");
        File file2 = obtainFile("pass" + (pass - 1) + "/page" + (i + 1) + ".txt");
        reader1 = new BufferedReader(new FileReader(file1));
        reader2 = new BufferedReader(new FileReader(file2));
        data1 = parseIntArray(readFile(reader1));
        data2 = parseIntArray(readFile(reader2));
        reader1.close();
        reader2.close();
        output.append(mergeSort(data1, data2));
        count++;
        writeFile("page" + count, output.toString(), pass);
        output = new StringBuilder();
      }
      if ((previousPassCount % 2) != 0) { // Write last file if odd number of pages.
        File file1 = obtainFile("pass" + (pass - 1) + "/page" + previousPassCount + ".txt");
        if (file1.isFile()) {
          reader1 = new BufferedReader(new FileReader(file1));
          count++;
          writeFile("page" + count, readFile(reader1), pass);
          reader1.close();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
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
    StringBuilder data = new StringBuilder();
    String line;
    try {
      while ((line = inputBuffer.readLine()) != null) {
        data.append(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return data.toString();
  }

  /**
  * Implementation of the merge sort.
  *
  * Takes two sorted integer arrays and merges them together by comparing
  * the head of each array until an ascending order is produced.
  *
  * @param contents1 The first sorted integer array.
  * @param contents2 The second sorted integer array.
  * @return string The final sorted list data structure to a string. Ready to write out.
  */
  public static String mergeSort(int[] contents1, int[] contents2) {
    List<Integer> sorted = new ArrayList<>();
    int iterator1 = 0;
    int iterator2 = 0;
    while (iterator1 < contents1.length || iterator2 < contents2.length) {
      if (iterator1 == contents1.length) {
        for (int i = iterator2; i < contents2.length; i++) {
          sorted.add(contents2[iterator2]);
        }
        iterator2 = contents2.length;
        break;
      }
      if (iterator2 == contents2.length) {
        for (int i = iterator1; i < contents1.length; i++) {
          sorted.add(contents1[iterator1]);
        }
        iterator1 = contents1.length;
        break;
      }
      if (contents1[iterator1] < contents2[iterator2]) {
        sorted.add(contents1[iterator1]);
        iterator1++;
      } else {
        sorted.add(contents2[iterator2]);
        iterator2++;
      }
    }
    return sorted.toString().replaceAll("\\[|\\]|\\s", "");
  }

}
