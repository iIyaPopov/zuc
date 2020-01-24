public class LFSR {
	private int vVector;
	private int uVector;
	private int[] registeresState;
	private FFunction f;
	
	public LFSR(byte[] k, byte[] iVector) {
		registeresState = new int[Constants.S_VECTOR_SIZE];
		int w;
		
		for (int i = 0; i < 16; i++) {
			registeresState[i] = combineTo31Bit(k[i], Constants.D[i], iVector[i]);
		}
		
		
		f = new FFunction();
		for (int i = 0; i < 32; i++) {
			int[] x = BitReorganization.run(this.registeresState);
			w = f.run(x);
			this.initMode(w >>> 1);
		}
		
		int[] x = BitReorganization.run(registeresState); 
		f.run(x);
	}
	
	private static int combineTo31Bit(byte a, int b, byte c) {
		int result = ((int)(a << 23) & 0xff) | (b << 8) | ((int) (c & 0xff));
		return result;
	}
	
	private void initMode(int uVector) {
		int fVector = (int)(((long) this.registeresState[15] << 15) +
				   			((long) this.registeresState[13] << 17) +
				   			((long) this.registeresState[10] << 21) +
				   			((long) this.registeresState[4]  << 20) +
				   			((long) this.registeresState[0]  << 8 ) +
				   			((long) this.registeresState[0])) % 0x7fffffff;
		if (fVector == 0) {
			fVector = 0x7fffffff;
		}
		this.statesUpdate(fVector);
	}
	
	private void statesUpdate(int vector) {
		for (int i = 0; i < 15; i++) {
			this.registeresState[i] = this.registeresState[i+1];
		}
		this.registeresState[15] = vector;
	}
	
	private void workMode() {
		int fVector = (int)(((long) this.registeresState[15] << 15) +
				   			((long) this.registeresState[13] << 17) +
				   			((long) this.registeresState[10] << 21) +
				   			((long) this.registeresState[4]  << 20) +
				   			((long) this.registeresState[0]  << 8 ) +
				   			((long) this.registeresState[0])) % 0x7fffffff;
		if (fVector == 0) {
			fVector = 0x7fffffff;
		}
		this.statesUpdate(fVector);
	}
	
	public int generateStream() {
		this.workMode();
		int[] x = BitReorganization.run(registeresState);
		int z = f.run(x) ^ x[3];
		return z;
	}
}