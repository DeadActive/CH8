package chip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;



public class ChipFrame extends JFrame implements KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1297479392899666409L;
	private ChipPanel panel;
	private MenuBar menubar;
	private int[] keyBuffer;
	private int[] keyIdToKey;
	private JFileChooser od;
	
	public ChipFrame(){
		setPreferredSize(new Dimension(660,380));
		pack();
		setPreferredSize(new Dimension(640 + getInsets().left + getInsets().right,320 + getInsets().top + getInsets().bottom));
		panel = new ChipPanel();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CH8");
		
		od = new JFileChooser("D://javaproj1//CH8//roms");
		
		menubar = new MenuBar();
		Menu file = new Menu("File");
		
		MenuItem open = new MenuItem("Open");
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CPU.init();
				od.showOpenDialog(null);
				CPU.loadRom(od.getSelectedFile());
				ChipMain.runFlag = true;
				new CpuThread();
			}
		});
		
		file.add(open);
		menubar.add(file);
		
		this.setMenuBar(menubar);
		
		add(panel);
		setVisible(true);
		addKeyListener(this);
		
		keyIdToKey = new int[256];
		keyBuffer = new int[16];
		fillKeyIds();
	}
	
	private void fillKeyIds() {
		for(int i = 0; i < keyIdToKey.length; i++) {
			keyIdToKey[i] = -1;
		}
		keyIdToKey['1'] = 1;
		keyIdToKey['2'] = 2;
		keyIdToKey['3'] = 3;		
		keyIdToKey['Q'] = 4;
		keyIdToKey['W'] = 5;
		keyIdToKey['E'] = 6;
		keyIdToKey['A'] = 7;
		keyIdToKey['S'] = 8;
		keyIdToKey['D'] = 9;
		keyIdToKey['Z'] = 0xA;
		keyIdToKey['X'] = 0;
		keyIdToKey['C'] = 0xB;
		keyIdToKey['4'] = 0xC;
		keyIdToKey['R'] = 0xD;
		keyIdToKey['F'] = 0xE;
		keyIdToKey['V'] = 0xF;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(keyIdToKey[e.getKeyCode()] != -1) {
			keyBuffer[keyIdToKey[e.getKeyCode()]] = 1;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(keyIdToKey[e.getKeyCode()] != -1) {
			keyBuffer[keyIdToKey[e.getKeyCode()]] = 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {	
	}
	
	public int[] getKeyBuffer() {
		return keyBuffer;
	}
}
