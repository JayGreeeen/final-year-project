package regex_to_FA;

public class TransitionTable {

	private String[][] transitions;
	
	public TransitionTable(int size){
		transitions = new String[size][size];
	}
	
	// 2 dimensional array 
		/*-
		 * 		0	1	2
		 * 0	-	a	ε
		 * 1
		 * 2
		 */
	public void addTransition(int from, int to, String input){
		transitions[from][to] = input;
	}
	
	public void addEmptyTransition(int from, int to){
		transitions[from][to] = "ε";
	}
	
	public void noTransition(int from, int to){
		transitions[from][to] = "-";
	}
	
	public String toString(){
		
		String headers = "\t";
		String values = "";
		
		for (int i = 0; i < transitions.length; i++){
			values += i + "\t";
			headers += i + "\t";
			for (int j = 0; j < transitions.length; j++){
				values += transitions[i][j] + "\t";
			}
			values += "\n";
		}
		headers += "\n";
		
		String output = "\n" + headers + values;
		return output;
	}
	
}
