package regex_to_FA;

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

	public class ConcatNode implements Tree_Node {
		private Tree_Node parent;
		private String text;
		private Tree_Node leftChild;
		private Tree_Node rightChild;
		private int childCount = 0;

		public ConcatNode(Tree_Node parent) {
			text = "•";
			this.parent = parent;
		}

		public void addChild(Tree_Node node) {
			if (leftChild == null) {
				leftChild = node;
			} else {
				rightChild = node;
			}
			// System.out.println("Added child to concat node: " +
			// node.getText());
			childCount++;
		}

		public Tree_Node getLeftChild() {
			return leftChild;
		}

		public Tree_Node getRightChild() {
			return rightChild;
		}

		public Tree_Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void setParent(Tree_Node node) {
			this.parent = node;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(Tree_Node node) {}

		public void replaceChild(Tree_Node oldNode, Tree_Node newNode){
			if (oldNode == leftChild){
				leftChild = newNode;
			} else if (oldNode == rightChild){
				rightChild = newNode;
			}
		}
		
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

	public class StarNode implements Tree_Node {

		private Tree_Node parent;
		private String text;
		private Tree_Node child;
		private int childCount = 0;

		public StarNode(Tree_Node parent) {
			text = "*";
			this.parent = parent;
		}

		public void addChild(Tree_Node node) {
			child = node;
			childCount++;
		}

		public void setParent(Tree_Node node) {
			this.parent = node;
		}

		public Tree_Node getChild() {
			return child;
		}

		public Tree_Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public Tree_Node getLeftChild() {
			return child;
		}

		public Tree_Node getRightChild() {
			// * node has no right child
			return null;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(Tree_Node node) {
			child = node;
		}
		
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode){
			if (oldNode == child){
				child = newNode;
			}
		}
		
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

	public class LeafNode implements Tree_Node {

		private Tree_Node parent;
		private String text;

		public LeafNode(String text) {
			this.text = text;
			// this.parent = parent;
		}

		public Tree_Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void addChild(Tree_Node node) {
		}

		public void setParent(Tree_Node node) {
			this.parent = node;
		}

		public Tree_Node getLeftChild() {
			return null;
		}

		public Tree_Node getRightChild() {
			return null;
		}

		public int getChildCount() {
			// leaf nodes do not have children
			return 0;
		}

		public void setLeftChild(Tree_Node node) {
		}

		
		
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode){}
		
		
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

	public class UnionNode implements Tree_Node {

		private Tree_Node parent;
		private Tree_Node leftChild;
		private Tree_Node rightChild;
		private int childCount;
		private String text;

		public UnionNode() {
			text = "|";
		}

		public Tree_Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void addChild(Tree_Node node) {
			if (leftChild == null) {
				leftChild = node;
			} else {
				rightChild = node;
			}
			childCount++;
		}

		public Tree_Node getLeftChild() {
			return leftChild;
		}

		public Tree_Node getRightChild() {
			return rightChild;
		}

		public void setParent(Tree_Node node) {
			// System.out.println("Setting left child of | to " + node);
			parent = node;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(Tree_Node node) {
			leftChild = node;
		}
		
		public void replaceChild(Tree_Node oldNode, Tree_Node newNode){
			if (oldNode == leftChild){
				leftChild = newNode;
			} else if (oldNode == rightChild){
				rightChild = newNode;
			}
		}

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
