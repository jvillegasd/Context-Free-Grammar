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
        test1(ge);
        CFGrammar cfg = new CFGrammar(ge);
        cfg.print();
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
        ge.add("S->E,S");
        ge.add("S->E");
    }
    
    private static void test2(ArrayList<String> ge){
        ge.add("S->Sa");
        ge.add("S->aAc");
        ge.add("S->c");
        ge.add("A->Ab");
        ge.add("A->ba");
    }
    
    private static void test3(ArrayList<String> ge){
        ge.add("E->E+T");
        ge.add("E->T");
        ge.add("T->T*F");
        ge.add("T->F");
        ge.add("F->i");
        ge.add("F->(E)"); 
    }
    
    private static void test4(ArrayList<String> ge){
        ge.add("E->E+T");
        ge.add("E->E-T");
        ge.add("E->T");
        ge.add("T->T*F");
        ge.add("T->T/F");
        ge.add("T->F");
        ge.add("F->(E)");
        ge.add("F->i");
        ge.add("F->E");
    }
}