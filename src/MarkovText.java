import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Markov Text Generator
 * @author Kathryn Liang
 */
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

        while (true) {
            System.out.print("Number of sentences to generate (type non-digit or 0 to quit): ");
            try {
                sentenceCount = userInput.nextInt();
            } catch (InputMismatchException e) {
                break;
            }

            if (sentenceCount == 0) break;

            System.out.println();
            if (chain != null && chain.isEmpty()) {
                String testOutputPath = "Debug-Output/MarkovChainContents.txt";
                PrintStream out = makeOutputPrintStream(testOutputPath);
                read(makeFileScanner(trainingPath));
                out.println(chain.toString());
                out.close();
            }
            for (int i = 0; i < sentenceCount; i++) {
                System.out.println(generateSentence());
            }
            System.out.println();
        }
        userInput.close();
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
            line += fileScanner.nextLine() + " ";
        }
        createMarkovChain(line);
    }

    public String generateSentence() {
        boolean midSentence = false;
        int wordCount = 0;
        int minWords = (int)(Math.random() * 10 + 3);
        String sentence = "";
        String word = "";
        MarkovNode currentNode = null;

        while (true) {
            int index = 0;

            //gets a starting word if starting a new sentence
            if (!midSentence) {
                word = chain.getStarterWord();
                currentNode = chain.getNode(word);
                sentence += word + " ";
                midSentence = true;
            }

            //randomly change word sometimes
            if (Math.random() * 10 < 0.5f) {
                index = (int)(Math.random() * chain.size());
                word = chain.getWord(index);
                currentNode = chain.getNode(word);
            }

            //gets a random word from the chain in the MarkovNode
            index = (int)(Math.random() * currentNode.getChain().size());
            if (index > currentNode.getChain().size()) index--;
            word = currentNode.getChain().getWord(index);
            currentNode = chain.getNode(word);

            //ensure sentence is not too short
            while (wordCount < minWords && currentNode.ender) {
                index = (int)Math.floor(Math.random() * chain.size());
                word = chain.getWord(index);
                currentNode = chain.getNode(word);
            }

            //special instructions for special characters
            if (word.equals(".") || word.equals("!") || word.equals(",") || word.equals("?") || word.equals(";") || word.equals(":")) {
                if (sentence.charAt(sentence.length() - 1) == ' ') {
                    sentence = sentence.substring(0, sentence.length() - 1);
                }
                sentence += word + " ";

                //returns the sentence if it is ended
                if (!word.equals(",") && !word.equals(";") && !word.equals(":")) return sentence;
            } else if (word.equals("-") || word.equals("/")) {
                if (sentence.charAt(sentence.length() - 1) == ' ') {
                    sentence = sentence.substring(0, sentence.length() - 1);
                }
                sentence += word;
            } else {
                sentence += word + " ";
            }
            wordCount++;
        }
    }

    private void createMarkovChain(String line) {
        String word = "";
        String digits = "";
        boolean lookForDigits = false;
        MarkovNode previous = null;

        for (int i = 0; i < line.length(); i++) {
            char a = line.charAt(i);

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
                if (!word.equals("")) {
                    //add completed word
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

                    //add special characters
                    if (a == '"' || a == '“' || a == '’' || a == ')' || a == '(') continue;
                    if (a != ' ') {
                        if (!chain.contains(Character.toString(a))) {
                            chain.addMarkovNode(Character.toString(a));
                        }
                        previous.addToChain(Character.toString(a));
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
