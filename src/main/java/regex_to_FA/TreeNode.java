package regex_to_FA;

public interface TreeNode {

	public TreeNode getParent();

	public String getText();

	public void addChild(TreeNode node);

	public void setLeftChild(TreeNode node);

	public TreeNode getLeftChild();

	public TreeNode getRightChild();

	public void setParent(TreeNode node);

	public int getChildCount();

	public boolean parentSet();

	public String toString();

	public class ConcatNode implements TreeNode {
		private TreeNode parent;
		private String text;
		private TreeNode leftChild;
		private TreeNode rightChild;
		private int childCount = 0;

		public ConcatNode(TreeNode parent) {
			text = "•";
			this.parent = parent;
		}

		public void addChild(TreeNode node) {
			if (leftChild == null) {
				leftChild = node;
			} else {
				rightChild = node;
			}
			// System.out.println("Added child to concat node: " +
			// node.getText());
			childCount++;
		}

		public TreeNode getLeftChild() {
			return leftChild;
		}

		public TreeNode getRightChild() {
			return rightChild;
		}

		public TreeNode getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void setParent(TreeNode node) {
			this.parent = node;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(TreeNode node) {
			leftChild = node;
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

	public class StarNode implements TreeNode {

		private TreeNode parent;
		private String text;
		private TreeNode child;
		private int childCount = 0;

		public StarNode(TreeNode parent) {
			text = "*";
			this.parent = parent;
		}

		public void addChild(TreeNode node) {
			child = node;
			childCount++;
		}

		public void setParent(TreeNode node) {
			this.parent = node;
		}

		public TreeNode getChild() {
			return child;
		}

		public TreeNode getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public TreeNode getLeftChild() {
			return child;
		}

		public TreeNode getRightChild() {
			// * node has no right child
			return null;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(TreeNode node) {
			child = node;
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

	public class LeafNode implements TreeNode {

		private TreeNode parent;
		private String text;

		public LeafNode(String text) {
			this.text = text;
			// this.parent = parent;
		}

		public TreeNode getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void addChild(TreeNode node) {
		}

		public void setParent(TreeNode node) {
			this.parent = node;
		}

		public TreeNode getLeftChild() {
			return null;
		}

		public TreeNode getRightChild() {
			return null;
		}

		public int getChildCount() {
			// leaf nodes do not have children
			return 0;
		}

		public void setLeftChild(TreeNode node) {
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

	public class UnionNode implements TreeNode {

		private TreeNode parent;
		private TreeNode leftChild;
		private TreeNode rightChild;
		private int childCount;
		private String text;

		public UnionNode() {
			text = "|";
		}

		public TreeNode getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void addChild(TreeNode node) {
			if (leftChild == null) {
				leftChild = node;
			} else {
				rightChild = node;
			}
			childCount++;
		}

		public TreeNode getLeftChild() {
			return leftChild;
		}

		public TreeNode getRightChild() {
			return rightChild;
		}

		public void setParent(TreeNode node) {
			// System.out.println("Setting left child of | to " + node);
			parent = node;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setLeftChild(TreeNode node) {
			leftChild = node;
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
