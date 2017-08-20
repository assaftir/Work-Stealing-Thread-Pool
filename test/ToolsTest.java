package bgu.spl.a2.test;

import java.util.Random;

public class ToolsTest {

	public static void main(String[] args){
		
		long func1 = func1(50123456);
		long func2 = func2(50123456);
		System.out.println(func1+func2 + 50123455);
	}
	
	public static long func2(long id){
    	Random r = new Random(id);
        long  sum = 0;
        for (long i = 0; i < id % 10000; i++) {
            sum += r.nextInt();
        }
        return sum;
    }

	
	public static long func1(long id) {
		long v = id + 1;
		while (!isPrime(v)) {
			v++;
		}
		return v;
	}
	
	private static boolean isPrime(long value) {
        if(value < 2) return false;
    	if(value == 2) return true;
        long sq = (long) Math.sqrt(value);
        for (long i = 2; i <= sq; i++) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }
	
	
	
}
