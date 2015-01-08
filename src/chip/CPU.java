package chip;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class CPU {
	static private char opcode;
	static private char[] V = new char[16];
	static private char I;
	static private char PC;
	static private int SP;
	static private char[] stack = new char[16];
	static private char[] memory = new char[4096];
	static char delay_timer = 60;
	static char sound_timer = 60;
	static private char[] flags = new char[8];
	static private int[] font ={0xF0, 0x90, 0x90, 0x90, 0xF0,	// 0
			0x20, 0x60, 0x20, 0x20, 0x70,	// 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0,	// 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0,	// 3
			0x90, 0x90, 0xF0, 0x10, 0x10,	// 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0,	// 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0,	// 6
			0xF0, 0x10, 0x20, 0x40, 0x40,	// 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0,	// 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0,	// 9
			0xF0, 0x90, 0xF0, 0x90, 0x90,	// A
			0xE0, 0x90, 0xE0, 0x90, 0xE0,	// B
			0xF0, 0x80, 0x80, 0x80, 0xF0,	// C
			0xE0, 0x90, 0x90, 0x90, 0xE0,	// D
			0xF0, 0x80, 0xF0, 0x80, 0xF0,	// E
			0xF0, 0x80, 0xF0, 0x80, 0x80	// F;
	};
	static private int[] bigFont = {0xFF, 0xFF, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF,	// 0
			0x18, 0x78, 0x78, 0x18, 0x18, 0x18, 0x18, 0x18, 0xFF, 0xFF,	// 1
			0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF,	// 2
			0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF,	// 3
			0xC3, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, 0x03, 0x03, 0x03, 0x03, // 4
			0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF,	// 5
			0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF,	// 6
			0xFF, 0xFF, 0x03, 0x03, 0x06, 0x0C, 0x18, 0x18, 0x18, 0x18, // 7
			0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF,	// 8
			0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF,	// 9
			0x7E, 0xFF, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, 0xC3, 0xC3, 0xC3, // A
			0xFC, 0xFC, 0xC3, 0xC3, 0xFC, 0xFC, 0xC3, 0xC3, 0xFC, 0xFC, // B
			0x3C, 0xFF, 0xC3, 0xC0, 0xC0, 0xC0, 0xC0, 0xC3, 0xFF, 0x3C, // C
			0xFC, 0xFE, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xFE, 0xFC, // D
			0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, // E
			0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC0, 0xC0, 0xC0, 0xC0  // F
	};
	
	static public byte[] screen;
	static public byte[] keys = new byte[16];
	static public boolean stop;
	static public boolean needRedraw;
	
	static private int x;
	static private int nn;
	
	static public void init(){
		I = 0x0;
		PC = 0x200;
		SP = 0;
		delay_timer = 0;
		sound_timer = 0;
		
		for(int i = 0;i<16;i++){
			V[i] = 0;
			stack[i] = 0;
		}
		
		for(int i = 0;i<4096;i++)
			memory[i] = 0;
		
		screen = new byte[64 * 32];
		
		for(int i = 0;i<8;i++)
			flags[i] = 0;
		
		for(int i = 0;i<16*5;i++){
			memory[i] = (char) font[i];
		}
		
		for(int i = 0;i<16*10;i++){
			memory[i+80] = (char) bigFont[i];
		}
		
		stop = false;
		needRedraw = false;
	}
	
	static public void nextOpcode(){
		opcode = (char) ((memory[PC]<<8)|(memory[PC+1]));
		
		System.out.print(Integer.toHexString(opcode) + ":");
		
		switch(opcode & 0xf000){
		
		case 0x0000:
			switch(opcode & 0x00ff){
			case 0x00e0:
				for (int i = 0;i<64*32;i++)
					screen[i] = 0;
				PC += 2;
				System.out.println("Clear the screen");
				break;
			case 0x00ee:
				SP--;
				PC = (char)(stack[SP] + 2);
				System.out.println("Return to " + Integer.toHexString(PC));
				break;
			default:
				System.err.println("Unsupported opcode...");
				System.exit(0);
			}
			break;

		
		case 0x1000:
			int nnn = opcode & 0x0fff;
			PC = (char)nnn;
			System.out.println("Jump to " + Integer.toHexString(nnn));
			break;
		case 0x2000:
			stack[SP] = PC;
			SP++;
			PC = (char)(opcode & 0x0fff);
			System.out.println("Calling " + Integer.toHexString(PC));
			break;
		case 0x3000:
			x = (opcode & 0x0f00)>>8;
			nn = (opcode & 0x0ff);
			if(V[x] == nn){
				PC += 4;
				System.out.println("Skipping next instruction");
			}
			else{
				PC += 2;
				System.out.println("Not skipping next instruction");
			}
			
			break;
		case 0x4000:
			x = (opcode & 0x0f00)>>8;
			nn = (opcode & 0x0ff);
			if(V[x] != nn){	
				PC += 4;
				System.out.println("Skipping next instruction");
			}
			else{
				PC += 2;
				System.out.println("Not skipping next instruction");
			}
			
			break;
		case 0x5000:
			int x = (opcode & 0x0f00)>>8;
			int y = (opcode & 0x00f0)>>4;
			if(V[x] == V[y]){
				PC += 4;
				System.out.println("Skipping next instruction");
			}
			else{
				PC += 2;
				System.out.println("Not skipping next instruction");
			}
		case 0x6000:
			x = (opcode & 0x0f00) >> 8;
			V[x] = (char)(opcode & 0x00ff);
			PC+=2;
			System.out.println("Sets " + Integer.toHexString((char)(opcode & 0x00ff)) + " to " + x);
			break;
		case 0x7000:
			int _x = (opcode & 0x0f00) >> 8;
			nn = (opcode & 0x00ff);
			V[_x] = (char)((V[_x] + nn) & 0xff);
			System.out.println("Adding " + nn + " to V[" + _x +"] = " + (int)V[_x]);
			PC += 2;
			break;	
		
		case 0x8000:
			
			switch (opcode & 0x000f){
			case 0x0000:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				V[x] = (char)V[y];
				PC += 2;
				System.out.println("Set V[" + x + "] to " + y);
				break;
			
			case 0x0001:
			x = (opcode & 0x0f00)>>8;
			y = (opcode & 0x00f0)>>4;
			V[x] = (char)(V[y]|V[x]);
			PC += 2;
			System.out.println("Set V[" + x + "] to " + y + " OR " + x);
			break;
			case 0x0002:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				V[x] = (char)(V[y] & V[x]);
				PC += 2;
				System.out.println("Set V[" + x + "] to " + y + " AND " + x);
				break;
			case 0x0003:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				V[x] = (char)(V[y] ^ V[x]);
				PC += 2;
				System.out.println("Set V[" + x + "] to " + y + " XOR " + x);
				break;
			case 0x0004:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				if(V[y]> 0xff - V[x]){
					V[0xf] = 1;
					System.out.println("carry");
				}else{
					V[0xf] = 0;
					System.out.println("not carry");
				}
				V[x] = (char)((V[x] + V[y])&0xff);
				PC += 2;
				System.out.println("Adding V[" + x + "] to " + y );
				break;
			case 0x0005:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				if(V[x]> V[y]){
					V[0xf] = 1;
					System.out.println("carry");
				}else{
					V[0xf] = 0;
					System.out.println("not carry");
				}
				V[x] = (char)((V[x] - V[y])&0xff);
				PC += 2;
				System.out.println("Substract V[" + x + "] to " + y );
				break;
			case 0x0006:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				if (V[x]>>8 == 1){
					V[0xf] = 1;
				}
				else{
					V[0xf] = 0;
				}
				V[x] = (char) (V[x]>>1);
				PC += 2;
				break;
			case 0x0007:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				if(V[x]> V[y]){
					V[0xf] = 1;
					System.out.println("carry");
				}else{
					V[0xf] = 0;
					System.out.println("not carry");
				}
				V[x] = (char)((V[y] - V[x])&0xff);
				PC += 2;
				System.out.println("Substract V[" + x + "] to " + y );
				break;
			case 0x000e:
				x = (opcode & 0x0f00)>>8;
				y = (opcode & 0x00f0)>>4;
				if (V[x]<<8 == 1){
					V[0xf] = 1;
				}
				else{
					V[0xf] = 0;
				}
				V[x] = (char) (V[x]<<1);
				PC += 2;
				break;
			}
			
			
			break;
		case 0x9000:
			x = (opcode & 0x0f00)>>8;
			y = (opcode & 0x00f0)>>4;
			if(V[x] != V[y]){
				PC += 4;
				System.out.println("Skipping next instruction");
			}
			else{
				PC += 2;
				System.out.println("Not skipping next instruction");
			}
		case 0xa000:
			I = (char)(opcode & 0x0fff);
			PC += 2;
			System.out.println("Set I to " + Integer.toHexString(I));
			break;
			
		case 0xc000:
			x = (opcode & 0x0f00) >> 8;
			nn = opcode & 0x00ff;
			int randNum = new Random().nextInt(256) & nn;
			System.out.println("Randomised " + Integer.toHexString(x));
			V[x] = (char) randNum;
			PC += 2;
			break;
		
		case 0xd000:
			try{
			int xx = V[(opcode & 0x0f00)>>8];
			int yy = V[(opcode & 0x00f0)>>4];
			int height = opcode & 0x000f;
			
			V[0xf] = 0;
			
			for(int _y = 0; _y < height; _y++) {
				int line = memory[I + _y];
				for(int _x1 = 0; _x1 < 8; _x1++) {
					int pixel = line & (0x80 >> _x1);
					if(pixel != 0) {
						int totalX = xx + _x1;
						int totalY = yy + _y;
						int index = totalY * 64 + totalX;
						
						if(screen[index] == 1)
							V[0xF] = 1;
						
						screen[index] ^= 1;
					}
				}
			}
			PC += 2;
			needRedraw = true;
			System.out.println("Drawing sprite on (" + xx + ";" + yy + ";" + height + ")");
			break;
			}
			catch(ArrayIndexOutOfBoundsException e){
				PC += 2;
				needRedraw = true;
				break;
			}
		
		case 0xe000:
			switch (opcode & 0x00ff){
			case 0x009e:
				x = (opcode & 0x0f00) >> 8;
				int key = V[x];
				
				if (keys[key] == 1){
					PC += 4;
				}
				else{
					PC += 2;
				}
				break;
			case 0x00a1:
				x = (opcode & 0x0f00) >> 8;
				key = V[x];
				
				if (keys[key] == 0){
					PC += 4;
				}
				else{
					PC += 2;
				}
				break;
			default:
				System.err.println("Unsupported opcode");
				System.exit(0);
			}

			break;
				
		case 0xf000:
			switch(opcode & 0x00ff){
			case 0x000a:
				x = (opcode & 0x0f00)>>8;
				boolean flag = false;
				for(;;){
					for (int i=0;i<keys.length;i++){
						setKeyBuffer(ChipMain.frame.getKeyBuffer());
						if (keys[i] != 0){
							V[x] = (char)i;
							System.out.println(i + " pressed");
							PC += 2;
							flag = true;
							break;
						}
						if(flag) break;
					}
					if(flag) break;
				}
				break;
			case 0x0029:
				x = (opcode & 0x0f00)>>8;
				int character = V[x];
				I = (char)(character * 5);
				System.out.println("Setting I to char " + x);
				PC += 2;
				break;
			case 0x0033:
				x = (opcode & 0x0F00) >> 8;
				int value = V[x];
				int hundreds = (value - (value % 100)) / 100;
				value -= hundreds * 100;
				int tens = (value - (value % 10))/ 10;
				value -= tens * 10;
				memory[I] = (char)hundreds;
				memory[I + 1] = (char)tens;
				memory[I + 2] = (char)value;
				System.out.println("Storing Binary-Coded Decimal V[" + x + "] = " + (int)(V[(opcode & 0x0F00) >> 8]) + " as { " + hundreds+ ", " + tens + ", " + value + "}");
				PC += 2;
				break;
			case 0x0055:
				x = (opcode & 0x0f00) >> 8;
				
				for(int i=0;i<x;i++){
					memory[I + 1] = V[i];
				}
				PC += 2;
				break;
			case 0x0065:
				x = (opcode & 0x0f00) >> 8;
				
				for(int i=0;i<x;i++){
					V[i] = memory[I + 1];
				}
				PC += 2;
				break;
			case 0x0007:
				x = (opcode & 0x0f00) >> 8;
				V[x] = (char)delay_timer;
				PC += 2;
				break;
			case 0x0015:
				x = (opcode & 0x0f00) >> 8;
				delay_timer = (char)V[x];
				PC += 2;
				break;
			case 0x0018:
				x = (opcode & 0x0f00) >> 8;
				sound_timer = (char)V[x];
				PC += 2;
				break;
			case 0x001e:
				x = (opcode & 0x0f00) >> 8;
				I += (char)V[x];
				System.out.println("Adding " + Integer.toHexString(x) + " to I");
				PC +=2;
				break;
				
				
			default:
					System.err.println("Unsupported opcode");
					System.exit(0);
			}
			break;
		
		default:
			System.out.println("Unsupported opcode...");
			System.exit(0);
		}
		

	}
	
	@SuppressWarnings("resource")
	static public void loadRom(File file){
		try {
			DataInputStream f = new DataInputStream(new FileInputStream(file));
			byte[] mem = new byte[4096];
			f.read(mem);
			
			System.out.println("Openning " + file.getName() + "...");
			
			for(int i = 0;i<4096-0x200;i++){
				memory[i+0x200] = (char) (mem[i] & 0xff);
			}
			
			System.out.println("Running " + file.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void ramDump(){
		for(int i = 0;i<4096;i++){
			if(i == 0x200) System.out.println("------------Game ROM------------");
			System.out.print(i + " ");
			System.out.println(Integer.toHexString((int)memory[i]));
		}
	}
	
	static public boolean needRedraw(){
		return needRedraw;
	}
	
	static public void removeRedraw(){
		needRedraw = false;
	}
	
	static public void setKeyBuffer(int[] keyBuffer){
		for (int i = 0;i< keys.length;i++){
			keys[i] = (byte) keyBuffer[i];
		}
	}
}
