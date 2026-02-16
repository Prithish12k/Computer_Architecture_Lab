package processor.pipeline;

import generic.*;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import processor.Processor;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}
	
	private static OperationType getOperationType(String opCode) {
    switch (opCode) {
        case "00000": return OperationType.add;
        case "00001": return OperationType.addi;
        case "00010": return OperationType.sub;
        case "00011": return OperationType.subi;
        case "00100": return OperationType.mul;
        case "00101": return OperationType.muli;
        case "00110": return OperationType.div;
        case "00111": return OperationType.divi;
        case "01000": return OperationType.and;
        case "01001": return OperationType.andi;
        case "01010": return OperationType.or;
        case "01011": return OperationType.ori;
        case "01100": return OperationType.xor;
        case "01101": return OperationType.xori;
        case "01110": return OperationType.slt;
        case "01111": return OperationType.slti;
        case "10000": return OperationType.sll;
        case "10001": return OperationType.slli;
        case "10010": return OperationType.srl;
        case "10011": return OperationType.srli;
        case "10100": return OperationType.sra;
        case "10101": return OperationType.srai;
        case "10110": return OperationType.load;
        case "10111": return OperationType.store;
        case "11000": return OperationType.jmp;
        case "11001": return OperationType.beq;
        case "11010": return OperationType.bne;
        case "11011": return OperationType.blt;
        case "11100": return OperationType.bgt;
        case "11101": return OperationType.end;
        default:
            Misc.printErrorAndExit("Unknown opcode for: " + opCode);
            return null; 
    }
}

	public String complementOf2(String binary){
		String ones="";
		for (int i=0;i<binary.length();i++){
			ones+=(binary.charAt(i) == '0') ? '1' : '0';
		}
	

	StringBuilder builder = new StringBuilder(ones);
	boolean one_added = false;

	for (int i = ones.length() - 1; i>0; i--){
		if(ones.charAt(i)=='1'){
			builder.setCharAt(i, '0');
		}
		else{
			builder.setCharAt(i, '1');
			one_added = true;
			break;	
		}
	}
	if(one_added == false){
		builder.append("1",0,7);
	}
	return builder.toString(); // returned twos complement
	}


	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			Instruction inst = new Instruction();
			// Converting instruction in int to binary
			String binInst = Integer.toBinaryString(IF_OF_Latch.getInstruction());

			//Adjusting binary size to 32 bits
			int binary_size = binInst.length();
			for (int i=0; i< 32-binary_size;i++){
				binInst = '0' + binInst;
			}

			//Extracting opcode
			String opCode = binInst.substring(0,5);
			
			//Get operation type of instruction
			OperationType opInst = getOperationType(opCode);
			
			//Setting operation type to the instruction
			inst.setOperationType(opInst);

			switch(opInst){
				case sub:
				case add:
				case div:
				case and:
				case mul:
				case or:
				case slt:
				case sll:
				case sra:
				case xor:
				case srl:
					Operand rd = new Operand();
					Operand rs1 = new Operand();
					Operand rs2 = new Operand();
					rd.setOperandType(OperandType.Register);					
					rs1.setOperandType(OperandType.Register);
					rs2.setOperandType(OperandType.Register);
					
					//Setting values
					rs1.setValue(Integer.parseInt(binInst.substring(5,10),2)); //set the value of register i.e. x0 === 00000 (register number and not value)
					rs2.setValue(Integer.parseInt(binInst.substring(10,15),2));
					rd.setValue(Integer.parseInt(binInst.substring(15,20),2));

					inst.setDestinationOperand(rd);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					break;
				case addi:
				case ori:
				case andi:
				case divi:
				case muli:
				case slli:
				case srli:
				case slti:
				case subi:
				case srai:
				case xori:
				case store:
				case load:
					rd = new Operand();
					rs1 = new Operand();
					rs2 = new Operand();

					rd.setOperandType(OperandType.Immediate);
					rs1.setOperandType(OperandType.Register);
					rs2.setOperandType(OperandType.Register);

					rs1.setValue(Integer.parseInt(binInst.substring(5,10),2)); //set the value of register i.e. x0 === 00000
					rs2.setValue(Integer.parseInt(binInst.substring(10,15),2));
					
					String immed = "";
					immed = binInst.substring(15,32);

					if(immed.charAt(0)=='1'){
						immed = complementOf2(immed);
						rd.setValue(-1*Integer.parseInt(immed,2));
					}
					else{
						rd.setValue(Integer.parseInt(immed,2));
					}
					inst.setDestinationOperand(rs2);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rd);
					break;
				case jmp:
					//Immediate
					Operand imme = new Operand();
					imme.setOperandType(OperandType.Immediate);
					
					//Register
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					rs1.setValue(Integer.parseInt(binInst.substring(5, 10),2));
					
					//Immediate converting to integer(2 cases - +ve or -ve)
					String immedi = binInst.substring(10, 32);
					if(immedi.charAt(0)=='1'){
						immedi = complementOf2(immedi);
						imme.setValue(-1*Integer.parseInt(immedi,2));
					}
					else{
						imme.setValue(Integer.parseInt(immedi,2));
					}

					//Assigning values to instruction operands
					inst.setSourceOperand1(rs1);
					inst.setDestinationOperand(imme);
					break;
				case beq:
				case bne:
				case blt:
				case bgt:
					imme = new Operand();
					rs1 = new Operand();
					rs2 = new Operand();
					
					//registers taking value from binInst and then setting the value in rs1 and rs2
					imme.setOperandType(OperandType.Immediate);
					rs1.setOperandType(OperandType.Register);
					rs2.setOperandType(OperandType.Register);
					
					rs1.setValue(Integer.parseInt(binInst.substring(5, 10),2));
					rs2.setValue(Integer.parseInt(binInst.substring(10, 15),2));
					
					//Immediate convertin to integer (2 cases - +ve and -ve)
					immedi = binInst.substring(15, 32);
					
					if(immedi.charAt(0)=='1'){
						immedi = complementOf2(immedi);
						imme.setValue(-1*Integer.parseInt(immedi,2));
					}
					else{
						imme.setValue(Integer.parseInt(immedi,2));
					}
					inst.setDestinationOperand(imme);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					break;
				case end:
					break;
				default:
					break;
			}

			OF_EX_Latch.setInstruction(inst);			
			OF_EX_Latch.setEX_enable(true);
			IF_OF_Latch.setOF_enable(false);
		}
	}

}