package FYP.backend;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import regex_to_FA.Print_Tree;
import regex_to_FA.Tree_Builder;
import regex_to_FA.Tree_Node;
import regex_to_FA.Tree_Node.ConcatNode;
import regex_to_FA.Tree_Node.LeafNode;
import regex_to_FA.Tree_Node.StarNode;
import regex_to_FA.Tree_Node.UnionNode;

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

		assertTrue(root instanceof LeafNode);
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

		assertTrue(root instanceof ConcatNode);
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

		assertTrue(root instanceof UnionNode);
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

		assertTrue(root instanceof StarNode);
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

		assertTrue(root instanceof LeafNode);
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

		assertTrue(root instanceof UnionNode);
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

		assertTrue(root instanceof ConcatNode);
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

		assertTrue(root instanceof StarNode);
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

		assertTrue(root instanceof ConcatNode);
		assertTrue(root.getChildCount() == 2);
		assertEquals(label1, root.getRightChild().getText());

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof StarNode);
		assertTrue(leftChild.getChildCount() == 1);

		Tree_Node childOfStar = leftChild.getLeftChild();
		assertTrue(childOfStar instanceof UnionNode);
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

		assertTrue(root instanceof ConcatNode);
		assertTrue(root.getChildCount() == 2);
		assertEquals(label2, root.getRightChild().getText());

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof StarNode);
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

		assertTrue(root instanceof ConcatNode);
		assertTrue(root.getChildCount() == 2);
		assertEquals(label1, root.getRightChild().getText());

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof UnionNode);
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

		assertTrue(root instanceof StarNode);
		assertTrue(root.getChildCount() == 1);

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof ConcatNode);
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

		assertTrue(root instanceof ConcatNode);
		assertTrue(root.getChildCount() == 2);

		Tree_Node leftChild = root.getLeftChild();
		assertTrue(leftChild instanceof StarNode);

		Tree_Node childOfStar = leftChild.getLeftChild();
		assertTrue(childOfStar instanceof ConcatNode);
		assertEquals(label1, childOfStar.getLeftChild().getText());
		assertEquals(label2, childOfStar.getRightChild().getText());

		Tree_Node rightChild = root.getRightChild();
		assertTrue(rightChild instanceof StarNode);
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
		Print_Tree.print(root); 

		String label1 = "a";
		String label2 = "b";
		String label3 = "c";

		assertTrue(root instanceof UnionNode);
		assertTrue(root.getChildCount() == 2);

		// left child of root
		Tree_Node leftChildRoot = root.getLeftChild(); // •
		assertTrue(leftChildRoot instanceof ConcatNode);
		assertTrue(leftChildRoot.getChildCount() == 2);
		assertEquals(label1, leftChildRoot.getLeftChild().getText()); // a

		Tree_Node rightChild = leftChildRoot.getRightChild(); // •
		assertTrue(rightChild instanceof ConcatNode);
		assertTrue(rightChild.getChildCount() == 2);
		assertEquals(label2, rightChild.getLeftChild().getText()); // b

		rightChild = rightChild.getRightChild(); // *
		assertTrue(rightChild instanceof StarNode);
		assertEquals(label3, rightChild.getLeftChild().getText()); // c

		// right child of root
		Tree_Node rightChildRoot = root.getRightChild(); // •
		assertTrue(rightChildRoot instanceof ConcatNode);
		assertTrue(rightChildRoot.getChildCount() == 2);

		Tree_Node leftChild = rightChildRoot.getLeftChild(); // *
		assertTrue(leftChild instanceof StarNode);

		leftChild = leftChild.getLeftChild(); // •
		assertTrue(leftChild instanceof ConcatNode);
		assertTrue(leftChild.getChildCount() == 2);
		assertEquals(label1, leftChild.getLeftChild().getText()); // a
		assertEquals(label2, leftChild.getRightChild().getText()); // b

		rightChild = rightChildRoot.getRightChild(); // *
		assertTrue(rightChild instanceof StarNode);
		assertEquals(label1, rightChild.getLeftChild().getText()); // a
	}

}
