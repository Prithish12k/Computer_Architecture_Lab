package processor.pipeline;

public class EX_IF_LatchType {
	boolean isBranchEnabled;
	int pc;

	public EX_IF_LatchType()
	{
		isBranchEnabled = false;
	}
	
	public boolean isBranchEnabled() {
		return isBranchEnabled;
	}

	public void setBranchEnabled(boolean isBranchEnabled) {
		this.isBranchEnabled = isBranchEnabled;
	}

	public void setPC(int pc){
		this.pc =pc;
	}

	public int getPC(){
		return pc;
	}
}