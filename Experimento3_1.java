import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento3_1 {

    public static void main(String[] args) {
        int steps = 0;
        int stiter = 100;
        int k = 0;
        double lamb = 0.0;

        PrintStream orgStream = null;

        Scanner in = new Scanner(System.in);
        try {
            orgStream = System.out;

            System.out.println("Number of iterations?");
            steps = in.nextInt();
            System.out.println("Value of k?");
            k = in.nextInt();
            System.out.println("Value of lambda?");
            lamb = in.nextDouble();
            System.out.println("Output base name?");
            String filename_base = in.next();

            PrintStream outHC, outSA;
            outHC = new PrintStream(new FileOutputStream(filename_base + "_HC_trace.txt", false));
            outSA = new PrintStream(new FileOutputStream(filename_base + "_SA_trace.txt", false));


            int map_seed = (int)(Math.random()*Integer.MAX_VALUE);
            int init_seed = (int)(Math.random()*Integer.MAX_VALUE);
            System.setOut(outHC);
            String[] main_args = new String[]{"--rtrace-cost", "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed)};
            try {
                Main exec = new Main();
                exec.main(main_args);
            }
            catch (Exception e) {
                orgStream.println("Exception in Main");
            }

            System.setOut(outSA);
            main_args = new String[]{"--rtrace-cost", "-sa", Integer.toString(steps), Integer.toString(stiter), Integer.toString(k), Double.toString(lamb),
                "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed)};
            try {
                Main exec = new Main();
                exec.main(main_args);
            }
            catch (Exception e) {
                orgStream.println("Exception in Main");
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
