package cfg;

import java.util.HashMap;
import java.util.Set;

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
