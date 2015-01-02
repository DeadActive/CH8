package chip;

public class CpuThread implements Runnable {
	Thread t;
	
	public CpuThread(){
		t = new Thread(this,"CPU");
		t.start();
	}
	
	public void run(){
		for(;;){
			CPU.setKeyBuffer(ChipMain.frame.getKeyBuffer());
			CPU.nextOpcode();
			if(CPU.needRedraw){
				ChipMain.frame.repaint();
				CPU.removeRedraw();
			}
			try{
				t.sleep(1);
			}
			catch(Exception e){
				System.err.println(e);
			}
		}
	}
}
