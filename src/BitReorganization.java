public class BitReorganization {
	
	public static int[] run(int[] registeresState) {
		int[] result = new int[4];
		result[0] = ((registeresState[15] & 0x7FFF8000) << 1)  | (registeresState[14] & 0x0000ffff); 
		result[1] = ((registeresState[11] & 0x0000ffff) << 16) | (registeresState[9] >> 15);
		result[2] = ((registeresState[7]  & 0x0000ffff) << 16) | (registeresState[5] >> 15);
		result[3] = ((registeresState[2]  & 0x0000ffff) << 16) | (registeresState[0] >> 15);
		return result;
	}
}