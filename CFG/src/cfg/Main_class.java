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
        cfg.computeSets();
        cfg.printFi();
        cfg.printFo();
        LL1Algorithm ll1 = new LL1Algorithm(cfg);
        ll1.computeMTable();
        ll1.printMTable();
        System.out.println(ll1.acceptedWord("(id,id,n)"));
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
    
    private static void test4(ArrayList<String> ge){ //Gramatica Ambigua
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
    
    private static void test5(ArrayList<String> ge){
        ge.add("S->a");
        ge.add("S->ab");
        ge.add("S->abc");
        ge.add("S->abcd");
        ge.add("S->e");
        ge.add("S->f");
    }
    
    private static void test6(ArrayList<String> ge){
        ge.add("A->BCDEfG");
        ge.add("B->b");
        ge.add("B->&");
        ge.add("C->c");
        ge.add("C->&");
        ge.add("D->d");
        ge.add("D->&");
        ge.add("E->e");
        ge.add("E->&");
        ge.add("G->g");
        ge.add("G->&");
    }
    
    private static void test7(ArrayList<String> ge){
        ge.add("A->BCBDe");
        ge.add("A->z");
        ge.add("B->aBB");
        ge.add("B->aBbB");
        ge.add("B->&");
        ge.add("C->DeF");
        ge.add("C->&");
        ge.add("D->abcd");
        ge.add("F->f");
    }
    
    private static void test8(ArrayList<String> ge){
        ge.add("A->BCeFG");
        ge.add("B->b");
        ge.add("C->c");
        ge.add("C->&");
        ge.add("F->f");
        ge.add("F->&");
        ge.add("G->g");
        ge.add("G->&");
    }
    
    private static void test9(ArrayList<String> ge){
        ge.add("S->xyN");
        ge.add("N->zS");
        ge.add("N->z");
        ge.add("N->&");
    }
}
