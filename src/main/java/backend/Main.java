package backend;

import regex_to_FA.Regex_to_FA;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main( String[] args ){
    	
    	String regex = "(ab)*";
    	String regex2 = "(a|b)*";
    	Regex_to_FA converter = new Regex_to_FA();
    	converter.convertToFA(regex2);
    	
    	
//    	Node node4 = new Node.StarNode(null);
//    	Node node1 = new Node.ConcatNode(node4);
//    	Node node2 = new Node.LeafNode("a", node1);
//    	Node node3 = new Node.LeafNode("b", node1);
//    	
//    
//    	node4.addChild(node1);
//    	node1.addChild(node2);
//    	node1.addChild(node3);
//    	
//    	PrintTree.print(node4);
    	
    }
}
