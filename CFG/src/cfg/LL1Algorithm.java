/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfg;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Visitante
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
        init();
    }
    
    private void init(){
        for(String nonTerminal : nonTerminals){
            for(String terminal : terminals){
                mTable.put(nonTerminal, new HashMap<>());
            }
        }
    }
    
    public void computeMTable(){
        for(String nonTerminal : nonTerminals){
            for(String terminal : terminals){
                
            }
        }
    }
    
    private boolean isInProduction(String terminal, String nonTerminal){
        boolean contains = false;
        
        return contains;
    }
}
