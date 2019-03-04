package toolbox;

public class Print_Tree {

	public static void printSideways(Tree_Node node) {
		recursivePrint(node, -1);
		System.out.println("");
	}

	private static void recursivePrint(Tree_Node node, int counter) {
		if (node != null) {
			counter++;
			if (node.getRightChild() != null) {
				recursivePrint(node.getRightChild(), counter);
			}

			System.out.println("\n");
			for (int i = 0; i < counter; i++) {
				System.out.print("\t");
			}
			System.out.print(node.getText());

			if (node.getLeftChild() != null) {
				recursivePrint(node.getLeftChild(), counter);
			}
		}
	}

	public static void print(Tree_Node node) {
		printNode(node, 5);
		printTree(node, 5);
	}
	
	private static void printNode(Tree_Node root, int indentation){
		for (int i = 0; i < indentation; i++) {
			System.out.print("\t");
		}
		System.out.println(root.getText());
	}

	private static void printTree(Tree_Node node, int indentation) {
		Tree_Node left = node.getLeftChild();
		Tree_Node right = node.getRightChild();
		
		System.out.println();
		int indent = indentation;
		if (left != null) {
			if (right != null){
				indent--;
			}
			printNode(left, indent);
			if (right != null) {
				printNode(right, indentation + 1);
			}
		}
		if (left != null) {
			printTree(left, indent);
			if (right != null) {
				printTree(right, indentation + 1);
			}
		}

	}

}
