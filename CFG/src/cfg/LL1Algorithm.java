package cfg;

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author LinkRs
 */
public class LL1Algorithm {
    
    private HashMap<String, Set<String>> normalizedGE = null;
    private HashMap<String, Set<String>> first = null;
    private HashMap<String, Set<String>> follow = null;
    private Set<String> nonTerminals = null;
    private Set<String> terminals = null;
    private HashMap<String, HashMap<String, String>> mTable = null;
    
    public LL1Algorithm(CFGrammar cfg){
        this.normalizedGE = cfg.getNormalizedGE();
        this.first = cfg.getFirst();
        this.follow = cfg.getFollow();
        this.nonTerminals = cfg.getNonTerminals();
        this.terminals = cfg.getTerminals();
        this.mTable = cfg.getmTableFirstRule();
    }
    
    public void computeMTable(){
        for(String nonTerminal : nonTerminals){ //M Table Second Rule
            Set<String> firstNT = first.get(nonTerminal);
            if(firstNT.contains("&")){
                Set<String> followNT = follow.get(nonTerminal);
                for(String terminal : followNT){
                    mTable.get(nonTerminal).put(terminal, "&");
                }
                if(followNT.contains("$")){
                    mTable.get(nonTerminal).put("$", "&");
                }
            }
        }
    }
    
    public boolean acceptedWord(String word){
        boolean accepted = false;
        Stack<String> stack = new Stack<>();
        stack.push("$");
        word+="$";
        String ae = word.charAt(0) + "";
        int idx = 0;
        do {
            String X = stack.peek();
            if(isTerminal(X) || X.equals("$")){
                if(X.equals(ae)){
                    stack.pop();
                    idx++;
                } else {
                    System.out.println("Error!");
                }
            } else {
                if(produce(X, ae)){
                    String production = mTable.get(X).get(ae);
                    String swappedProd = swapProduction(production);
                    pushOnStack(stack, swappedProd);
                } else {
                    System.out.println("Error!");
                }
            }
            idx++;
            if(idx < word.length()) ae = word.charAt(idx) + "";
        } while(!stack.isEmpty() && idx < word.length() && stack.peek().equals("$"));
        if(stack.peek().equals("$")) accepted = true;
        return accepted;
    }
    
    private boolean isTerminal(String symbol){
        return terminals.contains(symbol);
    }
    
    private boolean produce(String nonTerminal, String terminal){
        return mTable.get(nonTerminal).get(terminal) != null;
    }
    
    private String swapProduction(String production){
        String swappedProd = "";
        return swappedProd;
    }
    
    private void pushOnStack(Stack<String> stack, String swappedProd){
        for(int i = swappedProd.length() - 1; i >= 0; i--){
            String symbol = swappedProd.charAt(i) + "";
            stack.add(symbol);
        }
    }
    
    public void printMTable(){
        System.out.println("M Table:");
        for(String nonTerminal : nonTerminals){
            System.out.println(nonTerminal + ":");
            for(String terminal : terminals){
                if(mTable.get(nonTerminal).get(terminal) == null){
                    continue;
                }
                System.out.println("terminal " + terminal + ": " + mTable.get(nonTerminal).get(terminal));
            }
            if(mTable.get(nonTerminal).get("$") != null){
                System.out.println("terminal $: " + mTable.get(nonTerminal).get("$"));
            }
            System.out.println("");
        }
    }
}
