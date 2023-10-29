import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento5 {

    public static void main(String[] args) {
        int minstations;
        int maxstations;
        int step;

        PrintStream orgStream = null;

        Scanner in = new Scanner(System.in);
        try {
            orgStream = System.out;

            System.out.println("HC o SA?");
            String algorithm = in.next();
            System.out.println("Quina heuristica estas utilitzant? Bike_profits o Dinamica");
            String heuristica = in.next();
            System.out.println("Output base name?");
            String filename_base = in.next();

            if (algorithm == "SA" || algorithm ==" sa"){
                String[] main_args = new String[]{"--rformat", "-sa","default", "-r", Integer.toString(100)};
                mainexec(orgStream,main_args,filename_base,algorithm,heuristica);
            }
            else {
                String[] main_args = new String[]{"--rformat", "-r", Integer.toString(100)};
                mainexec(orgStream,main_args,filename_base,algorithm,heuristica);
            }

        }
        finally {
            in.close();
        }

    }

    public static void mainexec(PrintStream orgStream, String[] main_args, String filename_base, String algorithm, String heuristica) {
        try {
            PrintStream out;
            out = new PrintStream(new FileOutputStream(filename_base + "_Experimento5_" + algorithm + "_" + heuristica + ".txt", false));
            try {
                    Main exec = new Main(out);
                    exec.execute(main_args);
                }
                catch (Exception e) {
                    orgStream.println("Exception in Main");
                }
        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
    }
}
