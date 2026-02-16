package processor.pipeline;
import generic.*;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	Instruction instruction;
	int aluResult;
	int Idresult;

	public MA_RW_LatchType()
	{
		RW_enable = false;
	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
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

	public void setIdresult(int Idresult){
		this.Idresult = Idresult;
	}

	public int getIdresult(){
		return Idresult;
	}
}