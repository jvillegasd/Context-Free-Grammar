/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author LinkRs
 */
public class CFGrammar {
    private Set<String> nonTerminals = null;
    private Set<String> terminals = null; 
    private HashMap<String, Set<String>> first;
    private HashMap<String, Set<String>> follow;
    private HashMap<String, Set<String>> grammarEquations = null;
    private HashMap<String, HashMap<String, String>> mTable;
    private String initialState = "";
    
    public CFGrammar(ArrayList<String> grammarEq){
        this.grammarEquations = new HashMap<>();
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.mTable = new HashMap<>();
        init(grammarEq);
    }
    
    private boolean isNonTerminal(char symbol){
        return symbol >= 'A' && symbol <= 'Z';
    }
    
    private boolean isTerminal(String symbol){
        return terminals.contains(symbol);
    }
    
    private void init(ArrayList<String> grammarEquations){
        for(String grammarEq : grammarEquations){
            String splittedGE[] = grammarEq.split("->");
            String nonTerminal = splittedGE[0];
            String production = splittedGE[1];
            if(initialState.equals("")) initialState = nonTerminal;
            if(first.get(nonTerminal) == null){
                first.put(nonTerminal, new HashSet<>());
                follow.put(nonTerminal, new HashSet<>());
                this.grammarEquations.put(nonTerminal, new HashSet<>());
            }
            this.grammarEquations.get(nonTerminal).add(production);
            nonTerminals.add(nonTerminal);
            checkProduction(production);
        }
    }
    
    private void checkProduction(String production){
        for(int i = 0; i < production.length(); i++){
            char symbol = production.charAt(i);
            String terminal = "";
            while(!isNonTerminal(symbol) && symbol != '&' && i < production.length()){
                terminal+=symbol;
                i++;
                if(i >= production.length()) break;
                symbol = production.charAt(i);
            }
            if(terminal.equals("") && symbol != '&') nonTerminals.add(symbol + "");
            else if(!terminal.equals("")) terminals.add(terminal);
        }
    }
    
    public void getFirst(){
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            if(first.get(nonTerminal).isEmpty()) recFirst(nonTerminal);
        }
    }
    
    private Set<String> recFirst(String nonTerminal){
        for(String production : this.grammarEquations.get(nonTerminal)){
            if(isTerminal(production) || production.equals("&")) first.get(nonTerminal).add(production);
            else first.get(nonTerminal).addAll(recFirst(production));
        } 
        return first.get(nonTerminal);
    }
    
    public void getFollow(){
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            if(doFirstRule(nonTerminal)) follow.get(nonTerminal).add("$");
            for(String production : this.grammarEquations.get(nonTerminal)) doSecondRule(production);
            getFollow(nonTerminal);
        }
    }
    
    private Set<String> getFollow(String nonTerminalP){
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            for(String production : this.grammarEquations.get(nonTerminal)){
                if(production.contains(nonTerminalP)){
                    follow.get(nonTerminalP).addAll(doThirdRule(nonTerminal, production, nonTerminalP));
                }
            }
        }
        return follow.get(nonTerminalP);
    }
    
    private boolean doFirstRule(String nonTerminal){
        return nonTerminal.equals(initialState);
    }
    
    private void doSecondRule(String production){
        if(production.length() == 1) return;
        for(int i = 0; i < production.length(); i++){
            char nonTerminal = production.charAt(i);
            if(isNonTerminal(nonTerminal) && i + 1 < production.length()){
                char betha = production.charAt(i + 1);
                if(isNonTerminal(betha)){
                    Set<String> bethaFirst = first.get(betha + "");
                    bethaFirst.remove("&");
                    follow.get(nonTerminal + "").addAll(bethaFirst);
                } else follow.get(nonTerminal + "").add(betha + ""); 
            }
        }
    }
    
    private Set<String> doThirdRule(String nonTerminal, String production, String nonTerminalP){
        for(int i = 0; i < production.length(); i++){
            String symbol = production.charAt(i) + "";
            if(symbol.equals(nonTerminalP)){
                if(i + 1 < production.length()){
                    char betha = production.charAt(i + 1);
                    Set<String> bethaFirst = first.get(betha + "");
                    if(bethaFirst.contains("&")){
                        if(!follow.get(nonTerminal).isEmpty()){
                            follow.get(nonTerminalP).addAll(follow.get(nonTerminal));
                        } else{
                            follow.get(nonTerminalP).addAll(getFollow(nonTerminal));
                        }
                    }
                } else{
                    if(!follow.get(nonTerminal).isEmpty()){
                        follow.get(nonTerminalP).addAll(follow.get(nonTerminal));
                    } else{
                        follow.get(nonTerminalP).addAll(getFollow(nonTerminal));
                    }
                }
            }
        }
        return follow.get(nonTerminal);
    }
    
    private void print(){
        System.out.println("Terminals:");
        for(String t : terminals) System.out.print(t + "||");
        System.out.println("\nNon Terminals:");
        for(String nt : nonTerminals) System.out.print(nt + "||");
    }
    
    public void printFi(){
        System.out.println("First:");
        for(Map.Entry<String, Set<String>> entry : this.first.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}