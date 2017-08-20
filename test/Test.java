package bgu.spl.a2.test;
import java.util.Calendar;
import bgu.spl.a2.sim.*;

public class Test {
	
	public static void main(String[] args) throws InterruptedException{
		for(int i = 0 ; i < 10000 ; i++){
			System.out.println("------Run #" + i +"------\n----Start time: "+ Calendar.getInstance().get(Calendar.MILLISECOND) + "----\n");
			MergeSort.main(args);
			System.out.println("\n----End Time: " + Calendar.getInstance().get(Calendar.MILLISECOND) + "----\n----------------------------");
		}
	}
}
