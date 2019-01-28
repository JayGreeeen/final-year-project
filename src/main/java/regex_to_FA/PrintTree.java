package regex_to_FA;

public class PrintTree {

	public static void print(Node node){
		recursivePrint(node, -1);	
		System.out.println("");
	}
	
	private static void recursivePrint(Node node, int counter){
		if (node != null){
			counter++;
			if (node.getRightChild() != null){
				recursivePrint(node.getRightChild(), counter);
			} 
			
			System.out.println("\n");
			for (int i = 0; i < counter; i++){
				System.out.print("\t");
			}
			System.out.print(node.getText());
			
			if (node.getLeftChild() != null){
				recursivePrint(node.getLeftChild(), counter);
			}
		}
	}
}
