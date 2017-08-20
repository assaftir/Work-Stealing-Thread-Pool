/**
 * this class represents a product manufacture task
 */

package bgu.spl.a2.sim.tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import bgu.spl.a2.*;
import bgu.spl.a2.sim.*;
import bgu.spl.a2.sim.conf.*;
import bgu.spl.a2.sim.tools.*;

public class ManufactureTask extends Task<Product> {

	ManufactoringPlan productPlan;
	Product product;
	Warehouse warehouse;
	AtomicLong AllValues = new AtomicLong(0);
	CountDownLatch toolsLatch;
	CountDownLatch subPartsLatch;

	public ManufactureTask(Product product, Warehouse warehouse){
		this.product = product;
		this.warehouse = warehouse;
		productPlan = warehouse.getPlan(product.getName());
		for(String partName : productPlan.getParts())
			product.addPart(new Product(product.getStartId() + 1, partName));
	}

	protected void start() {
		//No sub parts, nothing to do
		if(product.getParts().isEmpty()){
			complete(product);
			return;
		}		
		//Send sub parts to manufacture
		List<Task<Product>> subPartsTasks = new ArrayList<>();
		for(Product part : product.getParts()){
			ManufactureTask newTask = new ManufactureTask(part, warehouse);
			subPartsTasks.add(newTask);
		}

		/*Assembly procedure optional callbacks*/
		int numOfRequiredTools = productPlan.getTools().length;
		//no required tools, when my parts are ready, just complete me
		if(numOfRequiredTools == 0){
			whenResolved(subPartsTasks, () -> {			
				complete(product);
				return;
			});
		}
		else{
			whenResolved(subPartsTasks, () -> {		
				AtomicInteger toolsUsageCounter = new AtomicInteger(numOfRequiredTools);
				for(String tool : productPlan.getTools()){ //Iterate required tools and apply useOn
					//If the tool is currently available at the warehouse, the promisedTool is resolved immediately
					Deferred<Tool> promisedTool = warehouse.acquireTool(tool);
					promisedTool.whenResolved( () -> {
						AllValues.getAndAdd(promisedTool.get().useOn(product));
						warehouse.releaseTool(promisedTool.get());
						//If i'm the last one to apply a tool, complete the product
						if(toolsUsageCounter.decrementAndGet() == 0){
							AtomicLong finalID = new AtomicLong(product.getStartId() + AllValues.get());
							product.setFinalID(finalID);
							complete(product);
						}
					});
				}
			});	
		}
		//Submit sub-parts to manufacture
		spawn(subPartsTasks.toArray(new Task<?>[0]));
	}
}
