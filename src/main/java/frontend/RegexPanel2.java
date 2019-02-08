package frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class RegexPanel2 extends JPanel {

	private String regex = "";
	private Graphics g;

	public RegexPanel2(){
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	
	public void paintComponent(Graphics g){
//		this.g = g;
		super.paintComponent(g);
		// paint the fa
		
		
	}
	
	public void convert(String regex){
		this.regex = regex;
		
		g.fillOval(130,130,50, 60);
		g.drawString("HELLO",40,40);
		g.drawString(regex,40,40);
		
		repaint();
	}
	
}
