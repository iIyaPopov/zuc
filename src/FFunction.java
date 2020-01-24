public class FFunction {
	private int R1;
	private int R2;
	
	public FFunction() {
		this.R1 = 0;
		this.R2 = 0;
	}
	
	public int run(int[] x) {
		int W = (int) (((x[0] ^ R1) + R2) % 100000000L);
		int W1 = (int) ((R1 + x[1]) % 0x100000000L);
		int W2 = R2 ^ x[2];
		int uVector = L1((W1 << 16) | ((W2 >> 16) & 0xffff));
		int vVector = L2((W2 << 16) | ((W1 >> 16) & 0xffff));
		this.R1 = combineTo32Bit(Constants.S0[(uVector >> 24) & 0xff],
								 Constants.S1[(uVector >> 16) & 0xff],
								 Constants.S0[(uVector >>  8) & 0xff],
								 Constants.S1[(uVector)       & 0xff]);
		
		this.R2 = combineTo32Bit(Constants.S0[(vVector >> 24) & 0xff],
								 Constants.S1[(vVector >> 16) & 0xff],
								 Constants.S0[(vVector >>  8) & 0xff],
								 Constants.S1[(vVector)       & 0xff]);
		return W; 
	}
	
	public int L1(int x) {
		int result = x ^ Integer.rotateLeft(x, 2) ^ Integer.rotateLeft(x, 10) ^ Integer.rotateLeft(x, 18) ^ Integer.rotateLeft(x, 24);
		return result;
	}
	
	public int L2(int x) {
		int result = x ^ Integer.rotateLeft(x, 8) ^ Integer.rotateLeft(x, 14) ^ Integer.rotateLeft(x, 22) ^ Integer.rotateLeft(x, 30);
		return result;
	}
	
	public static int combineTo32Bit(int a, int b, int c, int d) {
		int result = (a << 24) | (b << 16) | (c << 8) | d;
		return result;
	}
}