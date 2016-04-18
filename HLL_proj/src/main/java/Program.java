import com.google.common.base.Stopwatch;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import net.agkn.hll.HLL;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sushantathley on 3/19/16.
 */
public class Program {

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    final static HLL hll = new HLL(13/*log2m*/, 5/*registerWidth*/);

    static HashFunction murmur3 = Hashing.murmur3_128(123456);

    static Set<String> uniqueIps = new LinkedHashSet<String>();

    static int counter = 0;

    static String fileName = "/Users/sushantathley/Downloads/dbip-city-2016-03.csv-2";


    public static void main(String[] args) {


        estimateCardinalityHLL();
        calculateCardinalityHashSet();


    }


    private static void estimateCardinalityHLL() {
        System.out.println("Using HLL");

        for (int i = 1; i <= 14; i++) {
            long start = System.currentTimeMillis();
            counter = 0;
            hll.clear();

            String line = null;
            try {
                FileReader fileReader =
                        new FileReader(fileName);

                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);


                while (counter < i * 1000000 && (line = bufferedReader.readLine()) != null) {


                    int firstIndex = line.indexOf("\"");
                    int secondIndex = line.indexOf("\"", firstIndex + 1);
                    String ip = line.substring(firstIndex + 1, secondIndex);
                    HashCode hashCode1 = murmur3.hashString(ip, Charset.defaultCharset());
                    hll.addRaw(hashCode1.asLong());
                    counter++;
                    firstIndex = line.indexOf("\"", secondIndex + 1);
                    secondIndex = line.indexOf("\"", firstIndex + 1);

                    ip = line.substring(firstIndex + 1, secondIndex);
                    hashCode1 = murmur3.hashString(ip, Charset.defaultCharset());
                    hll.addRaw(hashCode1.asLong());
                    counter++;


                }


                // Always close files.
                bufferedReader.close();
            } catch (FileNotFoundException ex) {
                System.out.println(
                        "Unable to open file '" +
                                fileName + "'");
            } catch (IOException ex) {
                System.out.println(
                        "Error reading file '"
                                + fileName + "'");


            }

            Runtime runtime = Runtime.getRuntime();
            // Run the garbage collector
            runtime.gc();
            // Calculate the used memory
            long memory = runtime.totalMemory() - runtime.freeMemory();
            long time = System.currentTimeMillis() - start;
            System.out.println("Elements : " + counter);
            System.out.println("Cardinality : " + hll.cardinality());
            System.out.println("Memory used in bytes : " + (memory / 1024L));
            System.out.println("Time taken : " + time);
            System.out.println();
        }
    }


    private static void calculateCardinalityHashSet() {

        System.out.println("Using HashSet");

        for (int i = 1; i <= 14; i++) {
            long start = System.currentTimeMillis();
            counter = 0;
            uniqueIps.clear();
            String line = null;
            try {
                FileReader fileReader =
                        new FileReader(fileName);

                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);


                while (counter < i * 1000000 && (line = bufferedReader.readLine()) != null) {

                    int firstIndex = line.indexOf("\"");
                    int secondIndex = line.indexOf("\"", firstIndex + 1);
                    String ip = line.substring(firstIndex + 1, secondIndex);
                    uniqueIps.add(ip);
                    counter++;
                    firstIndex = line.indexOf("\"", secondIndex + 1);
                    secondIndex = line.indexOf("\"", firstIndex + 1);

                    ip = line.substring(firstIndex + 1, secondIndex);
                    uniqueIps.add(ip);
                    counter++;


                }

                // Always close files.
                bufferedReader.close();
            } catch (FileNotFoundException ex) {
                System.out.println(
                        "Unable to open file '" +
                                fileName + "'");
            } catch (IOException ex) {
                System.out.println(
                        "Error reading file '"
                                + fileName + "'");


            }

            Runtime runtime = Runtime.getRuntime();
            // Run the garbage collector
            runtime.gc();
            // Calculate the used memory
            long memory = runtime.totalMemory() - runtime.freeMemory();
            long time = System.currentTimeMillis() - start;
            System.out.println("Elements : " + counter);
            System.out.println("Cardinality using hashset : " + uniqueIps.size());
            System.out.println("Memory used in bytes : " + bytesToMegabytes(memory));
            System.out.println("Time taken : " + time);
            System.out.println();
        }
    }
}
