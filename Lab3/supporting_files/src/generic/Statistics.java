package generic;

import java.io.PrintWriter;

public class Statistics {
	
	// TODO add your statistics here
	static int numberOfInstructions;
	static int numberOfCycles;
	static float ipc;
	static float cpi;

	public static void printStatistics(String statFile)
	{
		try
		{
			PrintWriter writer = new PrintWriter(statFile);
			
			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			
			// TODO add code here to print statistics in the output file
			writer.println("IPC = " + cpi);
			writer.println("CPI = " + cpi);
			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	// TODO write functions to update statistics


	//getters and setters of inst_num, cycle_num, cpi_ipc (ALL STATISTICS)

	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}

	public static int getNumberofInstruction(){
		return numberOfInstructions;
	}
	public static int getNumberofCycles(){
		return numberOfCycles;
	}
	public static void setIpc(int instruction_num, int cycles_num){
		ipc = (float)instruction_num/(float)cycles_num;
	}
	public static void setCpi(int instruction_num, int cycles_num) {
		cpi = (float)cycles_num / (float)instruction_num;
	}

	public static float getIpc() {
		return ipc;
	}

	public static float getCpi() {
		return cpi;
	}

}