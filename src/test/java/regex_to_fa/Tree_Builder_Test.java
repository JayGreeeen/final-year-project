package regex_to_fa;

import org.junit.Test;

import regex_to_fa.Tree_Builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import toolbox.Print_Tree;
import toolbox.Tree_Node;
import toolbox.Tree_Node.Concat_Node;
import toolbox.Tree_Node.Leaf_Node;
import toolbox.Tree_Node.Star_Node;
import toolbox.Tree_Node.Union_Node;

public class Tree_Builder_Test {
	private Tree_Builder builder;

	@Test
	public void singleLetter() {
		/*-
		 * 	a
		 */
		String regex = "a";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		assertTrue(root instanceof Leaf_Node);
		assertTrue(root.getChildCount() == 0);
	}

	@Test
	public void concatenation() {
		/*-
		 * 		•
		 * 	   / \
		 * 	  a   b
		 */
		String regex = "ab";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String leftChild = "a";
		String rightChild = "b";

		assertTrue(root instanceof Concat_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(leftChild, root.getLeftChild().getText());
		assertEquals(rightChild, root.getRightChild().getText());
	}

	@Test
	public void union() {
		/*-
		 * 		|
		 * 	   / \
		 *    a   b
		 */
		String regex = "a|b";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String leftChild = "a";
		String rightChild = "b";

		assertTrue(root instanceof Union_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(leftChild, root.getLeftChild().getText());
		assertEquals(rightChild, root.getRightChild().getText());
	}

	@Test
	public void star() {
		/*-
		 * 	*
		 * 	|
		 * 	a
		 */
		String regex = "a*";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String leftChild = "a";

		assertTrue(root instanceof Star_Node);
		assertTrue(root.getChildCount() == 1);
		assertEquals(leftChild, root.getLeftChild().getText());
	}

	@Test
	public void singleStringBrackets() {
		/*-
		 * 	a
		 */
		String regex = "(a)";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		assertTrue(root instanceof Leaf_Node);
		assertTrue(root.getChildCount() == 0);
	}

	@Test
	public void unionBrackets() {
		/*-
		 * 		|
		 * 	   / \
		 *    a   b
		 */
		String regex = "(a|b)";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String leftChild = "a";
		String rightChild = "b";

		assertTrue(root instanceof Union_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(leftChild, root.getLeftChild().getText());
		assertEquals(rightChild, root.getRightChild().getText());
	}

	@Test
	public void concatBrackets() {
		/*-
		 * 		•
		 * 	   / \
		 *    a   b
		 */
		String regex = "(ab)";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String leftChild = "a";
		String rightChild = "b";

		assertTrue(root instanceof Concat_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(leftChild, root.getLeftChild().getText());
		assertEquals(rightChild, root.getRightChild().getText());
	}

	@Test
	public void starBrackets() {
		// check that it builds the tree correctly
		/*-
		 * 	  *
		 * 	  |
		 *    a
		 */
		String regex = "(a)*";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String leftChild = "a";

		assertTrue(root instanceof Star_Node);
		assertTrue(root.getChildCount() == 1);
		assertEquals(leftChild, root.getLeftChild().getText());

	}

	@Test
	public void unionStarConcat() {
		/*-
		 * 		•	
		 * 	   / \
		 *    *   a
		 *    |
		 *    |
		 *   / \
		 *  a   b
		 */
		String regex = "(a|b)*a";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String label1 = "a";
		String label2 = "b";

		assertTrue(root instanceof Concat_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(label1, root.getRightChild().getText());

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof Star_Node);
		assertTrue(leftChild.getChildCount() == 1);

		Tree_Node childOfStar = leftChild.getLeftChild();
		assertTrue(childOfStar instanceof Union_Node);
		assertTrue(childOfStar.getChildCount() == 2);
		assertEquals(label1, childOfStar.getLeftChild().getText());
		assertEquals(label2, childOfStar.getRightChild().getText());
	}

	@Test
	public void starConcat() {
		/*-
		 * 		•	
		 * 	   / \
		 *    *   b
		 *    |
		 *    a
		 */
		String regex = "a*b";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String label1 = "a";
		String label2 = "b";

		assertTrue(root instanceof Concat_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(label2, root.getRightChild().getText());

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof Star_Node);
		assertTrue(leftChild.getChildCount() == 1);
		assertEquals(label1, leftChild.getLeftChild().getText());
	}

	@Test
	public void unionConcat() {
		/*-
		 * 		•	
		 * 	   / \
		 *    |   a
		 *   / \
		 *  a   b
		 */
		String regex = "(a|b)a";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String label1 = "a";
		String label2 = "b";

		assertTrue(root instanceof Concat_Node);
		assertTrue(root.getChildCount() == 2);
		assertEquals(label1, root.getRightChild().getText());

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof Union_Node);
		assertTrue(leftChild.getChildCount() == 2);
		assertEquals(label1, leftChild.getLeftChild().getText());
		assertEquals(label2, leftChild.getRightChild().getText());
	}

	@Test
	public void concatStar() {
		/*-
		 * 		*
		 * 		|
		 * 		•	
		 * 	   / \
		 *    a   b
		 */
		String regex = "(ab)*";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String label1 = "a";
		String label2 = "b";

		assertTrue(root instanceof Star_Node);
		assertTrue(root.getChildCount() == 1);

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof Concat_Node);
		assertTrue(leftChild.getChildCount() == 2);
		assertEquals(label1, leftChild.getLeftChild().getText());
		assertEquals(label2, leftChild.getRightChild().getText());
	}

	@Test
	public void concatStarTwice() {
		/*-
		 * 		•	
		 * 	   / \
		 *    *	  *
		 *    |	  |
		 *    •   a
		 *   / \
		 *  a   b
		 */
		String regex = "(ab)*a*";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();

		String label1 = "a";
		String label2 = "b";

		assertTrue(root instanceof Concat_Node);
		assertTrue(root.getChildCount() == 2);

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof Star_Node);

		Tree_Node childOfStar = leftChild.getLeftChild();
		assertTrue(childOfStar instanceof Concat_Node);
		assertEquals(label1, childOfStar.getLeftChild().getText());
		assertEquals(label2, childOfStar.getRightChild().getText());

		Tree_Node rightChild = root.getRightChild();
		assertTrue(rightChild instanceof Star_Node);
		assertEquals(label1, rightChild.getLeftChild().getText());
	}

	@Test
	public void complexString() {
		/*-
		 * 				 |
		 * 			    / \
		 * 			  /	    \
		 * 			/		  \
		 * 		  •			   •
		 * 	    /   \	     /   \
		 * 	   a	 •	    *     *
		 * 		   /  \     |     |
		 * 		  b	   *    •     a
		 * 			   |   / \
		 * 			   c  a   b
		 */
		String regex = "abc* | (ab)*a*";

		builder = new Tree_Builder(regex);
		Tree_Node root = builder.buildTree();
		Print_Tree.printSideways(root); 

		String label1 = "a";
		String label2 = "b";
		String label3 = "c";

		assertTrue(root instanceof Union_Node);
		assertTrue(root.getChildCount() == 2);

		// left child of root
		Tree_Node leftChildRoot = root.getLeftChild(); // •
		assertTrue(leftChildRoot instanceof Concat_Node);
		assertTrue(leftChildRoot.getChildCount() == 2);
		assertEquals(label1, leftChildRoot.getLeftChild().getText()); // a

		Tree_Node rightChild = leftChildRoot.getRightChild(); // •
		assertTrue(rightChild instanceof Concat_Node);
		assertTrue(rightChild.getChildCount() == 2);
		assertEquals(label2, rightChild.getLeftChild().getText()); // b

		rightChild = rightChild.getRightChild(); // *
		assertTrue(rightChild instanceof Star_Node);
		assertEquals(label3, rightChild.getLeftChild().getText()); // c

		// right child of root
		Tree_Node rightChildRoot = root.getRightChild(); // •
		assertTrue(rightChildRoot instanceof Concat_Node);
		assertTrue(rightChildRoot.getChildCount() == 2);

		Tree_Node leftChild = rightChildRoot.getLeftChild(); // *
		assertTrue(leftChild instanceof Star_Node);

		leftChild = leftChild.getLeftChild(); // •
		assertTrue(leftChild instanceof Concat_Node);
		assertTrue(leftChild.getChildCount() == 2);
		assertEquals(label1, leftChild.getLeftChild().getText()); // a
		assertEquals(label2, leftChild.getRightChild().getText()); // b

		rightChild = rightChildRoot.getRightChild(); // *
		assertTrue(rightChild instanceof Star_Node);
		assertEquals(label1, rightChild.getLeftChild().getText()); // a
	}

}
