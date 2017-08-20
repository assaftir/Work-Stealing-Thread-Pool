package bgu.spl.a2.sim;
/**
 * this class represents an indexed product
 */
public class IndexedProduct {
	
	Product product;
	int index;
	
	public IndexedProduct(Product product, int index){
		this.product = product;
		this.index = index;
	}
	
	public int getProductIndex(){
		return index;
	}
	
	public Product getProduct(){
		return product;
	}

}
