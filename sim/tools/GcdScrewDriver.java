/**
 * this class represents a tool to be used by workers
 */

package bgu.spl.a2.sim.tools;

import java.math.BigInteger;

import bgu.spl.a2.sim.Product;

public class GcdScrewDriver implements Tool {
	
	String type = "gs-driver";

	@Override
	public String getType() {
		return type;
	}

	@Override
	public long useOn(Product p) {
		long result = 0;
		for(Product part : p.getParts()){
			result += Math.abs(func(part.getFinalId()));
		}		
		return result;
	}
	private long reverse(long input){		
		long reversedNum = 0;
		while(input != 0){
			reversedNum = (reversedNum*10);
			reversedNum = reversedNum + (input%10);
			input/=10;
		}
		return reversedNum;
		
	}
	private long func(long a){
		BigInteger a1 = BigInteger.valueOf(a);
		BigInteger a2 = BigInteger.valueOf(reverse(a));	
		return (a1.gcd(a2)).longValue();
		
	}

}
