package bgu.spl.a2.sim;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements Serializable {
	
	static final long serialVersionUID = 314159;
	
	String pName;
	long startID;
	AtomicLong finalID;
	List<Product> parts = new ArrayList<Product>();
	
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
    	startID = startId;
    	pName = name;
    	finalID = new AtomicLong(startId);
    }

	/**
	* @return The product name as a string
	*/
    public String getName(){
    	return pName;
    }

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId(){
    	return startID;
    }
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId(){
    	return finalID.longValue();
    }

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){
    	return parts;
    }

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){
    	parts.add(p);
    }
    
    public void setFinalID(AtomicLong finalID){
    	this.finalID = finalID;
    }

    public String toString(){  	
    	return (pName + " the id is " + finalID);

    }

}
