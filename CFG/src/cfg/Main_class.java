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
        ge.add("E->A");
        ge.add("E->L");
        ge.add("A->n");
        ge.add("A->id");
        ge.add("L->(S)");
        ge.add("S->EF");
        ge.add("F->,S");
        ge.add("F->&");
        CFGrammar cfg = new CFGrammar(ge);
        cfg.getFirst();
        cfg.printFi();
    }
    
}
