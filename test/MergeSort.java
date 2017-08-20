/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * this class represents a merge sort task
 */

package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

	private final int[] array;
	private int startPos = 0, endPos;

	public MergeSort(int[] array) {
		this.array = array;
		endPos = array.length;
	}

	private MergeSort(int[] array, int start, int end) {
		this.array = array;
		startPos = start;
		endPos = end;
	}

	public String toString() {
		return (Integer.toString(this.hashCode()));
	}

	@Override
	protected void start() {
		List<Task<int[]>> tasks = new ArrayList<Task<int[]>>();
		if (endPos - startPos > 4) {
			int middle = startPos + ((endPos - startPos) >> 1);
			MergeSort left = new MergeSort(array, startPos, middle);
			MergeSort right = new MergeSort(array, middle, endPos);
			tasks.add(left);
			tasks.add(right);
			whenResolved(tasks, () -> {
				merge(middle);
				complete(array);
			});
			spawn(left, right);
		} else {
			Arrays.sort(array, startPos, endPos);
			complete(array);
		}
	}

	private void merge(int middle) {

		int[] copy = new int[endPos - startPos];
		System.arraycopy(array, startPos, copy, 0, copy.length);
		int copyLow = 0;
		int copyHigh = endPos - startPos;
		int copyMiddle = middle - startPos;
		// q>=copyHigh indicates that right part is DONE, so just push left part
		// remains
		// p < copyMiddle && copy[p] < copy[q] indicates that there are still
		// items on left side, and
		// it's value is less the what's in q position in the right array.
		for (int i = startPos, p = copyLow, q = copyMiddle; i < endPos; i++) {
			if (q >= copyHigh || (p < copyMiddle && copy[p] < copy[q])) {
				array[i] = copy[p++];
			} else {
				array[i] = copy[q++];
			}
		}
	}

	static int i = 0;
	static long[] result = new long[10];

	public static void main(String[] args) throws InterruptedException {

    	WorkStealingThreadPool pool = new WorkStealingThreadPool(42);
    	int n = 10; //you may check on different number of elements if you like
    	for(i = 0 ; i < 10 ; i ++){
    		int[] array = new Random().ints(n).toArray();
    		MergeSort task = new MergeSort(array);
    		CountDownLatch l = new CountDownLatch(1);
    		pool.start();
    		pool.submit(task);
    		long startTime = System.nanoTime();
    		task.getResult().whenResolved(() -> {
    			//warning - a large print!! - you can remove this line if you wish
    			long endTime = System.nanoTime();
    			System.out.println(Arrays.toString(task.getResult().get()));
    			long f = (endTime - startTime)/1000000;
    			System.out.println(f); 
    			result[i] = f;
    			l.countDown();
    	});
    	l.await();
    	pool.shutdown();
    	}
	}
}
