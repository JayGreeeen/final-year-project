package toolbox;

/**
 * Interface for a node within a binary tree
 * 
 * @author Jaydene Green-Stevens
 *
 */
public interface Tree_Node {

	public Tree_Node getParent();

	public String getText();

	public void addChild(Tree_Node node);

	public void setLeftChild(Tree_Node node);

	public void replaceChild(Tree_Node oldNode, Tree_Node newNode);

	public Tree_Node getLeftChild();

	public Tree_Node getRightChild();

	public void setParent(Tree_Node node);

	public int getChildCount();

	public boolean parentSet();

	public String toString();

	/**
	 * Implementation of a Tree_Node. Represents the concatenation operator with
	 * the symbol •
	 * 
	 * @author Jaydene Green-Stevens
	 *
	 */
	public class Concat_Node implements Tree_Node {
		private Tree_Node parent;
		private String text;
		private Tree_Node leftChild;
		private Tree_Node rightChild;
		private int childCount = 0;

		public Concat_Node(Tree_Node parent) {
			text = "•";
			this.parent = parent;
		}

		/**
		 * Adds a child node to the concat node
		 */
		public void addChild(Tree_Node node) {
			if (leftChild == null) {
				leftChild = node;
			} else {
				rightChild = node;
			}
			childCount++;
		}

		/**
		 * Returns the left child node
		 */
		public Tree_Node getLeftChild() {
			return leftChild;
		}

		/**
		 * Returns the right child node
		 */
		public Tree_Node getRightChild() {
			return rightChild;
		}

		/**
		 * Returns the parent node. Could be null if the node is the root of the
		 * tree.
		 */
		public Tree_Node getParent() {
			return parent;
		}

		/**
		 * Returns the text inside the concat node - •
		 */
		public String getText() {
			return text;
		}

		/**
		 * Sets the parent node
		 */
		public void setParent(Tree_Node node) {
			this.parent = node;
		}

		/**
		 * Returns the number of child nodes the node has
		 */
		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(Tree_Node node) {
		}

		/**
		 * Replaces an existing child of the node with a new one
		 */
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode) {
			if (oldNode == leftChild) {
				leftChild = newNode;
			} else if (oldNode == rightChild) {
				rightChild = newNode;
			}
		}

		/**
		 * Checks to see if the node has a parent node, i.e. is it the root of
		 * the tree
		 */
		public boolean parentSet() {
			if (parent == null) {
				return false;
			}
			return true;
		}

		public String toString() {
			return text;
		}

	}

	/**
	 * Implementation of the Tree_Node. Represents star node with symbol *. Can
	 * only have a single child node.
	 * 
	 * @author Jaydene Green-Stevens
	 *
	 */
	public class Star_Node implements Tree_Node {

		private Tree_Node parent;
		private String text;
		private Tree_Node child;
		private int childCount = 0;

		public Star_Node(Tree_Node parent) {
			text = "*";
			this.parent = parent;
		}

		// Adds a child node to the tree
		public void addChild(Tree_Node node) {
			child = node;
			childCount++;
		}

		/**
		 * Sets the parent node
		 */
		public void setParent(Tree_Node node) {
			this.parent = node;
		}

		/**
		 * @return the child node
		 */
		public Tree_Node getChild() {
			return child;
		}

		/**
		 * Returns the parent node
		 */
		public Tree_Node getParent() {
			return parent;
		}

		/**
		 * Returns the text inside the node; *
		 */
		public String getText() {
			return text;
		}

		/**
		 * Returns the left child of the node
		 */
		public Tree_Node getLeftChild() {
			return child;
		}

		/**
		 * Star nodes cannot have a right child, so will return null
		 */
		public Tree_Node getRightChild() {
			return null;
		}

		/**
		 * Returns the number of child nodes the node has. Maximum should be 1
		 */
		public int getChildCount() {
			return childCount;
		}

		/**
		 * Sets the left child of the node
		 */
		public void setLeftChild(Tree_Node node) {
			child = node;
		}

		/**
		 * Replaces an existing child with a new one
		 */
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode) {
			if (oldNode == child) {
				child = newNode;
			}
		}

		/**
		 * Checks to see whether the node has a parent node, i.e. whether or not
		 * it is the root of the tree
		 */
		public boolean parentSet() {
			if (parent == null) {
				return false;
			}
			return true;
		}

		public String toString() {
			return text;
		}

	}

	/**
	 * Implementation of the Tree_Node. Represents leaf node which is a
	 * childless node, containing a symbol of the input alphabet.
	 * 
	 * @author Jaydene Green-Stevens
	 *
	 */
	public class Leaf_Node implements Tree_Node {

		private Tree_Node parent;
		private String text;

		public Leaf_Node(String text) {
			this.text = text;
		}

		/**
		 * Returns the parent of the node
		 */
		public Tree_Node getParent() {
			return parent;
		}

		/**
		 * Returns the text inside the node - symbol of the input alphabet
		 */
		public String getText() {
			return text;
		}

		/**
		 * Leaf nodes cannot have children. This method has no functionality
		 */
		public void addChild(Tree_Node node) {
		}

		/**
		 * Sets the parent of the leaf node
		 */
		public void setParent(Tree_Node node) {
			this.parent = node;
		}

		/**
		 * Leaf node has no left child, returns null for this method
		 */
		public Tree_Node getLeftChild() {
			return null;
		}

		/**
		 * Leaf node has no right child, returns null for this method
		 */
		public Tree_Node getRightChild() {
			return null;
		}

		/**
		 * Leaf node has no children, returns 0 for this method
		 */
		public int getChildCount() {
			return 0;
		}

		/**
		 * Leaf nodes cannot have children. This method has no functionality
		 */
		public void setLeftChild(Tree_Node node) {
		}

		/**
		 * Leaf nodes cannot have children. This method has no functionality
		 */
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode) {
		}

		/**
		 * Checks to see if the leaf node has a parent node, i.e. is it the root
		 * of the tree
		 */
		public boolean parentSet() {
			if (parent == null) {
				return false;
			}
			return true;
		}

		public String toString() {
			return text;
		}

	}

	/**
	 * Implementation of the Tree_Node. Represents union node which contains the
	 * symbol |.
	 * 
	 * @author Jaydene Green-Stevens
	 *
	 */
	public class Union_Node implements Tree_Node {

		private Tree_Node parent;
		private Tree_Node leftChild;
		private Tree_Node rightChild;
		private int childCount;
		private String text;

		public Union_Node() {
			text = "|";
		}

		/**
		 * Returns the parent node of the union node
		 */
		public Tree_Node getParent() {
			return parent;
		}

		/**
		 * Returns the text of the union node - |
		 */
		public String getText() {
			return text;
		}

		/**
		 * Adds a child to the node
		 */
		public void addChild(Tree_Node node) {
			if (leftChild == null) {
				leftChild = node;
			} else {
				rightChild = node;
			}
			childCount++;
		}

		/**
		 * Returns the left child of the node
		 */
		public Tree_Node getLeftChild() {
			return leftChild;
		}

		/**
		 * Returns the right child of the node
		 */
		public Tree_Node getRightChild() {
			return rightChild;
		}

		/**
		 * Sets the nodes parent
		 */
		public void setParent(Tree_Node node) {
			parent = node;
		}

		/**
		 * Returns the number of child nodes the node has
		 */
		public int getChildCount() {
			return childCount;
		}

		/**
		 * Sets the value of the left child node
		 */
		public void setLeftChild(Tree_Node node) {
			leftChild = node;
		}

		/**
		 * Replaces an existing child node with a new node
		 */
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode) {
			if (oldNode == leftChild) {
				leftChild = newNode;
			} else if (oldNode == rightChild) {
				rightChild = newNode;
			}
		}

		/**
		 * Checks the see if the node has a parent, i.e. whether or not it is
		 * the root node
		 */
		public boolean parentSet() {
			if (parent == null) {
				return false;
			}
			return true;
		}

		public String toString() {
			return text;
		}

	}
}
