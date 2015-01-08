package chip;


public class ChipMain extends Thread{
	
	public static ChipFrame frame;
	public static boolean runFlag = false;
	
	public ChipMain(String name){
		frame = new ChipFrame();
		frame.setTitle("CH8 - " + name);
	}
	public static void main(String args[]){
		CPU.init();
		
		ChipMain main = new ChipMain(args[0]);

		main.start();
	}
	
	public void run(){
		while(!runFlag){
			
		}
		
		while(runFlag){
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
}
