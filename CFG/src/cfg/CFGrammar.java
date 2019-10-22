package cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author LinkRs
 * Notes:
 * NT: Non Terminal
 * GE: Grammar Equation
 */
public class CFGrammar {
    private Set<String> nonTerminals = null;
    private Set<String> terminals = null; 
    private HashMap<String, Set<String>> first;
    private HashMap<String, Set<String>> follow;
    private HashMap<String, Set<String>> grammarEquations = null;
    private HashMap<String, Set<String>> normalizedGE = null;
    private HashMap<String, HashMap<String, String>> mTableFirstRule = null;
    private String initialState = "";
    
    public CFGrammar(ArrayList<String> grammarEq){
        this.grammarEquations = new HashMap<>();
        this.normalizedGE = new HashMap<>();
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.mTableFirstRule = new HashMap<>();
        init(grammarEq);
    }
    
    private boolean isNonTerminal(char symbol){
        return symbol >= 'A' && symbol <= 'Z';
    }
    
    private boolean isNonTerminal(String symbol){
        return nonTerminals.contains(symbol);
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
            if(this.grammarEquations.get(nonTerminal) == null){
                this.grammarEquations.put(nonTerminal, new HashSet<>());
            }
            this.grammarEquations.get(nonTerminal).add(production);
        }
        normalizeGE();
        for(Map.Entry<String, Set<String>> entry : this.normalizedGE.entrySet()){
            String nonTerminal = entry.getKey();
            if(first.get(nonTerminal) == null){
                first.put(nonTerminal, new HashSet<>());
                follow.put(nonTerminal, new HashSet<>());   
            }
            nonTerminals.add(nonTerminal);
            for(String production : entry.getValue()){
                checkProduction(production);
            }
        }
        for(String nonTerminal : nonTerminals){
            for(String terminal : terminals){
                mTableFirstRule.put(nonTerminal, new HashMap<>());
            }
            mTableFirstRule.put(nonTerminal, new HashMap<>());
        }
    }
    
    private void checkProduction(String production){
        for(int i = 0; i < production.length(); i++){
            char symbolC = production.charAt(i);
            if("'".equals(symbolC + "")) continue;
            if(isNonTerminal(symbolC)){
                String symbol = symbolC + "";
                i++;
                while(i < production.length() && "'".equals(production.charAt(i) + "")){
                    symbol+="'";
                    i++;
                }
                i--;
                nonTerminals.add(symbol);
            }
            else if(symbolC != '&' && !"'".equals(symbolC + "")) terminals.add(symbolC + "");
        }
    }
    
    private void normalizeGE(){
        removeLeftRec();
        leftFactorization();
    }
    
    private void removeLeftRec(){
        for(Map.Entry<String, Set<String>> entry : this.grammarEquations.entrySet()){
            String nonTerminal = entry.getKey();
            String auxNonTerminal = getAuxNonTerminal(nonTerminal);
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
                    this.normalizedGE.get(auxNonTerminal).add(alpha + auxNonTerminal);
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
    
    private void leftFactorization(){
        boolean factorized = false;
        HashMap<String, Set<String>> auxNormalizedGE = (HashMap<String, Set<String>>)this.normalizedGE.clone();
        for(Map.Entry<String, Set<String>> entry : auxNormalizedGE.entrySet()){
            String nonTerminal = entry.getKey();
            Set<String> productionSet = entry.getValue();
            Set<String> prefixSet = getPrefix(productionSet);
            for(String prefix : prefixSet){
                Set<String> samePrefixSet = samePrefix(productionSet, prefix);
                String LCP = longestCommonPrefix(samePrefixSet);
                if(LCP.length() > 0 && samePrefixSet.size() > 1){
                    factorized = true;
                    normalizedGE.get(nonTerminal).removeAll(samePrefixSet);
                    Set<String> newProductions = deletePrefix(prefix, samePrefixSet);
                    String auxNonTerminal = getAuxNonTerminal(nonTerminal);
                    normalizedGE.put(auxNonTerminal, new HashSet<>());
                    normalizedGE.get(auxNonTerminal).addAll(newProductions);
                    normalizedGE.get(nonTerminal).add(prefix + auxNonTerminal);
                }
            }
        }
        if(factorized) leftFactorization();
    }
    
    private Set<String> getPrefix(Set<String> productionSet){
        Set<String> prefixSet = new HashSet<>();
        for(String production : productionSet){
            String prefix = production.charAt(0) + "";
            prefixSet.add(prefix);
        }
        return prefixSet;
    }
    
    private Set<String> samePrefix(Set<String> productionSet, String ogPrefix){
        Set<String> samePrefixSet = new HashSet<>();
        for(String production : productionSet){
            String prefix = production.charAt(0) + "";
            if(prefix.equals(ogPrefix)) samePrefixSet.add(production);
        }
        return samePrefixSet;
    }
    
    private String longestCommonPrefix(Set<String> samePrefixSet) {
        if(samePrefixSet == null || samePrefixSet.isEmpty()) return "";
        String initialProd = samePrefixSet.iterator().next();
        for(int i = 0; i < initialProd.length(); i++){
            char symbol = initialProd.charAt(i);
            for(String production : samePrefixSet){
                if(production.equals(initialProd)) continue;
                if(i == production.length() || production.charAt(i) != symbol){
                    return initialProd.substring(0, i);
                }
            }
        }
        return initialProd;
    }
    
    private Set<String> deletePrefix(String prefix, Set<String> samePrefixSet){
        Set<String> newProductions = new HashSet<>();
        for(String production : samePrefixSet){
            String newProduction = production.substring(prefix.length());
            if(newProduction.isEmpty()) newProduction = "&";
            newProductions.add(newProduction);
        }
        return newProductions;
    }
    
    private String getAuxNonTerminal(String nonTerminal){
        String auxNonTerminal = nonTerminal + "'";
        while(normalizedGE.get(auxNonTerminal) != null) auxNonTerminal+="'";
        return auxNonTerminal;
    }
    
    public void computeSets(){
        computeFirst();
        computeFollow();
    }
    
    private void computeFirst(){
        for(String nonTerminal : this.nonTerminals){
            if(first.get(nonTerminal).isEmpty()) recFirst(nonTerminal, new HashSet<>());
            boolean hasEpsilonOnProd = false;
            for(String production : this.normalizedGE.get(nonTerminal)){
                if(production.contains("&")){
                    hasEpsilonOnProd = true;
                    break;
                }
            }
            if(first.get(nonTerminal).contains("&") && !hasEpsilonOnProd){
                first.get(nonTerminal).remove("&");
            }
        }
    }
    
    private Set<String> recFirst(String nonTerminal, Set<String> lastGESet){
        if(!first.get(nonTerminal).isEmpty()) return first.get(nonTerminal);
        for(String production : this.normalizedGE.get(nonTerminal)){
            if(lastGESet.contains(production)) continue;
            for(int i = 0; i < production.length(); i++){
                char symbolC = production.charAt(i);
                String symbol = symbolC + "";
                if(symbol.equals("'")) continue;
                if(i + 1 < production.length() && "'".equals(production.charAt(i + 1) + "")){
                    i++;
                    while(i < production.length() && "'".equals(production.charAt(i) + "")){
                        symbol+="'";
                        i++;
                    }
                }
                if(isNonTerminal(symbol)){
                    if(!first.get(symbol).isEmpty()){
                        first.get(nonTerminal).addAll(first.get(symbol));
                        for(String terminal : first.get(symbol)){ //M Table First Rule
                            if(terminal.equals("&")) continue;
                            mTableFirstRule.get(nonTerminal).put(terminal, production);
                        }
                        if(!first.get(symbol).contains("&")) break;
                    }else{
                        lastGESet.add(production);
                        Set<String> firstSymbol = recFirst(symbol, lastGESet);
                        first.get(nonTerminal).addAll(firstSymbol);
                        for(String terminal : firstSymbol){ //M Table First Rule
                            if(terminal.equals("&")) continue;
                            mTableFirstRule.get(nonTerminal).put(terminal, production);
                        }
                        if(!firstSymbol.contains("&")) break;
                    }
                } else if(isTerminal(symbolC + "") || symbolC == '&') {
                    first.get(nonTerminal).add(symbolC + "");
                    if(symbolC != '&'){ //M Table First Rule
                        mTableFirstRule.get(nonTerminal).put(symbolC + "", production); 
                    }
                    break;
                }
            }
        }
        return first.get(nonTerminal);
    }
    
    private void computeFollow(){
        for(String nonTerminal : nonTerminals) recFollow(nonTerminal, new HashSet<>());
    }
    
    private Set<String> recFollow(String nonTerminalB, Set<String> lastGE){
        if(doFirstRule(nonTerminalB)) follow.get(nonTerminalB).add("$");
        for(Map.Entry<String, Set<String>> entry : this.normalizedGE.entrySet()){
            String nonTerminal = entry.getKey();
            for(String production : this.normalizedGE.get(nonTerminal)){
                if(lastGE.contains(production)) continue;
                doSecondRule(production);
                if(production.contains(nonTerminalB)){
                    lastGE.add(production); 
                    follow.get(nonTerminalB).addAll(doThirdRule(nonTerminal, production, nonTerminalB, lastGE));
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
            int aux = i;
            char nonTerminalC = production.charAt(i);
            if("'".equals(nonTerminalC + "")) continue;
            String nonTerminal = nonTerminalC + "";
            if(i + 1 < production.length() && "'".equals(production.charAt(i + 1) + "")){
                i++;
                while(i < production.length() && "'".equals(production.charAt(i) + "")){
                    nonTerminal+="'";
                    i++;
                }
            }
            if(isNonTerminal(nonTerminal) && i + 1 < production.length()){
                i++;
                char bethaC = production.charAt(i);
                String betha = bethaC + ""; 
                if(i + 1 < production.length() && "'".equals(production.charAt(i + 1) + "")){
                    i++;
                    while(i < production.length() && "'".equals(production.charAt(i) + "")){
                        betha+="'";
                        i++;
                    }
                }
                if(isNonTerminal(betha)){
                    Set<String> bethaFirst = first.get(betha);
                    if(bethaFirst.contains("&")){
                        bethaFirst.remove("&");
                        follow.get(nonTerminal).addAll(bethaFirst);
                        bethaFirst.add("&");
                    } else follow.get(nonTerminal).addAll(bethaFirst);
                } else follow.get(nonTerminal).add(betha); 
            }
            i = aux;
        }
    }
    
    private Set<String> doThirdRule(String nonTerminal, String production, String nonTerminalB, Set<String> lastGE){
        for(int i = 0; i < production.length(); i++){
            int aux = i;
            String symbol = production.charAt(i) + "";
            if(symbol.equals("'") || isTerminal(symbol)) continue;
            if(i + 1 < production.length() && "'".equals(production.charAt(i + 1) + "")){
                i++;
                while(i < production.length() && "'".equals(production.charAt(i) + "")){
                    symbol+="'";
                    i++;
                }
            }
            if(symbol.equals(nonTerminalB)){
                if(i + 1 < production.length()){ //Third rule - another NT next to current NT
                    i++;
                    int startBetha = i, endBetha = i + 1;
                    char bethaC = production.charAt(i);
                    String betha = bethaC + "";
                    if(i + 1 < production.length() && "'".equals(production.charAt(i + 1) + "")){
                        i++;
                        while(i < production.length() && "'".equals(production.charAt(i) + "")){
                            betha+="'";
                            i++;
                        }
                        endBetha = i;
                    }
                    if(isTerminal(betha)){
                        if(follow.get(nonTerminalB).contains("$") && !nonTerminalB.equals(initialState)){
                            follow.get(nonTerminalB).remove("$");
                        }
                        continue;
                    }
                    Set<String> bethaFirst = first.get(betha);
                    if(bethaFirst.contains("&")){
                        String bethaEpsilon = production.substring(0, startBetha) + production.substring(endBetha);
                        doSecondRule(bethaEpsilon);
                        follow.get(nonTerminalB).addAll(doThirdRule(nonTerminal, bethaEpsilon, nonTerminalB, lastGE));
                        if(follow.get(nonTerminalB).isEmpty()){
                            follow.get(nonTerminalB).addAll(recFollow(nonTerminal, lastGE));
                        }
                    }
                } else { //Third rule - current NT next to Alpha
                    follow.get(nonTerminalB).addAll(recFollow(nonTerminal, lastGE));
                }
            }
            i = aux;
        }
        return follow.get(nonTerminalB);
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public HashMap<String, Set<String>> getFirst() {
        return first;
    }

    public HashMap<String, Set<String>> getFollow() {
        return follow;
    }

    public HashMap<String, Set<String>> getNormalizedGE() {
        return normalizedGE;
    }

    public HashMap<String, HashMap<String, String>> getmTableFirstRule() {
        return mTableFirstRule;
    }

    public String getInitialState() {
        return initialState;
    }
    
    public void print(){
        System.out.println("Terminals:");
        for(String t : terminals) System.out.print(t + "||");
        System.out.println("\nNon Terminals:");
        for(String nt : nonTerminals) System.out.print(nt + "||");
        System.out.println("");
        for(Map.Entry<String, Set<String>> entry : this.normalizedGE.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }
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
