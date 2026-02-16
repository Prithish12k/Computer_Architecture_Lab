package generic;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import processor.Clock;
import processor.Processor;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{
		/*
		 *
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		InputStream fileInputStream=null;
		try{
			fileInputStream= new FileInputStream(assemblyProgramFile);
			DataInputStream dataStream = new DataInputStream(fileInputStream);

			// Taking first four bytes as Program counter
			int next=0;
			if(dataStream.available()>0){ //dataStream contains data
				next = dataStream.readInt();
				processor.getRegisterFile().setProgramCounter(next); //setting program counter in register file ()
			}

			// Subsequent bits stored in the memory
			for(int i=0;dataStream.available()>0;i++){
				next=dataStream.readInt();
				processor.getMainMemory().setWord(i, next); //Putting values in the memory (This helps in getWord(alu) for load in pipeline.MemoryAccess )
			}
			
			// Setting x0, x1, x2 as required
			processor.getRegisterFile().setValue(0, 0);
			processor.getRegisterFile().setValue(1,65535);
			processor.getRegisterFile().setValue(2, 65535);
			dataStream.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		 
	}
	
	public static void simulate()
	{
		int instruction_num = 0;
		int cycles_num = 0;
		while(simulationComplete == false)
		{
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			processor.getOFUnit().performOF();
			Clock.incrementClock();
			processor.getEXUnit().performEX();
			Clock.incrementClock();
			processor.getMAUnit().performMA();
			Clock.incrementClock();
			processor.getRWUnit().performRW();
			Clock.incrementClock();

			++instruction_num;
			++cycles_num;
		}

		// set statistics
		Statistics.setNumberOfInstructions(instruction_num);
		Statistics.setNumberOfCycles(cycles_num);
		Statistics.setCpi(instruction_num, cycles_num);
		Statistics.setIpc(instruction_num, cycles_num);
		
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
}