/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.util.concurrent.*;
import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.*;
import bgu.spl.a2.sim.tasks.ManufactureTask;
import bgu.spl.a2.sim.tools.*;
import java.io.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	static WorkStealingThreadPool pool;
	static Warehouse theWarehouse;
	static ParsedData parsed;
	static ConcurrentLinkedQueue<Product> finalProducts;
	static ConcurrentLinkedQueue<IndexedProduct> finalIndexedProductsQueue;
	/**
	 * Begin the simulation
	 * Should not be called before attachWorkStealingThreadPool()
	 */
	public static ConcurrentLinkedQueue<Product> start() throws InterruptedException{
		finalIndexedProductsQueue = new ConcurrentLinkedQueue<IndexedProduct>();
		int nextProductIndex = 0;
		//Submit Waves
		for(List<Order> orders : parsed.getWaves()){
			CountDownLatch latch = new CountDownLatch(calculateWaveQuantity(orders));
			for(Order order : orders){
				for(int i = 0 ; i < order.getQty() ; i++){
					String nextProductName = order.getProduct();
					long nextProductID = (order.getStartId() + i);
					Product nextProduct = new Product(nextProductID, nextProductName);
					IndexedProduct nextIndexedProduct = new IndexedProduct(nextProduct, nextProductIndex);
					ManufactureTask task = new ManufactureTask(nextIndexedProduct.getProduct(), theWarehouse);
					task.getResult().whenResolved(()->{
						finalIndexedProductsQueue.add(nextIndexedProduct);
						latch.countDown();
					});
					nextProductIndex++;
					pool.submit(task);
				}
			}
			//Wait until current wave orders has been manufactured
			waitForWaveProduction(latch);
		}
		finalProducts = getSortedFinalProductsQueue();		
		return finalProducts;
	}

	static int calculateWaveQuantity(List<Order> orders){		
		int orderQtySum = 0;
		for(int i = 0 ; i < orders.size() ; i++){
			orderQtySum += orders.get(i).getQty();
		}
		return orderQtySum;
	}

	static void waitForWaveProduction(CountDownLatch latch) {
		try{
			latch.await();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	//Sort final products list and return it as a sorted queue
	static ConcurrentLinkedQueue<Product> getSortedFinalProductsQueue(){
		finalProducts = new ConcurrentLinkedQueue<Product>();
		IndexedProduct[] finalIndexedProductsArray = new IndexedProduct[totalNumOfProducts()];
		finalIndexedProductsQueue.toArray(finalIndexedProductsArray);
		Arrays.sort(finalIndexedProductsArray, new ProductComparator());
		for(IndexedProduct finalIndexedProduct : finalIndexedProductsArray)
			finalProducts.add(finalIndexedProduct.getProduct());
		return finalProducts;
	}
	
	static int totalNumOfProducts(){		
		int totalNumOfProducts = 0;	
		for(List<Order> orders : parsed.getWaves()){
			totalNumOfProducts += calculateWaveQuantity(orders);
		}		
		return totalNumOfProducts;
	}
	
	public static void main(String [] args) throws InterruptedException{		
		//build resources		
		parsed = parseInputFile(getInputFilePath(args));
		buildWarehouse();
		buildAndStartPool();		
		//Start the simulation
		ConcurrentLinkedQueue<Product> simulationResult = start();
		//Serialize output file
		serializeOutputFile(simulationResult, getOutputFileName(args));		
		//Pool shutdown
		pool.shutdown();
	}
	
	static ParsedData parseInputFile(String data){
		try(JsonReader reader = new JsonReader(new FileReader(data));){
			parsed = new Gson().fromJson(reader, ParsedData.class);
		} catch (IOException e){
			System.err.println("Input Error");
			System.exit(1);
		}
		return parsed;
	}
	
	static void buildAndStartPool(){
		WorkStealingThreadPool pool = new WorkStealingThreadPool(parsed.getThreads().intValue());
		attachWorkStealingThreadPool(pool);		
		pool.start();	
	}
	
	/**
	 * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	 * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	 */
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		pool = myWorkStealingThreadPool;
	}
	
	static void buildWarehouse(){
		theWarehouse = new Warehouse();
		//fill warehouse with tools
		for(ParsedTool parsedTool : parsed.getTools())
			addToolToWarehouse(parsedTool);
		//fill warehouse with plans
		for(Plan plan : parsed.getPlans()){
			theWarehouse.addPlan(new ManufactoringPlan(plan.getProduct() ,plan.getParts().toArray(new String[0]), plan.getTools().toArray(new String[0])));
		}
	}	
	
	static void addToolToWarehouse(ParsedTool parsedTool){
		String toolName = parsedTool.getTool();
		int toolQuantity = parsedTool.getQty();
		if(toolName.equals("rs-pliers"))
			theWarehouse.addTool(new RandomSumPliers(), toolQuantity);
		else if(toolName.equals("np-hammer"))
			theWarehouse.addTool(new NextPrimeHammer(), toolQuantity);
		else
			theWarehouse.addTool(new GcdScrewDriver(), toolQuantity);
	}	
	
	static String getInputFilePath(String[] args){
		if(args.length > 0)
			return args[0];
		return null;
	}

	static String getOutputFileName(String[] args){
		if(args.length > 1)
			return args[1];
		return "result.ser";
	}

	static void serializeOutputFile(ConcurrentLinkedQueue<Product> simulationResult, String serializedFileName){		
		try(FileOutputStream fout = new FileOutputStream(serializedFileName);
				ObjectOutputStream oos = new ObjectOutputStream(fout);){
			oos.writeObject(simulationResult);
		}catch(IOException e){
			System.err.println("Error serializing output file");
			e.printStackTrace();
		}		
	}
}
