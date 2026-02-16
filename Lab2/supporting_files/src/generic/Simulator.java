package generic;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import generic.Instruction.OperationType;
import generic.Operand.OperandType;

public class Simulator {

	static FileInputStream inputcodeStream = null;
	static String objectFilePath;

	public static void setupSimulation(String assemblyProgramFile, String objectProgramFile) {
		objectFilePath = objectProgramFile;

		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}

	public static void assemble() {
		// TODO your assembler code
		try {
			String ObjectFilePath = objectFilePath;

			FileOutputStream fos = new FileOutputStream(ObjectFilePath);
			DataOutputStream dos = new DataOutputStream(fos);

			dos.writeInt(ParsedProgram.firstCodeAddress);

			for (Integer dataValue : ParsedProgram.data) {
				dos.writeInt(dataValue);
			}

			for (Instruction instr : ParsedProgram.code) {
				int encodedInstr = encodeInstruction(instr);
				dos.writeInt(encodedInstr);
			}

			dos.close();
			fos.close();

			System.out.println("Assembly successful!");
		} catch (Exception e) {
			Misc.printErrorAndExit("Error writing object file: " + e);
		}
	}

	private static int getOpcode(OperationType OpType) {
		switch (OpType) {
			case add:
				return 0;
			case addi:
				return 1;
			case sub:
				return 2;
			case subi:
				return 3;
			case mul:
				return 4;
			case muli:
				return 5;
			case div:
				return 6;
			case divi:
				return 7;
			case and:
				return 8;
			case andi:
				return 9;
			case or:
				return 10;
			case ori:
				return 11;
			case xor:
				return 12;
			case xori:
				return 13;
			case slt:
				return 14;
			case slti:
				return 15;
			case sll:
				return 16;
			case slli:
				return 17;
			case srl:
				return 18;
			case srli:
				return 19;
			case sra:
				return 20;
			case srai:
				return 21;
			case load:
				return 22;
			case store:
				return 23;
			case jmp:
				return 24;
			case beq:
				return 25;
			case bne:
				return 26;
			case blt:
				return 27;
			case bgt:
				return 28;
			case end:
				return 29;
			default:
				return -1;
		}
	}

	private static int encodeInstruction(Instruction instr) {
		int opcode = getOpcode(instr.getOperationType());
		int binary = 0;

		switch (instr.getOperationType()) {
			// R3I-Type Instructions
			case add:
			case sub:
			case mul:
			case div:
			case and:
			case or:
			case xor:
			case slt:
			case sll:
			case srl:
			case sra: {
				int rs1 = instr.getSourceOperand1().getValue();
				int rs2 = instr.getSourceOperand2().getValue();
				int rd = instr.getDestinationOperand().getValue();
				binary = (opcode << 27) | (rs1 << 22) | (rs2 << 17) | (rd << 12);
				break;
			}
			// R2I-Type Instructions
			case addi:
			case subi:
			case muli:
			case divi:
			case andi:
			case ori:
			case xori:
			case slti:
			case slli:
			case srli:
			case srai:
			case load:
			case store: {
				int rs1 = instr.getSourceOperand1().getValue();
				int rd = instr.getDestinationOperand().getValue();

				int immediate = 0;
				Operand op2 = instr.getSourceOperand2();
				if (op2.getOperandType() == OperandType.Immediate) {
					immediate = op2.getValue();
				} else if (op2.getOperandType() == OperandType.Label) {
					immediate = ParsedProgram.symtab.get(op2.getLabelValue());
				}

				immediate = immediate & 0x1FFFF;

				binary = (opcode << 27) | (rs1 << 22) | (rd << 17) | immediate;
				break;
			}

			// branch Instructions (R2I-Type)
			case beq:
			case bne:
			case blt:
			case bgt: {
				int rs1 = instr.getSourceOperand1().getValue();
				int rs2 = instr.getSourceOperand2().getValue();

				int immediate = 0;
				Operand opi = instr.getDestinationOperand();
				if (opi.getOperandType() == OperandType.Immediate) {
					immediate = opi.getValue();
				} else if (opi.getOperandType() == OperandType.Label) {
					immediate = ParsedProgram.symtab.get(opi.getLabelValue());
					int currentPC = instr.getProgramCounter();
					immediate = immediate - currentPC;
				}

				// for baranch instructions with labels, PC = PC + immediate (labelAddress)
				// say PC = 7 and we want to jump to address 3, then immediate needs to be -4,
				// not 3,
				// but immediate is passed as 3, so we need to subtract PC from immediate, 3 - 7
				// = -4
				// thats why we calculate offset

				immediate = immediate & 0x1FFFF;

				binary = (opcode << 27) | (rs1 << 22) | (rs2 << 17) | immediate;
				break;
			}
			// RI-Type Instructions
			case jmp: {
				int operand = 0;
				Operand opr = instr.getDestinationOperand();
				int rd = 0;

				if (opr.getOperandType() == OperandType.Register) {
					rd = opr.getValue();
				} else if (opr.getOperandType() == OperandType.Immediate) {
					operand = opr.getValue();
				} else if (opr.getOperandType() == OperandType.Label) {
					operand = ParsedProgram.symtab.get(opr.getLabelValue());
					int currentPC = instr.getProgramCounter();
					operand = operand - currentPC;
				}
				// Same here as branch instructions, we need to calculate offset for label
				// operands.
				// Mask to 22 bits
				operand = operand & 0x3FFFFF;

				binary = (opcode << 27) | (rd << 22) | operand;
				break;
			}
			case end: {
				binary = opcode << 27;
				break;
			}
		}
		return binary;
	}
}
