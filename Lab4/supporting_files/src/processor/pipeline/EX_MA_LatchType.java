package processor.pipeline;
import generic.*;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	Instruction instruction;
	int aluResult;

	public EX_MA_LatchType()
	{
		MA_enable = false;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}

	public void setInstruction(Instruction instruction){
		this.instruction = instruction;
	}

	public Instruction getInstruction(){
		return instruction;
	}

	public void setAlu(int aluResult){
		this.aluResult = aluResult;
	}

	public int getALu(){
		return aluResult;
	}
}