import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * ScriptPost program CTF
 * @author Alex Ru
 * @version 08.24.25
 */
public class ScriptPost {
    private static class State {
        public Map<String, Queue<String>> dict = new HashMap<>(); // Map for function definitions
        public Deque<Queue<String>> returnStack = new ArrayDeque<>(); // Stack of tokens to return to
    }

    private Queue<String> queue = new ArrayDeque<>(); // Input queue
    private final Deque<Integer> stack = new ArrayDeque<>(); // Operand stack
    private final State state = new State(); // Current state (see above)

    /**
     * Read text and add tokens to queue
     * @param text line of text
     */
    public void read(String text) {
        String[] tokens = text.strip().split("\\s+");
        queue.addAll(Arrays.asList(tokens));
    }

    /**
     * Read through file and add tokens to input queue
     * @param file file to read
     */
    public void readFile(File file) {
        try {
            // Read from file
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNext()) {
                // Read line from file
                String line = fileScanner.nextLine();
                read(line);
            }
        } catch (FileNotFoundException e) { // Shouldn't happen
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Evaluate tokens... good luck understanding this...
     * @return continue reading more input (only used for test method)
     */
    public boolean eval() {
        while (!(queue.isEmpty() && state.returnStack.isEmpty())) {
            if (queue.isEmpty()) {
                queue = state.returnStack.pop();
            }

            String tok = queue.poll();
            assert tok  != null;

            switch (tok) {
                case "print" -> {
                    if (!stack.isEmpty()) {
                        int x = stack.pop();
                        System.out.println(x);
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "quit" -> {
                    return false;
                }
                case "+" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            stack.push(x + y);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "-" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            stack.push(x - y);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "*" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            stack.push(x * y);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "/" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            if (y == 0 || (x == Integer.MIN_VALUE && y == -1))
                                throw new IllegalStateException("arithmetic error: invalid division");
                            stack.push(x / y);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "%" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            if (y == 0 || (x == Integer.MIN_VALUE && y == -1))
                                throw new IllegalStateException("arithmetic error: invalid mod");
                            stack.push(x % y);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "**" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            if (y < 0) throw new IllegalStateException("arithmetic error: invalid exponent");
                            int res = 1;
                            for (int i = 0; i < y; i++) res *= x;
                            stack.push(res);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "<" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            stack.push(x < y ? 1 : 0);
                        } else throw new IllegalStateException("invalid operation: no int");
                    } else throw new IllegalStateException("invalid operation: no int");
                }
                case "drop" -> {
                    if (!stack.isEmpty()) {
                        stack.pop();
                    } else {
                        throw new IllegalStateException("invalid drop");
                    }
                }
                case "swap" -> {
                    if (!stack.isEmpty()) {
                        int y = stack.pop();
                        if (!stack.isEmpty()) {
                            int x = stack.pop();
                            stack.push(y);
                            stack.push(x);
                        } else throw new IllegalStateException("invalid swap");
                    } else throw new IllegalStateException("invalid swap");
                }
                case "rot" -> {
                    if (!stack.isEmpty()) {
                        int z = stack.pop();
                        if (!stack.isEmpty()) {
                            int y = stack.pop();
                            if (!stack.isEmpty()) {
                                int x = stack.pop();
                                stack.push(y);
                                stack.push(z);
                                stack.push(x);
                            } else throw new IllegalStateException("invalid rot");
                        } else throw new IllegalStateException("invalid rot");
                    } else throw new IllegalStateException("invalid rot");
                }
                case "if" -> {
                    if (!stack.isEmpty()) {
                        int x = stack.pop();
                        if (x == 0) {
                            for (int i = 0; i < 3; i++) {
                                if (!queue.isEmpty()) {
                                    queue.poll();
                                } else throw new IllegalStateException("invalid if");
                            }
                        }
                    } else throw new IllegalStateException("invalid if");
                }
                case "pick" -> {
                    if (!stack.isEmpty()) {
                        int n = stack.pop();
                        if (n > 0) {
                            Deque<Integer> tmp = new ArrayDeque<>();
                            int x = 0;
                            while (n > 0) {
                                if (!stack.isEmpty()) {
                                    x = stack.pop();
                                    tmp.push(x);
                                } else {
                                    throw new IllegalStateException("invalid pick");
                                }
                                n--;
                            }
                            while (!tmp.isEmpty()) {
                                stack.push(tmp.pop());
                            }
                            stack.push(x);
                        } else throw new IllegalStateException("invalid pick");
                    } else throw new IllegalStateException("invalid pick");
                }
                case "skip" -> {
                    if (!stack.isEmpty()) {
                        int n = stack.pop();
                        if (n >= 0) {
                            while (n > 0) {
                                if (!queue.isEmpty()) {
                                    queue.poll();
                                } else throw new IllegalStateException("invalid skip");
                                n--;
                            }
                        } else throw new IllegalStateException("invalid skip");
                    } else throw new IllegalStateException("invalid skip");
                }
                case ":" -> {
                    if (queue.isEmpty()) throw new IllegalStateException("invalid definition");
                    String name = queue.poll();
                    Queue<String> def = new LinkedList<>();
                    String next = "";
                    while (!";".equals(next)) {
                        if (queue.isEmpty()) throw new IllegalStateException("invalid definition");
                        next = queue.poll();
                        if (!";".equals(next)) def.add(next);
                    }
                    state.dict.put(name, def);
                }
                default -> {
                    Integer parsed;
                    try {
                        parsed = Integer.parseInt(tok);
                    } catch (NumberFormatException e) {
                        parsed = null;
                    }

                    if (parsed != null) {
                        stack.push(parsed);
                    } else {
                        Map<String, Queue<String>> dict = state.dict;
                        if (dict.containsKey(tok)) {
                            Queue<String> def = new LinkedList<>(dict.get(tok));
                            if (!queue.isEmpty()) {
                                state.returnStack.push(queue);
                            }
                            queue = def;
                        } else {
                            throw new IllegalStateException("unknown token");
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Method to help you play around with program
     */
    public void test() {
        Scanner in = new Scanner(System.in); // Input scanner

        boolean cont = true; // Boolean to track if continue reading input
        while (cont) {
            System.out.print("ScriptPost>> ");

            // Get tokens and add into queue
            String[] tokens = in.nextLine().split("\\s+");
            queue.addAll(Arrays.asList(tokens));

            // Evaluate tokens
            cont = eval();

            // Print output
            System.out.println(stack.peek());
        }
    }

    /**
     * Return the top of the operand stack
     * @return top of stack
     */
    public int pop() {
        assert !stack.isEmpty();
        return stack.pop();
    }

    /**
     * Main method to help you do basic testing
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ScriptPost program = new ScriptPost();

        // Just simple way for you to play around with the language
        program.test();
    }
}
