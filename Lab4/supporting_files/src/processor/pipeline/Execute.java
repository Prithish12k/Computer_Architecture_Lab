package processor.pipeline;

import generic.*;
import generic.Operand.OperandType;
import processor.Processor;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch,
			EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}

	public void performEX() {
		// TODO
		boolean is_jumped = false;
		if (OF_EX_Latch.isEX_enable()) {

			// Working with instruction which we got from OF_EX_Latch()
			Instruction inst = OF_EX_Latch.getInstruction();
			EX_MA_Latch.setInstruction(inst);

			String opType = inst.getOperationType().toString();

			// programCounter from RegisterFile (In InstructionFetch.java pcnow was set as
			// (currentpc + 1))
			int pcnow = inst.getProgramCounter();

			// Finding alu
			int alu = 0;
			if (opType.equals("addi") || opType.equals("subi") || opType.equals("muli") || opType.equals("divi")
					|| opType.equals("andi") || opType.equals("ori") || opType.equals("xori") || opType.equals("slti")
					|| opType.equals("slli") || opType.equals("srli") || opType.equals("srai") || opType.equals("load")
					|| opType.equals("store")) {

				// getting values of register and immediates for operation R2I
				int rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue()); // Getting
																												// value
																												// from
																												// register
																												// file,
																												// register
																												// number
																												// stored
																												// in
																												// Operand
																												// (Which
																												// is
																												// set
																												// at
																												// RegisterWrite.java)
																												// (while
																												// x0 is
																												// defined
																												// initially
																												// in
																												// Simulator.java)
				int rd = containingProcessor.getRegisterFile().getValue(inst.getDestinationOperand().getValue());
				int immed = inst.getSourceOperand2().getValue();

				switch (opType) {
					case "addi":
						alu = rs1 + immed;
						break;
					case "subi":
						alu = rs1 - immed;
						break;
					case "muli":
						alu = rs1 * immed;
						break;
					case "divi":
						alu = rs1 / immed;
						EX_MA_Latch.setX31Result(rs1 % immed); // remainder stored in latch, written in RW
						break;
					case "andi":
						alu = rs1 & immed;
						break;
					case "xori":
						alu = rs1 ^ immed;
						break;
					case "ori":
						alu = rs1 | immed;
						break;
					case "slti":
						alu = 0;
						if (immed > rs1)
							alu = 1;
						break;
					case "slli":
						alu = rs1 << immed;
						EX_MA_Latch.setX31Result((immed > 0 && immed < 32) ? (rs1 >>> (32 - immed)) : 0); // shifted-out
																											// bits
						break;
					case "srai":
						alu = rs1 >> immed; // arithmetic shift right
						EX_MA_Latch.setX31Result((immed > 0 && immed < 32) ? (rs1 & ((1 << immed) - 1)) : 0); // shifted-out
																												// bits
						break;
					case "srli":
						alu = rs1 >>> immed; // logical shift right
						EX_MA_Latch.setX31Result((immed > 0 && immed < 32) ? (rs1 & ((1 << immed) - 1)) : 0); // shifted-out
																												// bits
						break;
					case "load":
						alu = rs1 + immed;
						break;
					case "store":
						alu = rd + immed; // We took value from rd specially for it.
						break;
					default:
						System.out.print("Problem in switch(OpType) for R2I");
				}

			} else if (opType.equals("add") || opType.equals("sub") || opType.equals("mul") || opType.equals("div")
					|| opType.equals("and") || opType.equals("or") || opType.equals("xor") || opType.equals("slt")
					|| opType.equals("sll") || opType.equals("srl") || opType.equals("sra")) {

				// Finding alu for register operations
				int rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
				int rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());

				switch (opType) {
					case "add":
						alu = rs1 + rs2;
						break;
					case "sub":
						alu = rs1 - rs2;
						break;
					case "mul":
						alu = rs1 * rs2;
						break;
					case "div":
						alu = rs1 / rs2;
						EX_MA_Latch.setX31Result(rs1 % rs2); // remainder stored in latch, written in RW
						break;
					case "and":
						alu = rs1 & rs2;
						break;
					case "xor":
						alu = rs1 ^ rs2;
						break;
					case "or":
						alu = rs1 | rs2;
						break;
					case "sll":
						alu = rs1 << rs2;
						EX_MA_Latch.setX31Result((rs2 > 0 && rs2 < 32) ? (rs1 >>> (32 - rs2)) : 0); // shifted-out bits
						break;
					case "slt":
						alu = 0;
						if (rs2 > rs1)
							alu = 1;
						break;
					case "sra":
						alu = rs1 >> rs2;
						EX_MA_Latch.setX31Result((rs2 > 0 && rs2 < 32) ? (rs1 & ((1 << rs2) - 1)) : 0); // shifted-out
																										// bits
						break;
					case "srl":
						alu = rs1 >>> rs2;
						EX_MA_Latch.setX31Result((rs2 > 0 && rs2 < 32) ? (rs1 & ((1 << rs2) - 1)) : 0); // shifted-out
																										// bits
						break;
					default:
						System.out.print("Issue in R3 switch");
				}
			}

			else if (opType.equals("jmp")) {

				// Specially for jmp
				OperandType jumpType = inst.getDestinationOperand().getOperandType();
				int immed = 0;

				// jmp instruction has either register or immediate as target
				if (jumpType == OperandType.Immediate) {
					immed = inst.getDestinationOperand().getValue();
				}

				else {
					immed = containingProcessor.getRegisterFile().getValue(inst.getDestinationOperand().getValue()); // Value
																														// corresponding
																														// to
																														// register
																														// (if
																														// present)
				}

				alu = immed + pcnow; // line number where we have to go.

				is_jumped = true;
				EX_IF_Latch.setBranchEnabled(true);
				EX_IF_Latch.setPC(alu);// Sending the value calculated to IF stage
				IF_OF_Latch.setOF_enable(false);
				Statistics.incrementWrongBranchCount();
				Statistics.incrementInstructions();
			} else if (opType.equals("end")) {
				// Do nothing (To separate it so that it does not go to default error line in
				// next else statement)
			} else {
				// Conditional statement

				// Setting register value
				int rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				int rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
				int immed = inst.getDestinationOperand().getValue();

				switch (opType) {
					case "bgt":
						if (rs1 > rs2) {
							alu = pcnow + immed;

							// Enabling EX_IF_LatchType
							is_jumped = true;
							EX_IF_Latch.setBranchEnabled(true);
							EX_IF_Latch.setPC(alu);
							IF_OF_Latch.setOF_enable(false);
							Statistics.incrementWrongBranchCount();
							Statistics.incrementInstructions();
						}
						break;
					case "beq":
						if (rs1 == rs2) {
							alu = pcnow + immed;

							is_jumped = true;
							EX_IF_Latch.setBranchEnabled(true);
							EX_IF_Latch.setPC(alu);
							IF_OF_Latch.setOF_enable(false);
							Statistics.incrementWrongBranchCount();
							Statistics.incrementInstructions();
						}
						break;
					case "blt":
						if (rs1 < rs2) {
							alu = pcnow + immed;

							is_jumped = true;
							EX_IF_Latch.setBranchEnabled(true);
							EX_IF_Latch.setPC(alu);
							IF_OF_Latch.setOF_enable(false);
							Statistics.incrementWrongBranchCount();
							Statistics.incrementInstructions();
						}
						break;
					case "bne":
						if (rs1 != rs2) {
							alu = pcnow + immed;

							is_jumped = true;
							EX_IF_Latch.setBranchEnabled(true);
							EX_IF_Latch.setPC(alu);
							IF_OF_Latch.setOF_enable(false);
							Statistics.incrementWrongBranchCount();
							Statistics.incrementInstructions();
						}
						break;
					default:
						System.out.print("Problem in conditional branch statements");
						break;
				}
			}

			EX_MA_Latch.setAlu(alu); // sending alu for MA

			OF_EX_Latch.setEX_enable(false); // Setting previous latch to be off

			// Next latch is turned on only if no branch statement
			if (is_jumped == false) {
				EX_MA_Latch.setMA_enable(true);
			} else {
				EX_MA_Latch.setMA_enable(false);
			}

		} else {
			EX_MA_Latch.setMA_enable(false);
		}
	}
}
