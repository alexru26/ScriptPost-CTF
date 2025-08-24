import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.*;

public class Grader {
    private final ScriptPost program;

    private static int[] testcases;
    private static int[] expectedList;

    public Grader() {
        program = new ScriptPost();
        File file = new File("./programs/answer.sp");
        program.readFile(file);

        testcases = new int[200];
        expectedList = new int[200];

        try {
            Scanner in = new Scanner(new File("./testcases.csv"));
            int c = 0;
            while (in.hasNextLine()) {
                String[] parts = in.nextLine().split(",");
                testcases[c] = Integer.parseInt(parts[0]);
                expectedList[c] = Integer.parseInt(parts[1]);
                c++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public int getOutput(int test, long timeoutMs) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Callable<Integer> task = () -> {
                program.read(test + " sumDigits");
                program.eval();
                return program.pop();
            };

            Future<Integer> future = executor.submit(task);
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    public boolean validate(int test, int expected, long timeoutMs) {
        try {
            int actual = getOutput(test, timeoutMs);
            return expected == actual;
        } catch (TimeoutException te) {
            System.err.println("Timeout: " + te.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        Grader grader = new Grader();

        boolean correct = true;
        for (int i=0; i<testcases.length; i++) {
            boolean ok = grader.validate(testcases[i], expectedList[i], 5);
            if (ok) {
                System.out.println("Input = " + testcases[i] + " -> " + "PASS");
            } else {
                correct = false;
                System.out.println("Input = " + testcases[i] + " -> " + "FAIL");
            }
        }

        if (correct)
            System.out.println("Congratulations. You did it.");
        else
            System.out.println("Unlucky. Try again.");
    }
}
