import java.io.*;
import java.util.*;

public class Zuc {
	private byte[] key;
	private byte[] initVector;
	private static final int KEY_LENGTH 		= 16;
	private static final int INIT_VECTOR_LENGTH = 16;
	
	public Zuc() throws FileNotFoundException {
		key = new byte[16];
		initVector = new byte[16];
	}
	
	public void loadKey(String keyFile) throws IOException, FileNotFoundException {
		File file = new File(keyFile);
		if (file.length() == 33) {
			FileInputStream fis = new FileInputStream(file);
			for (int i = 0; i < KEY_LENGTH; i++) {
				key[i] = (byte) fis.read();
			}
			fis.read();
			for (int i = 0; i < INIT_VECTOR_LENGTH; i++) {
				initVector[i] = (byte) fis.read();
			}
		} else {
			throw new RuntimeException("Illegal size of key file");
		}
	}

	public void encrypt(String input, String output) throws IOException {
		File file = new File(input);
		FileInputStream in = new FileInputStream(file);
		FileOutputStream out = new FileOutputStream(new File(output));
		LFSR register = new LFSR(key, initVector);
		int stream = register.generateStream();
		byte[] z = this.split(stream);
		int index = 0;
		int m;
		int c;
		long fileSize = file.length();
		for (int i = 0; i < fileSize;) {
			if (index < 4) {
				m = in.read();
				c = m ^ z[index];
				index++;
				i++;
				out.write((byte) c);
			} else {
				stream = register.generateStream();
				z = this.split(stream);
				index = 0;
			}
		}
	}
	
	private byte[] split(int x) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((x >>> 24) & 0xff);
		bytes[1] = (byte)((x >>> 16) & 0xff);
		bytes[2] = (byte)((x >>> 8) & 0xff);
		bytes[3] = (byte)(x & 0xff);
		return bytes;
	}
	
	public void decrypt(String input, String output) throws IOException {
		this.encrypt(input, output);
	}
	
	public void doTests(String keyFile) throws IOException {
		int length;
		double res;
		int l;
		int delay;
		Test test;
		
		test = new Test(this.init(keyFile));
		length = 10000;
		res = test.frequencyTest(length);
		System.out.println("Frequency test: " + res);
		
		test = new Test(this.init(keyFile));
		l = 4;
		res = test.sequencesTest(l);
		System.out.println("Sequences test: " + res);
		
		test = new Test(this.init(keyFile));
		length = 10000;
		l = 4;
		res = test.seriesTest(length, l);
		System.out.println("Series test: " + res);
		
		test = new Test(this.init(keyFile));
		length = 10000;
		delay = 4;
		res = test.autocorrelationTest(length, delay);
		System.out.println("Autocorrelation test: " + res);
		
		test = new Test(this.init(keyFile));
		l = 6;
		res = test.universalTest(l);
		System.out.println("Universal test: " + res);
	}
	
	private LFSR init(String keyFile) throws IOException {
		this.loadKey(keyFile);
		LFSR register = new LFSR(key, initVector);
		return register;
	}
	
	public static void main(String[] args) {
		String dir = "/home/ilya/Desktop/ZUC/";
		String input 	= dir + "input.txt";
		String encrypt 	= dir + "encrypt.txt";
		String decrypt 	= dir + "decrypt.txt";
		String keyFile 	= dir + "key.txt";
		try {
			/*Zuc zucE = new Zuc();
			zucE.loadKey(keyFile);
			zucE.encrypt(input, encrypt);*/
			
			/*Zuc zucD = new Zuc();
			zucD.loadKey(keyFile);
			zucD.decrypt(encrypt, decrypt);*/
			
			Zuc zucT = new Zuc();
			zucT.doTests(keyFile);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		
		
		//Test test = new Test(new LFSR(k, iv));
		//System.out.println(test.frequencyTest(10000000));
		//System.out.println(test.seriesTest(100, 3));
		//System.out.println(test.sequenceTest(3));
		//System.out.println(0xffffffff + 1);
		/*for (int i = 0; i < 50; i++) {
			Test test = new Test(new LFSR(k, iv));
			System.out.println(test.autocorrelationTest(10000, i));
		}*/
	}
}