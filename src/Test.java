import java.util.*;

public class Test {
	private LFSR register;
	private int mask;
	private int z;
	
	public Test(LFSR register) {
		this.register = register;
		this.mask = 0;
	}
	
	private int getNextBit() {
		int result;
		if (mask == 0) {
			mask = 0x80000000;
			z = this.register.generateStream();
		}
		if ((int) (z & mask) != 0) {
			result = 1;
		} else {
			result = 0;
		}
		mask = mask >>> 1;
		return result;
	}
	
	private int getNextBlock(int length) {
		int result = 0;
		for (int i = 0; i < length; i++) {
			result = (result << 1) | this.getNextBit();
		}
		return result;
	}
	
	public double frequencyTest(int n) {
		double result = 0;
		int oneCount = 0;
		for (int i = 0; i < n; i++) {
			oneCount += this.getNextBit();
		}
		result = (oneCount - n / 2) * 2 / Math.sqrt(n);
		return result; 
	}
	
	public double sequencesTest(int l) {
		int radix = 2;
		int[] numsCount = new int[(int) Math.pow(2, l)];
		int bitsCount = 0;
		StringBuilder num = new StringBuilder();
		int n = 5 * l * (int) Math.pow(2, l);
		for (int i = 0; i < n;) {
			if (bitsCount == l) {
				int a = Integer.parseInt(num.toString(), radix);
				numsCount[a]++;
				bitsCount = 0;
				num.delete(0, l);
			} else {
				num.append(getNextBit());
				bitsCount++;
				i++;
			}
		}
		double result = 0;
		for (int i = 0; i < numsCount.length; i++) {
			result += Math.pow(numsCount[i] - n / (l * Math.pow(2, l)), 2);
		}
		result *= l * Math.pow(2, l) / n;
		return result;
	}
	
	public double seriesTest(int n, int l) {
		int[] zero = new int[l];
		int[] one = new int[l];
		int count = 1;
		int prev = this.getNextBit();
		mask = mask >>> 1;
		for (int i = 1; i < n; i++) {
			if (this.getNextBit() == 1) {
				if (prev == 1) {
					count++;
				} else {
					prev = 1;
					if (count <= l) {
						zero[count-1]++;
					}
					count = 1;
				}
			} else {
				if (prev == 0) {
					count++;
				} else {
					prev = 0;
					if (count <= l) {
						one[count-1]++;
					}
					count = 1;
				}
			}
		}
		double result0 = 0;
		double result1 = 0;
		for (int i = 1; i <= l; i++) {
			double c = n / (Math.pow(2, i+2));
			result0 += Math.pow((zero[i-1] - c), 2) / c;
			result1 += Math.pow((one[i-1] - c), 2) / c;
		}
		return result0 + result1;
	}

	public double autocorrelationTest(int n, int delay) {
		if (delay == 0) {
			return 1;
		}
		double result = 0;
		int[] prev = new int[delay];
		for (int i = 0; i < delay; i++) {
			prev[i] = this.getNextBit();
		}
		for (int i = 0; i < n - delay; i++) {
			int current = this.getNextBit();
			result += prev[0] ^ current;
			for (int j = 0; j < delay - 1; j++) {
				prev[j] = prev[j+1];
			}
			prev[delay-1] = current;
		}
		result = result / (n - delay);
		return result;
	}

	public double universalTest(int l) {
		int count = (int) Math.pow(2, l);
		int[] tab = new int[count];
		int Q = 10 * count;
		int K = 1000 * count;
		for (int i = 0; i < Q; i++) {
			tab[this.getNextBlock(l)] = i;
		}
		double sum = 0;
		for (int i = Q; i < Q + K; i++) {
			int pattern = this.getNextBlock(l);
			sum += Math.log(i - tab[pattern]);
			tab[pattern] = i;
		}
		double f = sum / Math.log(2) / K;
		double c = 0.7 - 0.8 / l + (4 + 32 / l) * Math.pow(K, (-3 / l)) / 15;
		double z = (f - 5.2177052) / (c * Math.sqrt(2.954));
		return z;
	}
}