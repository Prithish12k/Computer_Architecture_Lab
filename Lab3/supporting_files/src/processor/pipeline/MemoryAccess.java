package processor.pipeline;
import generic.*;
import processor.Processor;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_enable()){

			// Taking instruction and alu from EX_MA latch
			Instruction inst = EX_MA_Latch.getInstruction();
			String op = inst.getOperationType().toString();
			int alu = EX_MA_Latch.getALu(); //alu is pcnow + immed i.e. address (for load and store)
			

			if(op.equals("load")){
				MA_RW_Latch.setIdresult(containingProcessor.getMainMemory().getWord(alu)); //storing load result to  latch
			}
			else if(op.equals("store")){
				//Storing word in source operand 1 in alu (address). Value is taken from registerfile
				containingProcessor.getMainMemory().setWord(alu, containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue())); 
			}


			//Not used for load and store are passed directly to next latch
			MA_RW_Latch.setInstruction(inst); //Sending instruction further
			MA_RW_Latch.setAlu(alu);
			
			MA_RW_Latch.setRW_enable(true);
			EX_MA_Latch.setMA_enable(false);
		}
	}

}