package backend;

import regex_to_FA.Regex_to_FA;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main( String[] args ){
    	
    	String regex = "(a|b)*|ab";
    	
    	// aab
    	// a | b
    	// (a|b)
    	// (a|b)a
    	// a*
    	// a*b
    	// (ab)*
    	// (ab)*a
    	// (ab)*a*
    	// (a|b)*
    	
    	Regex_to_FA converter = new Regex_to_FA();
    	converter.convertToFA(regex);
    	
    }
}
