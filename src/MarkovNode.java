import java.util.ArrayList;

/**
 * Node in Markov Chain, each contains its own chain
 */
public class MarkovNode {
    private String word;
    private MarkovChain nextWords;
    boolean starter;
    boolean ender;
    public MarkovNode next;

    public MarkovNode(String word) {
        next = null;
        starter = false;
        ender = false;
        this.word = word;
        nextWords = new MarkovChain();
    }

    public void addToChain(String s) {
        nextWords.addMarkovNode(s);
    }

    public boolean hasWord(String s) {
        if (word.equals(s)) return true;
        return false;
    }

    public String getWord() {
        return word;
    }

    public MarkovChain getChain() {
        return nextWords;
    }
}