import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;

public class MarkovText {

    private MarkovChain chain;
    private int sentenceCount = 2;

    public MarkovText() {
        chain = new MarkovChain();
        simpleUI();
    }

    private void simpleUI() {
        Scanner userInput = new Scanner(System.in);
        System.out.println("**Markov Text Generator**\n");
        System.out.print("Please enter training file path: ");
        String trainingPath = userInput.nextLine();
        System.out.println("Number of sentences to generate: ");
        try {
            sentenceCount = userInput.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Not an number, set to default (2)");
            sentenceCount = 2;
        }

        String testOutputPath = "Test/MarkovChainContents.txt";
        PrintStream out = makeOutputPrintStream(testOutputPath);
        read(makeFileScanner(trainingPath));
        out.println(chain.toString());
        for (int i = 0; i < sentenceCount; i++) {
            System.out.println(generateSentence());
        }

    }

    private Scanner makeFileScanner(String path) {
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return fileScanner;
    }

    private PrintStream makeOutputPrintStream(String path) {
        PrintStream outstream = null;

        try {
            File outfile = new File(path);
            outfile.getParentFile().mkdirs();
            outstream = new PrintStream(outfile);
        } catch (FileNotFoundException e) {
            System.out.println("File cannot be written to.\n");
            System.exit(1);
        }

        return outstream;
    }

    private void read(Scanner fileScanner) {
        String line = "";
        while (fileScanner.hasNextLine()) {
            line += fileScanner.nextLine();
        }
        createMarkovChain(line);
    }

    public String generateSentence() {
        boolean midSentence = false;
        String sentence = "";
        String word = "";
        MarkovNode currentNode = null;

        while (true) {
            if (!midSentence) {
                word = chain.getStarterWord();
                currentNode = chain.getNode(word);
                sentence += word + " ";
                midSentence = true;
                continue;
            }
            if (Math.random() * 10 < 1f) {
                int index = (int)Math.floor(Math.random() * chain.size);
                word = chain.getWord(index);
                currentNode = chain.getNode(word);
                sentence += word + " ";
            }
            int index = (int)Math.random() * currentNode.getChain().size;
            if (index > currentNode.getChain().size) index--;
            word = currentNode.getChain().getWord(index);
            currentNode = chain.getNode(word);

            if (word.equals(".") || word.equals("!") || word.equals(",") || word.equals("?")) {
                sentence = sentence.substring(0, sentence.length() - 1);
                sentence += word + " ";
                if (!word.equals(",")) return sentence;
            } else if (word.equals("-") || word.equals("/")) {
                sentence = sentence.substring(0, sentence.length() - 1);
                sentence += word;
            } else {
                sentence += word + " ";
            }
        }
    }

    private void createMarkovChain(String line) {
        String word = "";
        String digits = "";
        boolean lookForDigits = false;
        MarkovNode previous = null;

        for (int i = 0; i < line.length(); i++) {
            char a = line.charAt(i);

            if (a == '"' || a == '“') continue;

            //build digits by character
            if (Character.isDigit(a) || a == '$' && !lookForDigits) {
                lookForDigits = true;
                digits += "" + a;
                continue;
            }
            if (Character.isDigit(a) || a == '$' && lookForDigits) {
                digits += "" + a;
                continue;
            }
            if (!Character.isDigit(a) && lookForDigits) {
                lookForDigits = false;
                if (!chain.contains(digits)) {
                    chain.addMarkovNode(digits);
                }
                if (previous != null) {
                    if (previous.getWord().equals(".")) chain.getNode(digits).starter = true;
                    previous.addToChain(digits);
                }
                previous = chain.getNode(digits);
                digits = "";
            }

            //build words by character
            if (!Character.isLetter(a) && a != '\'' || i == line.length() - 1) {
                if (word.equals("")) {
                    if (a != ' ') {
                        if (!chain.contains(Character.toString(a))) {
                            chain.addMarkovNode(Character.toString(a));
                        }
                        if (previous != null) previous.addToChain(Character.toString(a));
                        previous = chain.getNode(Character.toString(a));
                    }
                } else {
                    word = word.toLowerCase();
                    if (!chain.contains(word)) {
                        chain.addMarkovNode(word);
                    }
                    if (previous != null) {
                        if (previous.getWord().equals(".")) chain.getNode(word).starter = true;
                        previous.addToChain(word);
                    }
                    previous = chain.getNode(word);
                    word = "";

                    if (a != ' ') {
                        if (!chain.contains(Character.toString(a))) {
                            chain.addMarkovNode(Character.toString(a));
                        }
                        if (previous != null) previous.addToChain(Character.toString(a));
                        previous = chain.getNode(Character.toString(a));
                    }
                }
            } else {
                word += "" + a;
            }
        }
    }

    public static void main(String[] args) {
        new MarkovText();
    }
}
