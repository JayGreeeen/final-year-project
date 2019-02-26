package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import regex_to_fa.Regex_to_FA;
import toolbox.Finite_Automata;
import view.FA_Drawer;

public class Regex_Converter_Panel extends JPanel {

	
	
		private Finite_Automata fa = null;
		
		// ***********
		private FA_Drawer drawer;
		
		public Regex_Converter_Panel() {
			setBackground(Color.white);
			setMinimumSize(new Dimension(100, 300));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
	
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawer = new FA_Drawer(g);
			
//			if (fa != null) {
//				drawer.drawFA(fa);
//			}
		}
	
		public void convertToFa(String regex) {
			
			Regex_to_FA converter = new Regex_to_FA();
			
			
	
			String errorMessage = converter.validate(regex);
	
			if (errorMessage == "") {
				Finite_Automata fa = converter.convertToFA(regex);
				this.fa = fa;
				repaint();
			} else {
				String message = "Please enter a valid regex. " + errorMessage;
				JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	
	
	
}
