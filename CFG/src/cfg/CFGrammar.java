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
    private HashMap<String, Set<String>> normalizedGE = null;
    private HashMap<String, HashMap<String, String>> mTable;
    private String initialState = "";
    
    public CFGrammar(ArrayList<String> grammarEq){
        this.grammarEquations = new HashMap<>();
        this.normalizedGE = new HashMap<>();
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.mTable = new HashMap<>();
        init(grammarEq);
        normalizeGE();
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
            if(isNonTerminal(symbol)) nonTerminals.add(symbol + "");
            else if(symbol != '&') terminals.add(symbol + "");
        }
    }
    
    private void normalizeGE(){
        removeLeftRec();
    }
    
    private void removeLeftRec(){
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            String auxNonTerminal = nonTerminal + "'";
            boolean isRemoved = false;
            this.normalizedGE.put(nonTerminal, new HashSet<>());
            Set<String> bethaSet = new HashSet<>();
            for(String production : entry.getValue()){
                char symbol = production.charAt(0);
                if(isNonTerminal(symbol) && nonTerminal.equals(symbol + "")){
                    isRemoved = true;
                    String alpha = production.substring(1);
                    if(this.normalizedGE.get(auxNonTerminal) == null){
                        this.normalizedGE.put(auxNonTerminal, new HashSet<>());
                    }
                    this.normalizedGE.get(auxNonTerminal).add(alpha);
                }
                if(!production.contains(nonTerminal)) bethaSet.add(production + auxNonTerminal);
            }
            if(!isRemoved){
                this.normalizedGE.get(nonTerminal).addAll(entry.getValue());
            } else {
                this.normalizedGE.get(nonTerminal).addAll(bethaSet);
                this.normalizedGE.get(auxNonTerminal).add("&");
            }
        }
    }
    
    public void getFirst(){
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            System.out.println(nonTerminal + ": " + this.grammarEquations.get(nonTerminal));
            //if(first.get(nonTerminal).isEmpty()) recFirst(nonTerminal, "");
        }
    }
    
    private Set<String> recFirst(String nonTerminal, String lastNonTerminal){
        if(!first.get(nonTerminal).isEmpty()) return first.get(nonTerminal);
        for(String production : this.grammarEquations.get(nonTerminal)){
            char symbol = production.charAt(0);
            if(isNonTerminal(symbol) && !lastNonTerminal.equals(symbol + "")){
                if(!first.get(symbol + "").isEmpty()){
                    first.get(nonTerminal).addAll(first.get(symbol + ""));
                }else{
                    first.get(nonTerminal).addAll(recFirst(symbol + "", nonTerminal));
                }
            } else if(!isNonTerminal(symbol)) {
                first.get(nonTerminal).add(symbol + "");
            }
        }
        return first.get(nonTerminal);
    }
    
    public void getFollow(){
        for(String nonTerminal : nonTerminals) getFollow(nonTerminal, "");
    }
    
    private Set<String> getFollow(String nonTerminalB, String lastNonTerminal){
        if(!follow.get(nonTerminalB).isEmpty()) return follow.get(nonTerminalB);
        if(doFirstRule(nonTerminalB)) follow.get(nonTerminalB).add("$");
        for(String production : this.grammarEquations.get(nonTerminalB)) doSecondRule(production);
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            if(nonTerminal.equals(lastNonTerminal)) continue;
            for(String production : entry.getValue()){
                if(production.contains(nonTerminalB)){
                    follow.get(nonTerminalB).addAll(doThirdRule(nonTerminal, production, nonTerminalB));
                }
            }
        }
        return follow.get(nonTerminalB);
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
    
    private Set<String> doThirdRule(String nonTerminal, String production, String nonTerminalB){
        if(!follow.get(nonTerminal).isEmpty()) return follow.get(nonTerminal);
        for(int i = 0; i < production.length(); i++){
            String symbol = production.charAt(i) + "";
            if(symbol.equals(nonTerminalB)){
                if(i + 1 < production.length()){
                    char betha = production.charAt(i + 1);
                    if(isTerminal(betha + "")){
                        follow.get(nonTerminalB).add(betha + "");
                        continue;
                    }
                    Set<String> bethaFirst = first.get(betha + "");
                    if(bethaFirst.contains("&")){
                        if(!follow.get(nonTerminal).isEmpty()){
                            follow.get(nonTerminalB).addAll(follow.get(nonTerminal));
                        } else{
                            follow.get(nonTerminalB).addAll(getFollow(nonTerminal, nonTerminalB));
                        }
                    }
                } else{
                    if(!follow.get(nonTerminal).isEmpty()){
                        follow.get(nonTerminalB).addAll(follow.get(nonTerminal));
                    } else{
                        follow.get(nonTerminalB).addAll(getFollow(nonTerminal, nonTerminalB));
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
    
    public void printFo(){
        System.out.println("Follow:");
        for(Map.Entry<String, Set<String>> entry : this.follow.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}