package chip;

import java.io.File;
import java.io.FileNotFoundException;

public class ChipMain extends Thread{
	
	public static ChipFrame frame;
	
	public ChipMain(String name){
		frame = new ChipFrame();
		frame.setTitle("Chip8 - " + name);
	}
	public static void main(String args[]){
		CPU.init();
		CPU.loadRom(new File(args[0]));
		
		ChipMain main = new ChipMain(args[0]);
		CpuThread cpu = new CpuThread();
		main.start();
	}
	
	public void run(){
		for(;;){
			for(int i = 60;i>0;i--){
				if(CPU.delay_timer > 0)
					CPU.delay_timer--;
				if(CPU.sound_timer > 0)
					CPU.sound_timer--;
				
				try{
					Thread.sleep(16);
				}
				catch(Exception e){
					System.err.println(e);
				}
			}
		}
	}
}
