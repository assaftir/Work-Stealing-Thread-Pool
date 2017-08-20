package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {

	ConcurrentLinkedDeque<Task<?>>[] pQueues;
	Processor[] processors;
	Thread[] threads;
	VersionMonitor monitor;
	AtomicInteger stealingMonitor;
	int nThreads;
	
	/**
	 * creates a {@link WorkStealingThreadPool} which has nthreads
	 * {@link Processor}s. Note, threads should not get started until calling to
	 * the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */

	@SuppressWarnings("unchecked")
	public WorkStealingThreadPool(int nthreads) {
		
		if( nthreads == 0 ){ System.out.println("Shvita, Habayta!"); System.exit(0); }
		nThreads = nthreads;
		processors = new Processor[nthreads];
		threads = new Thread[nthreads];
		pQueues = new ConcurrentLinkedDeque[nthreads];
		monitor = new VersionMonitor();
		for (int i = 0; i < pQueues.length; i++) {
			pQueues[i] = new ConcurrentLinkedDeque<Task<?>>();
		}
	}


	/**
	 * submits a task to be executed by a processor belongs to this thread pool
	 *
	 * @param task
	 *            the task to execute
	 */
	public void submit(Task<?> task) {
		pQueues[ThreadLocalRandom.current().nextInt(0, nThreads)].add(task);
		monitor.inc();
	}
	/**
	 * closes the thread pool - this method interrupts all the threads and wait
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 * @throws UnsupportedOperationException
	 *             if the thread that attempts to shutdown the queue is itself a
	 *             processor of this queue
	 */
	public void shutdown() throws InterruptedException {
		for (int i = 0; i < threads.length; i++) {
			threads[i].interrupt();
			threads[i].join();
		}
	}
	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (int i = 0; i < processors.length; i++){
			processors[i] = new Processor(i, this);
			threads[i] = new Thread(processors[i]);
		}
		for (int i = 0; i < threads.length; i++)
			threads[i].start();
		
	}

	void sendToProcessor(int id, Task<?>... tasks){
		for(Task<?> task : tasks){
			pQueues[id].add(task);
		}
		monitor.inc();
	}

	Task<?> fetchNextForID(int myID){
		Task<?> fetched = null;
		while(fetched == null && !Thread.currentThread().isInterrupted()){
			fetched = pQueues[myID].pollFirst();
			if(fetched == null){
				try{
					tryStealingOrSleep(myID);
				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
		}
		return fetched;
	}
	void tryStealingOrSleep(int myID) throws InterruptedException{
		int currentVersion = monitor.getVersion();
		int firstVictim = (myID + 1) % nThreads;
		for(int i = firstVictim ; i != myID ; i = (i+1) % nThreads){
			double victimSize = pQueues[i].size();
			for(int j = 0 ; j < victimSize/2 ; j++){
				Task<?> stolenTask = pQueues[i].pollLast();
				if(stolenTask != null)
					pQueues[myID].add(stolenTask);
				else
					break;
			}
			if(!pQueues[myID].isEmpty())
				return;
		}
		monitor.await(currentVersion); //failed stealing
	}
}
