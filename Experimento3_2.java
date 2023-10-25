import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento3_2 {

    public static void main(String[] args) {
        int steps = 0;
        int stiter = 1;
        ArrayList<Integer> k = new ArrayList<Integer>();
        ArrayList<Double> lamb = new ArrayList<Double>();
        int nreps = 0;

        PrintStream orgStream = null;

        Scanner in = new Scanner(System.in);
        try {
            orgStream = System.out;

            int aux1;
            System.out.println("Number of repetitions?");
            nreps = in.nextInt();
            System.out.println("Number of iterations?");
            steps = in.nextInt();
            System.out.println("Values of k? (end with 0)");
            while((aux1 = in.nextInt())!=0) {
                k.add(aux1);
            }
            double aux2;
            System.out.println("Values of lambda? (end with 0)");
            while((aux2 = in.nextDouble())!=0.0) {
                lamb.add(aux2);
            }
            System.out.println("Output base name?");
            String filename_base = in.next();

            ArrayList<PrintStream> outs = new ArrayList<PrintStream>();
            for (int i = 0; i < k.size(); ++i) {
                for (int l = 0; l < lamb.size(); ++l) {
                    outs.add(new PrintStream(new FileOutputStream(String.format("%s_k%dl%d.txt", filename_base, i, l), false)));
                }
            }

            for (int i = 0; i < nreps; ++i) {
                int map_seed = (int)(Math.random()*Integer.MAX_VALUE);
                int init_seed = (int)(Math.random()*Integer.MAX_VALUE);
                for (int x = 0; x < k.size(); ++x) {
                    for (int y = 0; y < lamb.size(); ++y) {
                        System.setOut(outs.get(x*lamb.size()+y));
                        String[] main_args = new String[]{"--rformat-no-tags", "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed),
                            "-sa", Integer.toString(steps), Integer.toString(stiter), Integer.toString(k.get(x)), Double.toString(lamb.get(y))};
                        if (i == 0) main_args[0] = "--rformat";
                        try {
                            Main exec = new Main();
                            exec.main(main_args);
                        }
                        catch (Exception e) {
                            orgStream.println("Exception in Main");
                        }
                    }
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
