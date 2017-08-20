/**
 * this class represents a comparator for indexed products by their order of manufacture
 */

package bgu.spl.a2.sim;

import java.util.Comparator;

public class ProductComparator implements Comparator<IndexedProduct> {

	@Override
	public int compare(IndexedProduct p1, IndexedProduct p2) {	
		if(p1 == null || p2 == null){
			System.err.println("Something strange occurred while sorting final products");
			System.exit(-1);
		}
		return (p1.getProductIndex() - p2.getProductIndex());
	}
}
