package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.*;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Deferred;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {

	Tool sDriver;
	Tool nPhammer;
	Tool rsPlier;
	AtomicInteger numOfSdrivers = new AtomicInteger(0);
	AtomicInteger numOfPhammers = new AtomicInteger(0);
	AtomicInteger numOfRSPliers = new AtomicInteger(0);
	ConcurrentLinkedQueue<Deferred<Tool>> sDriversPromises = new ConcurrentLinkedQueue<Deferred<Tool>>();
	ConcurrentLinkedQueue<Deferred<Tool>> pHammersPromises = new ConcurrentLinkedQueue<Deferred<Tool>>();
	ConcurrentLinkedQueue<Deferred<Tool>> rsPliersPromises = new ConcurrentLinkedQueue<Deferred<Tool>>();
	ConcurrentLinkedQueue<ManufactoringPlan> plansInventory = new ConcurrentLinkedQueue<>();

	/**
	 * Constructor
	 */
	public Warehouse(){}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * @param type - string describing the required tool
	 * @return a deferred promise for the requested tool
	 */
	public Deferred<Tool> acquireTool(String type){
		Deferred<Tool> tool = new Deferred<Tool>();
		if(type.equals("gs-driver")){
			if(numOfSdrivers.decrementAndGet() >= 0){
				tool.resolve(sDriver);
			} else {
				numOfSdrivers.incrementAndGet();
				sDriversPromises.add(tool);
			}
			return tool;			
		}
		else if(type.equals("np-hammer")){
			if(numOfPhammers.decrementAndGet() >= 0){
				tool.resolve(nPhammer);
			} else {
				numOfPhammers.incrementAndGet();
				pHammersPromises.add(tool);
			}
			return tool;			
		}
		else{
			if(numOfRSPliers.decrementAndGet() >= 0){
				tool.resolve(rsPlier);
			} else {
				numOfRSPliers.incrementAndGet();
				rsPliersPromises.add(tool);
			}
			return tool;			
		}
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 * @param tool - The tool to be returned
	 */
	public void releaseTool(Tool tool){

		if(tool instanceof GcdScrewDriver){
			synchronized (sDriversPromises) {
				numOfSdrivers.incrementAndGet();
				if(!sDriversPromises.isEmpty()){
					sDriversPromises.poll().resolve(sDriver);
				}
			}
		}
		else if(tool instanceof NextPrimeHammer){
			synchronized (pHammersPromises) {
				numOfPhammers.incrementAndGet();
				if(!pHammersPromises.isEmpty()){
					pHammersPromises.poll().resolve(nPhammer);
				}
			}
		}
		else{
			synchronized (rsPliersPromises) {
				numOfRSPliers.incrementAndGet();
				if(!rsPliersPromises.isEmpty()){
					rsPliersPromises.poll().resolve(rsPlier);
				}
			}
		}   	
	}

	/**
	 * Getter for ManufactoringPlans
	 * @param product - a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product; null if the product plan doesn't exists in the warehouse
	 */
	public ManufactoringPlan getPlan(String product){
		ManufactoringPlan requestedPlan = null;
		for(ManufactoringPlan plan : plansInventory){
			if(plan.getProductName().equals(product)){
				requestedPlan = plan;
				break;
			}
		}
		return requestedPlan;
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * @param plan - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan){
		plansInventory.add(plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later retrieval
	 * @param tool - type of tool to be stored
	 * @param qty - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty){
		if(tool instanceof GcdScrewDriver){
			sDriver = tool;
			numOfSdrivers.set(qty);
		}
		else if(tool instanceof RandomSumPliers){
			rsPlier = tool;
			numOfRSPliers.set(qty);
		}
		else{
			nPhammer = tool;
			numOfPhammers.set(qty);			
		}
	}
}
