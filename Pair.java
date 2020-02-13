public class Pair implements Comparable<Pair>{
    private String word;
    private int priority;

    public Pair(String word, int priority){
        this.word = word;
        this.priority = priority;
    }

    @Override
    public int compareTo(Pair p){
        int comparedPriority = p.priority;
        if(this.priority > comparedPriority) return -1;
        else if (this.priority == comparedPriority) return 0;
        else return 1;
    }
    
    public String getWord(){
        return this.word;
    }

    public int getPriority(){
        return this.priority;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public void setWord(String word){
        this.word = word;
    }
}