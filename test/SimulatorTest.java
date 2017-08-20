package bgu.spl.a2.test;

import bgu.spl.a2.sim.*;

public class SimulatorTest {
	
	public static void main(String[] args) throws InterruptedException{
		String[] simulatorParams = {"input .json file (Warehouse init state and plans)"};
		//Simulate 100 times
		for(int i = 0 ; i < 100 ; i++){
			String fileName = "Simulation_Output_"+Integer.toString(i);
			System.out.println("Run #" +i);
			Simulator.main(simulatorParams);
		}
	}
}
