import java.io.*;
import java.util.regex.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Application for minor word autocomplete functionality.
 * 
 * @author Ian Lundberg
 * @version 1.0
 */
public class ac_test{

    public static void main(String[] args) throws Exception{
        
        // stores the dictionary
        DLBTrie dictionary = new DLBTrie();
        // stores the user's history
        LinkedList<Pair> history = new LinkedList<Pair>();
        // stores the predictions
        LinkedList<String> predictions = new LinkedList<String>();
        // stores average prediction time
        LinkedList<Double> avgTime = new LinkedList<Double>();
        // scanner used to read user input
        Scanner sc = new Scanner(System.in);
        // stores user input while typing a word
        String userInput = new String();

        // reads a dictionary file and adds all of the words to the dictionary trie
        if (!loadDictionary(dictionary, new File("dictionary.txt")))
        { System.out.println("A 'dictionary.txt' file does not exist in the directory!"); System.exit(1); }
        // loads the previous user history and adds it to the dictionary
        loadUserHistory(dictionary, history, new File("user_history.txt"));

        // prompts the user for the first character
        System.out.print("Enter your first character: ");
        // prompts the user for new letters and words until '!' is entered
        for (char input = sc.next().charAt(0); input != '!'; input = sc.next().charAt(0)){

            // checks if a command is entered. e.g. $, 1-5
            System.out.println();
            if(!commandCheck(dictionary, history, predictions, input, userInput)){

                // appends user input with the next character
                userInput += input;

                // predicts five words and returns the time it took to predict them
                Double timeToPredict = getPredictions(dictionary, userInput, predictions, 5);
                System.out.format("(%f s)\n", timeToPredict);
                avgTime.add(timeToPredict);

                // prints out the predictions
                System.out.println("Predictions:");
                if(predictions.isEmpty()){
                    System.out.println("\nNo predictions found!\nType '$' when you finish typing your word.");
                } else {
                    for(int i = 0; i < predictions.size(); i++)
                        System.out.format("(%d) %s    ", i+1, predictions.get(i));
                    System.out.println();
                }
                // prompts for the next character
                System.out.print("\nEnter the next character: ");

            // resets the input string for a new word
            } else userInput = new String();
        }
        System.out.format("\nAverage time:  %f s\nBye!\n", getAverage(avgTime));
        // saves user history and closes the input scanner
        saveUserHistory(history, new File("user_history.txt"));
        sc.close();
    }

    /**
     * Populates a LinkedList from a DLBTrie with a given number of autocomplete predictions that
     * begin with a given prefix.
     * 
     * @param words the dictionary to pull words from
     * @param prefix the prefix each prediction must also have
     * @param queue the LinkedList holding the predictions
     * @param num the number of predictions to generate 
     * @return the amount of time, in seconds, it took to generate the predictions
     */
    private static Double getPredictions(DLBTrie words, String prefix, LinkedList<String> queue, int num){
        long startTime = System.nanoTime();
        int count = 0;
        queue.clear();
        for(Pair p : words.wordsWithPrefix(prefix)) { 
            if (count == num) break;
            queue.add(p.getWord());
            count++;
        }
        return ((System.nanoTime() - startTime) / 1000000000.0);
    }
    
    /**
     * Reads a given text file line by line and populates a DLB Trie with the words.
     * 
     * @param trie the DLB Trie to add the words to
     * @param fp the being read from
     */
    private static boolean loadDictionary(DLBTrie trie, File fp) throws Exception{
        
        if (!fp.exists() || fp.isDirectory()) return false;

        BufferedReader br = new BufferedReader(new FileReader(fp));
        for (String word = br.readLine(); word != null; word = br.readLine())
            trie.add(word);

        br.close();
        return true;
    }

    /** 
     * Incremements the priority of a specified word within a specified DLB Trie.
     * 
     * @param tr the Trie to search through
     * @param word the word to increment the priority for
     */
    private static void updateDLB(DLBTrie tr, String word){
        if (tr.contains(word)){
            tr.updatePriority(word);
        } else tr.add(word, 1);
    }

    /** 
     * Updates the words in a given LinkedList that is storing autocomplete user history 
     * with the current priority.
     * 
     * @param history the LinkedList storing the user history
     * @param word the word whose priority is being incremented
     */
    private static void updateHistory(LinkedList<Pair> history, String word){

        boolean notContains = true;
        for(Pair p : history){
            if(p.getWord().equals(word)) 
            { p.setPriority(p.getPriority() + 1); notContains = false; }
        } 
        if(notContains){
            history.add(new Pair(word, 1));
        }
    }

    /** 
     * Checks an input Character for any commands in the command line and proceeds
     * according to their meaning.
     * 
     * @param dictionary the dictionary Trie storing all of the words
     * @param history the LinkedList storing the user's history
     * @param predictions the LinkedList storing the predictions by my the program already
     * @param input the command to search for
     * @param cust_word the word/prefix being searched for
     * @return false if a command is not parsed
     * @return true if a valid command is parsed
     */
    private static boolean commandCheck(DLBTrie dictionary, LinkedList<Pair> history, LinkedList<String> predictions, char input, String cust_word){
        int wordSelect = Character.getNumericValue(input);
        String completeWord = new String();
        if (Character.compare(input, '$') == 0){
            completeWord = cust_word;
            complete(dictionary, history, completeWord);
            return true;
        } else if ((wordSelect >= 1 && wordSelect <= predictions.size()) && predictions.size() >= 1) {
            completeWord = predictions.get(wordSelect-1);
            complete(dictionary, history, completeWord);
            return true;
        }
        return false;
    }

    /**
     * Updates a given DLB Trie and user history LinkedList with new priority values.
     * 
     * 
     * @param dictionary the dictionary holding all words
     * @param history the LinkedList storing all previous words that were search for
     * @param word the word with the priority being updated
     */
    private static void complete(DLBTrie dictionary, LinkedList<Pair> history, String word){
        System.out.format("\nWORD COMPLETED:  %s\n", word);
        updateDLB(dictionary, word);
        updateHistory(history, word);
        System.out.format("\nEnter first character of next word: ");
    }

    /** 
     * Sorts the LinkedList that is storing the user history based on each word's priority
     * and writes it to a text file named 'user_history.txt'.abstract
     * 
     * @param history the LinkedList holding the user history
     * @param fp the filpath to the user_history.txt file
     */
    private static void saveUserHistory(LinkedList<Pair> history, File fp) throws Exception{

        if(!history.isEmpty()){
            fp.createNewFile();
            Collections.sort(history);

            BufferedWriter output = new BufferedWriter(new FileWriter(fp));
            for(Pair p : history){
                output.write(String.format("%s, %d\n", p.getWord(), p.getPriority()));
            }
            output.close();
        }
    }

    /**
     * Reads the 'user_history.txt' file line by line, parses each word and priority, and then adds
     * them to the user history LinkedList and dictionary DLB.
     * 
     * @param dictionary the dlb trie storing all words
     * @param history the LinkedList storing all previous user entries
     * @param fp the filepath to the 'user_historty.txt' file
     * @return false if the file does not exist
     * @return true if the file does exist
     * @throws Exception
     */
    private static boolean loadUserHistory(DLBTrie dictionary, LinkedList<Pair> history, File fp) throws Exception{

        if (!fp.exists() || fp.isDirectory()) return false;

        BufferedReader br = new BufferedReader(new FileReader(fp));

        for(String str = br.readLine(); str != null; str = br.readLine()){
            
            int priority;
            Pattern pat = Pattern.compile("\\d+");
            Matcher mat = pat.matcher(str);
            mat.find();
            priority = Integer.parseInt(mat.group());

            String word;
            word = str.substring(0, str.indexOf(','));

            history.add(new Pair(word, priority));

            dictionary.add(word, priority);
        }
        br.close();
        return true;
    }

    /**
     * Adds up all the values in a given list of Doubles, divides it by its size, and then 
     * returns it.
     * 
     * @param list the list of Double values
     * @return the average of all of the values
     */
    private static Double getAverage(LinkedList<Double> list){
        if (list.size() == 0) return 0.0;
        Double avg = 0.0;
        for (Double dbl : list) avg += dbl;
        return avg / list.size();
    }
}