import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento4 {

    public static void main(String[] args) {
        int minstations;
        int maxstations;
        int step;
        int h;

        PrintStream orgStream = null;

        Scanner in = new Scanner(System.in);
        try {
            orgStream = System.out;

            System.out.println("Minimum number of stations?");
            minstations = in.nextInt();
            System.out.println("Maximum number of stations?");
            maxstations = in.nextInt();
            System.out.println("Step size?");
            step = in.nextInt();
            System.out.println("Heuristic? 0: Only bikes profit, 1: bikes profit + transport cost, 2: bikes profit + dynamic transport cost");
            h = in.nextInt();
            System.out.println("Output base name?");
            String filename_base = in.next();

            PrintStream out;
            out = new PrintStream(new FileOutputStream(filename_base + "_station.txt", false));
            for (int s = minstations; s <= maxstations; s+=step) {
                
                String[] main_args = new String[]{"--rformat-no-tags", "--change-stations", Integer.toString(s), "-r", Integer.toString(10), "-he", Integer.toString(h)};
                if (s == minstations) main_args[0] = "--rformat";
                try {
                    Main exec = new Main(out);
                    exec.execute(main_args);
                }
                catch (Exception e) {
                    orgStream.println("Exception in Main");
                }


            }

        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
        finally {
            in.close();
        }
    }
}
