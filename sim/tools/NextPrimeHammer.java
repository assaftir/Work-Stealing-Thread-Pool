/**
 * this class represents a tool to be used by workers
 */


package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class NextPrimeHammer implements Tool {

	String type = "np-hammer";
	
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
	
	public long func(long id) {
		long v = id + 1;
		while (!isPrime(v)) {
			v++;
		}
		return v;
	}
	
	private boolean isPrime(long value) {
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
