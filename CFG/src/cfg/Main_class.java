/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfg;

import java.util.ArrayList;

/**
 *
 * @author LinkRs
 */
public class Main_class {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<String> ge = new ArrayList<>();
        test2(ge);
        CFGrammar cfg = new CFGrammar(ge);
        cfg.getFirst();
        cfg.printFi();
        cfg.getFollow();
        cfg.printFo();
    }
    
    private static void test1(ArrayList<String> ge){
        ge.add("E->A");
        ge.add("E->L");
        ge.add("A->n");
        ge.add("A->id");
        ge.add("L->(S)");
        ge.add("S->EF");
        ge.add("F->,S");
        ge.add("F->&");
    }
    
    private static void test2(ArrayList<String> ge){
        ge.add("S->aAcF");
        ge.add("S->cF");
        ge.add("F->aF");
        ge.add("F->&");
        ge.add("A->baG");
        ge.add("G->bG");
        ge.add("G->&");
    }
}
