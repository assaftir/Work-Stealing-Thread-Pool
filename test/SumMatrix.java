package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SumMatrix extends Task<int[]> {
	
	private int[][] array;
	
	public SumMatrix(int[][] array){
		this.array = array;
	}
	
	public String toString(){return "Big Matrix Task";}
	
	protected void start(){
		
		List<Task<Integer>> tasks = new ArrayList<>();
		int rows = array.length;
		for(int i = 0 ; i<rows ; i++){
			SumRow newTask = new SumRow(array, i);
			spawn(newTask);
			tasks.add(newTask);
		}
		//Send my callback (continuation) to run when ALL tasks are done, register to each task Deferred!
		//Only one of them will finally run the callback (the last one that is done)
		whenResolved(tasks, () -> {
			int[] res = new int[rows];
			for(int j = 0 ; j < rows ; j++){
				res[j]=tasks.get(j).getResult().get();
			}
			complete(res);
		});
	}

	public static void main(String[] args) throws InterruptedException{
		WorkStealingThreadPool pool = new WorkStealingThreadPool(Integer.parseInt(args[0]));
		int[][] array = new int[Integer.parseInt(args[1])][Integer.parseInt(args[2])];

		for(int i = 0 ; i < array.length ; i++){
			for(int j = 0 ; j< array[0].length ; j++){
				array[i][j] = i;
			}
		}
		SumMatrix myTask = new SumMatrix(array);
		CountDownLatch l = new CountDownLatch(1);
		pool.start();
		pool.submit(myTask);
		myTask.getResult().whenResolved(() -> {
			System.out.println("Done.");
			l.countDown();
		});

		l.await();
		pool.shutdown();

	}
}
