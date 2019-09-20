/**
 * Markov chain, essentially a linked list of MarkovNodes
 */
public class MarkovChain {
    private MarkovNode head;
    private int size;

    public MarkovChain() {
        size = 0;
        head = null;
    }

    public void addMarkovNode(String s) {
        MarkovNode newNode = new MarkovNode(s);
        if (s.equals(".") || s.equals("!") || (s.equals("!"))) newNode.ender = true;
        if (head == null) {
            head = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        if (head == null) return true;
        return false;
    }

    private void moveToFront(String s) {
        if (head == null || head.getWord().equals(s)) {
            return;
        }
        MarkovNode current = head.next;
        MarkovNode previous = head;
        while (current != null) {
            if (current.getWord().equals(s)) {
                previous.next = current.next;
                current.next = head;
                head = current;
            }
            previous = current;
            current = current.next;
        }
    }

    public String getWord(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds");
        }
        MarkovNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.getWord();
    }

    public boolean contains(String s) {
        MarkovNode current = head;
        while (current != null) {
            if (current.getWord().equals(s)) {
                moveToFront(s);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public MarkovNode getNode(String s) {
        MarkovNode current = head;
        while (current != null) {
            if (current.getWord().equals(s)) {
                return current;
            }
            current = current.next;
        }
        throw new IllegalArgumentException("List not contain this word.");
    }

    public String toString() {
        MarkovNode current = head;
        String s = "";
        while (current != null) {
            s += current.getWord() + "[" + current.getChain().size + "] - ";
            for (int i = 0; i < current.getChain().size; i++) {
                s += current.getChain().getWord(i) + " ";
            }
            s += "\n";
            current = current.next;
        }
        return s;
    }

    public String getStarterWord() {
        MarkovChain starterWords = new MarkovChain();
        MarkovNode current = head;
        while (current != null) {
            if (current.starter) {
                starterWords.addMarkovNode(current.getWord());
            }
            current = current.next;
        }

        int index = (int)(Math.random() * starterWords.size);
        return starterWords.getWord(index);
    }
}
