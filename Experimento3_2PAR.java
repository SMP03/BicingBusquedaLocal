import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento3_2PAR {

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

            ArrayList<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < nreps; ++i) {
                int map_seed =(int)(Math.random()*Integer.MAX_VALUE);
                int init_seed =(int)(Math.random()*Integer.MAX_VALUE);
                for (int x = 0; x < k.size(); ++x) {
                    for (int y = 0; y < lamb.size(); ++y) {
                        Map<String, Object> task = new TreeMap<String, Object>();
                        task.put("out", outs.get(x*lamb.size()+y));
                        task.put("orig", orgStream);
                        task.put("map_seed", map_seed);
                        task.put("init_seed", init_seed);
                        task.put("steps", steps);
                        task.put("stiter", stiter);
                        task.put("k", k.get(x));
                        task.put("l", lamb.get(y));
                        tasks.add(task);
                    }
                }
            }

            for (PrintStream out : outs) {
                String[] main_args = new String[]{"--rformat", "-q"};
                try {
                    Main exec = new Main(out);
                    exec.main(main_args);
                }
                catch (Exception e) {
                    orgStream.println("Exception in Main");
                }

            }
            tasks.parallelStream().forEach(t -> 
                compute((PrintStream)t.get("out"), (PrintStream)t.get("orig"), (int)t.get("map_seed"), (int)t.get("init_seed"), (int)t.get("steps"), (int)t.get("stiter"), (int)t.get("k"), (double)t.get("l")));
        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
        finally {
            in.close();
        }
    }

    private static void compute(PrintStream out, PrintStream orig, int map_seed, int init_seed, int steps, int stiter, int k, double l) {
        String[] main_args = new String[]{"--rformat-no-tags", "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed),
            "-sa", Integer.toString(steps), Integer.toString(stiter), Integer.toString(k), Double.toString(l)};
        try {
            Main exec = new Main(out);
            orig.printf("Thread %10d: Computing with k:%4d, l:%.6f, steps:%6d, stiter:%4d, m:%12d, i:%12d%n", Thread.currentThread().getId(), k, l, steps, stiter, map_seed, init_seed);
            exec.main(main_args);
        }
        catch (Exception e) {
            orig.println("Exception in Main");
        }
    }
}
