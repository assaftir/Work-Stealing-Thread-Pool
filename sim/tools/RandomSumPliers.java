/**
 * this class represents a tool to be used by workers
 */


package bgu.spl.a2.sim.tools;
import java.util.Random;
import bgu.spl.a2.sim.Product;

public class RandomSumPliers implements Tool {

	String type = "rs-pliers";
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
	
	public long func(long id){
    	Random r = new Random(id);
        long sum = 0;
        for (long i = 0; i < id % 10000; i++) {
            sum += r.nextInt();
        }
        return sum;
    }

}
