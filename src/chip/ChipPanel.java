package chip;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ChipPanel extends JPanel{
	public void paint(Graphics g){
		byte[] display = CPU.screen;
		for (int i = 0;i<display.length;i++){
			if(display[i] == 0)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.WHITE);
			
			int x = (i%64);
			int y = (int)Math.floor(i/64);
			
			g.fillRect(x*10, y*10, 10, 10);
		}
	}
}
