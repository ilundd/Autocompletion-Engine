import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Represents a DLB Trie that stores a dictionary.
 * 
 * @author Ian Lundberg
 * @version 1.0
 */
public class DLBTrie{

    // stores the root of the Trie
    private Node root;

    /**
     * Represents a Node in a DLB Trie.
     * 
     * @author Ian Lundberg
     * @version 1.0
     */
    private class Node{

        // stores the Character value
        public Character value;
        // stores the Next Node
        public Node next;
        // stores the first Child Node of the next LinkedList
        public Node child;
        // stores the word priority if the node ends a word
        public int priority;

        /**
         * First constructor for Node.
         * 
         * Initializes the Node with a given Character value and 
         * null values for next and child. 
         * 
         * @param value the Character to initialize the Node with
         */
        public Node(Character value){
            this.value = value;
            this.next = null;
            this.child = null;
            this.priority = 0;
        } 

    }

    /** 
     * Default constructor for DLBTrie.
     * 
     * Initializes an empty DLBTrie.
     */
    public DLBTrie() {}

    /**
     * First constructor for DLBTrie.
     * 
     * Initializes a DLBTrie with an array of words.
     * 
     * @param words the words to initialize the Trie with
     */
    public DLBTrie(String[] words)
    { for (String word : words) add(word);  }

    
    /**
     * Gets the total number of words contained in the Trie
     * 
     * @return the number of words in the Trie
     */
    public int size(){
        int size = 0;
        for(Pair p : words()) size++;
        return size;
    }

    /**
     * Checks if the Trie contains a word
     * 
     * Searches the Trie for a given word and returns true if it 
     * it contains the word
     * 
     * @param word the word to search for
     * @return true if the Trie contains the word
     * @return false if the Trie does not contain the word
     */
    public boolean contains(String word) { 
        Node end = search(root, word, 0);
        return  end != null && end.value == '$'; 
    }

    /**
     * Searches the Trie for a given word.
     * 
     * Searches the Trie for a given word and returns the deepest node
     * found.
     * 
     * @param root the starting node 
     * @param word the word to search for
     * @param d the index to start from in the word
     * @return the deepest Node found while searching
     */
    private Node search(Node root, String word, int d){
        char c = (d != word.length()) ? word.charAt(d) : '$';
        if (root == null) return null;
        else if (d == word.length()) return root;
        else if (c != root.value) return search(root.next, word, d); 
        else return search(root.child, word, d+1);
    }

    /**
     * Retrieves the priority value of a given word if the word exists.
     * 
     * @param word the word to get the priority of
     * @return the priority of the word
     */
    public int getPriority(String word){
        Node nd = search(root, word, 0);
        return nd.priority;
    }

    /**
     * Increments the priority value of a given word if it exists.
     * 
     * @param word the word to increase in priority
     */
    public void updatePriority(String word){
        Node nd = search(root, word, 0);
        if(nd.value == '$')
            nd.priority++;
    }

    /**
     * Adds a given word to the Trie.
     * 
     * @param word the word to add 
     */
    public void add(String word)
    { root = add(root, word, 0, 0);  }

    /**
     * Adds a given word to the Trie with a given priority.abstract
     * 
     * @param word the word to add
     * @param priority the priority value of the word
     */
    public void add(String word, int priority)
    { root = add(root, word, 0, priority);  }

    /**
     * Adds a given word to the Trie.
     * 
     * Adds a word to the Trie starting at a given node and 
     * returns the new root node of the Trie.
     * 
     * @param root the root node of the trie being added to
     * @param word the word being added
     * @param d the index to start from in the word
     * @return the root node of the new Trie
     */
    private Node add(Node root, String word, int d, int priority){
        char c = (d != word.length()) ? word.charAt(d) : '$';
        if (root == null) root = new Node(c);
        if (d == word.length()) { 
            if (root.value != '$') { 
                Node buffer = new Node(c);
                buffer.next = root;
                root = buffer;
            }
            root.priority = priority; 
            return root;
        }
        if (c != root.value) root.next = add(root.next, word, d, priority);
        else root.child = add(root.child, word, d+1, priority);
        return root;
    }

    /**
     * Returns an Iterable for the words in the Trie.
     * 
     * @return a String iterable for the trie
     */
    public Iterable<Pair> words()
    { return wordsWithPrefix(""); }

    /**
     * Returns an Iterable for every word in the Trie with a given prefix.
     * 
     * Returns an iterable containing every word in the Trie that 
     * start with a given prefix.
     * 
     * @param prefix the prefix to look for
     * @return a String iterable containing words that starts with the prefix
     */
    public Iterable<Pair> wordsWithPrefix(String prefix){
        LinkedList<Pair> queue = new LinkedList<Pair>();
        collect(search(root, prefix, 0), prefix, queue);
        Collections.sort(queue);
        return queue;
    }

    /**
     * Finds every word in a Trie that starts with a given Prefix.
     * 
     * Searches the Trie, starting at a given node, for every word containing a 
     * given prefix and adds it to a given LinkedList.
     * 
     * @param root the starting point to search from
     * @param prefix the prefix to search for
     * @param queue the queue to add the words to
     */
    private void collect(Node root, String prefix, LinkedList<Pair> queue){
        if (root == null) return;
        if (root.value == '$') queue.add(new Pair(prefix, root.priority));
        for (; root != null; root = root.next)
            collect(root.child, prefix + root.value, queue);
    }
}