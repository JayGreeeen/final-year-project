package regex_to_FA;

public interface Node {

	public Node getParent();

	public String getText();

	public void addChild(Node node);
	
	public void setLeftChild(Node node);
	
	public Node getLeftChild();

	public Node getRightChild();
	
	public void setParent(Node node);
	
	public int getChildCount();
	
	public boolean parentSet();
	
	public String toString();
	

	public class ConcatNode implements Node {
		private Node parent;
		private String text;
		private Node leftChild;
		private Node rightChild;
		private int childCount = 0;

		public ConcatNode(Node parent) {
			text = "•";
			this.parent = parent;
		}

		public void addChild(Node node){
			if (leftChild == null){
				leftChild = node;
			} else {
				rightChild = node;
			}
			System.out.println("Added child to concat node: " + node.getText());
			childCount++;
		}

		public Node getLeftChild() {
			return leftChild;
		}

		public Node getRightChild() {
			return rightChild;
		}

		public Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void setParent(Node node) {
			this.parent = node;
		}

		public int getChildCount() {
			return childCount;
		}
		
		public void setLeftChild(Node node) {
			leftChild = node;
		}
		
		public boolean parentSet(){
			if (parent == null){
				return false;
			}
			return true;
		}

		public String toString(){
			return text;
		}

	}
	

	public class StarNode implements Node {

		private Node parent;
		private String text;
		private Node child;
		private int childCount = 0;

		public StarNode(Node parent) {
			text = "*";
			this.parent = parent;
		}
		
		public void addChild(Node node){
			child = node;
			childCount++;
		}

		public void setParent(Node node) {
			this.parent = node;
		}
		
		public Node getChild() {
			return child;
		}

		public Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public Node getLeftChild() {
			return child;
		}

		public Node getRightChild() {
			// * node has no right child
			return null;
		}
		
		public int getChildCount() {
			return childCount;
		}
		
		public void setLeftChild(Node node) {
			child = node;
		}
		
		public boolean parentSet(){
			if (parent == null){
				return false;
			}
			return true;
		}
		
		public String toString(){
			return text;
		}
		
	}


	public class LeafNode implements Node {

		private Node parent;
		private String text;

		public LeafNode(String text, Node parent) {
			this.text = text;
			this.parent = parent;
		}

		public Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}
		
		public void addChild(Node node) { }

		public void setParent(Node node) {
			this.parent = node;
		}
		
		public Node getLeftChild() {
			return null;
		}

		public Node getRightChild() {
			return null;
		}

		public int getChildCount() {
			// leaf nodes do not have children
			return 0;
		}
		
		public void setLeftChild(Node node) {}
		
		public boolean parentSet(){
			if (parent == null){
				return false;
			}
			return true;
		}
		
		public String toString(){
			return text;
		}

	}

	
	public class UnionNode implements Node{

		private Node parent;
		private Node leftChild;
		private Node rightChild;
		private int childCount;
		private String text;
		
		public UnionNode() {
			text = "|";
		}
		
		public Node getParent() {
			return parent;
		}

		public String getText() {
			return text;
		}

		public void addChild(Node node) {
			if (leftChild == null){
				leftChild = node;
			} else{
				rightChild = node;
			}
			childCount++;
		}

		public Node getLeftChild() {
			return leftChild;
		}

		public Node getRightChild() {
			return rightChild;
		}

		public void setParent(Node node) {
			System.out.println("Setting left child of | to " + node);
			parent = node;
		}

		public int getChildCount() {
			return childCount;
		}
		
		public void setLeftChild(Node node) {
			leftChild = node;
		}
		
		public boolean parentSet(){
			if (parent == null){
				return false;
			}
			return true;
		}
		
		public String toString(){
			return text;
		}
		
	}
}
