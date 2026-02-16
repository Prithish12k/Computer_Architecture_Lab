# Comprehensive Guide to Assembler Theory: From Assembly to Binary Machine Code

## Table of Contents
1. [Introduction](#introduction)
2. [What is an Assembler?](#what-is-an-assembler)
3. [The Big Picture: Assembly to Execution](#the-big-picture-assembly-to-execution)
4. [Assembly Language Fundamentals](#assembly-language-fundamentals)
5. [Understanding Memory Organization](#understanding-memory-organization)
6. [The Instruction Set Architecture (ISA)](#the-instruction-set-architecture-isa)
7. [Instruction Encoding: Assembly to Binary](#instruction-encoding-assembly-to-binary)
8. [The Two-Pass Assembly Process](#the-two-pass-assembly-process)
9. [Symbol Table and Label Resolution](#symbol-table-and-label-resolution)
10. [Object File Format](#object-file-format)
11. [Complete Example: Step-by-Step Assembly](#complete-example-step-by-step-assembly)
12. [Implementing the Assembler in Java](#implementing-the-assembler-in-java)

---

## Introduction

This document provides a comprehensive, ground-up explanation of how an **assembler** works—specifically, how it translates human-readable assembly language into binary machine code that a processor can execute. By the end of this guide, you should understand every step of the assembly process well enough to implement an assembler from scratch.

### Prerequisites for Lab 2
This guide is designed to give you all the theoretical knowledge needed to complete Lab 2, which involves implementing the `assemble()` method in `Simulator.java`. You will need to:
1. Read and parse assembly source files
2. Build a symbol table for labels
3. Encode each instruction into binary format
4. Write the binary object file

---

## What is an Assembler?

An **assembler** is a program that translates **assembly language** (a low-level, human-readable programming language) into **machine code** (binary instructions that a CPU can directly execute).

### The Translation Hierarchy

```
High-Level Language (C, Java, Python)
           ↓  (Compiler)
    Assembly Language
           ↓  (Assembler)  ← We focus here
      Machine Code (Binary)
           ↓  (Loader)
       Memory/CPU Execution
```

### Why Assembly Language Exists

**Machine code** is extremely difficult for humans to read and write:
```
Binary:   00000101 00110100 01010011
```

**Assembly language** provides human-readable mnemonics:
```assembly
add %x3, %x4, %x5    ; Add register x3 and x4, store in x5
```

The assembler's job is to bridge this gap by converting the human-readable assembly into the binary format the CPU understands.

---

## The Big Picture: Assembly to Execution

### Complete Workflow

```
┌──────────────────────────────────────────────────────────────┐
│  ASSEMBLY SOURCE FILE (.asm)                                 │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ .data                                                  │  │
│  │ n: 0 1                                                 │  │
│  │ .text                                                  │  │
│  │ main:                                                  │  │
│  │   load %x0, $n, %x3                                   │  │
│  │   add %x3, %x4, %x5                                   │  │
│  │   end                                                  │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼  ASSEMBLER (Two Passes)
┌──────────────────────────────────────────────────────────────┐
│  Pass 1: Build Symbol Table                                  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Symbol     │  Address                                 │  │
│  │  ───────────┼──────────                                │  │
│  │  n          │  0                                       │  │
│  │  main       │  2                                       │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                              │
│  Pass 2: Generate Machine Code                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  For each instruction:                                 │  │
│  │    1. Determine opcode                                 │  │
│  │    2. Encode operands (registers, immediates)          │  │
│  │    3. Resolve labels using symbol table                │  │
│  │    4. Output 32-bit binary instruction                 │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│  OBJECT FILE (.obj)                                          │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Binary Machine Code:                                  │  │
│  │  00000000 00000010  (data section marker)              │  │
│  │  00000000 00000000  (data: 0)                          │  │
│  │  00000000 00000001  (data: 1)                          │  │
│  │  [encoded instructions...]                             │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼  LOADER
┌──────────────────────────────────────────────────────────────┐
│  MAIN MEMORY                                                 │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Address 0:  [instruction or data]                     │  │
│  │  Address 1:  [instruction or data]                     │  │
│  │  Address 2:  [instruction or data]                     │  │
│  │  ...                                                   │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼  CPU EXECUTION
                        (Fetch-Decode-Execute)
```

---

## Assembly Language Fundamentals

### Structure of an Assembly File

An assembly file for this lab has two main sections:

```assembly
.data           ; DATA SECTION - Constants and variables
n:
    0           ; First value
    1           ; Second value

.text           ; CODE SECTION - Instructions
main:           ; Label marking the entry point
    load %x0, $n, %x3   ; First instruction
    add %x3, %x4, %x5   ; Second instruction
    end                  ; Terminate program
```

### 1. The `.data` Section

The `.data` section contains initialized data (constants and variables) that the program will use.

```assembly
.data
variableName:       ; Label for this data
    10              ; First integer value
    20              ; Second integer value (arrays continue)
anotherVar:
    100             ; Another labeled value
```

**Key Points:**
- Each label marks the starting address of data
- Multiple values after a label create an array
- Data is stored at consecutive memory addresses starting from **address 0**

### 2. The `.text` Section

The `.text` section contains the actual executable instructions.

```assembly
.text
main:                       ; Entry point label
    load %x0, $a, %x3       ; Load value from memory
    add %x3, %x4, %x5       ; Arithmetic operation
for:                        ; Loop label
    blt %x7, %x6, loop      ; Conditional branch
    end                     ; Program termination
loop:
    addi %x7, 1, %x7        ; Increment
    jmp for                 ; Unconditional jump
```

### 3. Labels

Labels are identifiers that mark positions in code or data:

```assembly
myLabel:        ; A label ends with a colon (:)
```

**Uses of Labels:**
1. **Data Labels**: Mark memory locations of variables (`$n` refers to address of n)
2. **Code Labels**: Mark jump/branch targets (`jmp for`, `beq %x1, %x2, endif`)

### 4. Registers

Registers are small, fast storage locations inside the CPU. In this ISA:

| Register | Purpose |
|----------|---------|
| `%x0`    | Always contains 0 (hardwired zero) |
| `%x1-%x30` | General purpose registers |
| `%x31`   | Special register (holds remainder after division) |

**Register Notation**: `%xN` where N is the register number (0-31)

### 5. Operand Types

Every instruction operates on **operands**. There are three types:

| Type | Example | Description |
|------|---------|-------------|
| **Register** | `%x3` | CPU register (0-31) |
| **Immediate** | `10` | Constant integer value |
| **Label** | `$n` or `loop` | Symbol reference (resolved to address) |

---

## Understanding Memory Organization

### Linear Memory Model

Memory is organized as a linear array of addressable locations:

```
Address     Content
───────────────────────────
   0        [Data or Instruction]
   1        [Data or Instruction]
   2        [Data or Instruction]
   3        [Data or Instruction]
   ...
```

### Memory Layout for Assembly Programs

```
┌─────────────────────────────────────────────┐
│     Memory Layout                           │
├─────────────────────────────────────────────┤
│  Address 0:  Data value 0                   │  ← .data section starts
│  Address 1:  Data value 1                   │
│  Address 2:  Data value 2                   │
│     ...                                     │
│  Address N:  First instruction (code start) │  ← .text section starts
│  Address N+1: Second instruction            │
│  Address N+2: Third instruction             │
│     ...                                     │
│  Address M:  Last instruction               │
└─────────────────────────────────────────────┘
```

### Example Memory Layout

For this assembly program:
```assembly
.data
n:
    0
    1
.text
main:
    load %x0, $n, %x3
    add %x3, %x4, %x5
    end
```

The memory layout would be:
```
Address 0:  0                       (n[0])
Address 1:  1                       (n[1])
Address 2:  [load instruction]      (main:)
Address 3:  [add instruction]
Address 4:  [end instruction]
```

The **firstCodeAddress** would be **2** (where the .text section begins).

---

## The Instruction Set Architecture (ISA)

### Instruction Categories

The ISA for this lab includes 30 instructions organized into categories:

#### 1. R3I Type (Register-Register-Register)
**Format**: `opcode rs1, rs2, rd`
- 3 register operands
- Result of operation on rs1 and rs2 stored in rd

| Instruction | Operation | Description |
|-------------|-----------|-------------|
| `add` | rd = rs1 + rs2 | Addition |
| `sub` | rd = rs1 - rs2 | Subtraction |
| `mul` | rd = rs1 × rs2 | Multiplication |
| `div` | rd = rs1 ÷ rs2 | Division (remainder in %x31) |
| `and` | rd = rs1 & rs2 | Bitwise AND |
| `or`  | rd = rs1 \| rs2 | Bitwise OR |
| `xor` | rd = rs1 ^ rs2 | Bitwise XOR |
| `slt` | rd = (rs1 < rs2) ? 1 : 0 | Set if less than |
| `sll` | rd = rs1 << rs2 | Shift left logical |
| `srl` | rd = rs1 >> rs2 | Shift right logical |
| `sra` | rd = rs1 >>> rs2 | Shift right arithmetic |

**Example**:
```assembly
add %x3, %x4, %x5   ; x5 = x3 + x4
```

#### 2. R2I Type (Register-Immediate-Register)
**Format**: `opcode rs1, imm, rd`
- 2 register operands + 1 immediate value
- Result of operation on rs1 and imm stored in rd

| Instruction | Operation | Description |
|-------------|-----------|-------------|
| `addi` | rd = rs1 + imm | Add immediate |
| `subi` | rd = rs1 - imm | Subtract immediate |
| `muli` | rd = rs1 × imm | Multiply immediate |
| `divi` | rd = rs1 ÷ imm | Divide immediate |
| `andi` | rd = rs1 & imm | Bitwise AND immediate |
| `ori`  | rd = rs1 \| imm | Bitwise OR immediate |
| `xori` | rd = rs1 ^ imm | Bitwise XOR immediate |
| `slti` | rd = (rs1 < imm) ? 1 : 0 | Set if less than immediate |
| `slli` | rd = rs1 << imm | Shift left immediate |
| `srli` | rd = rs1 >> imm | Shift right immediate |
| `srai` | rd = rs1 >>> imm | Shift right arithmetic imm |

**Example**:
```assembly
addi %x6, 10, %x6   ; x6 = x6 + 10
```

#### 3. Memory Instructions (R2I variant)
**Format**: `opcode rs1, offset/label, rd`

| Instruction | Operation | Description |
|-------------|-----------|-------------|
| `load` | rd = Memory[rs1 + offset] | Load from memory |
| `store` | Memory[rs1 + offset] = rd | Store to memory |

**Examples**:
```assembly
load %x0, $n, %x3   ; x3 = Memory[x0 + address_of_n] = Memory[0 + 0] = n[0]
load %x0, 1, %x4    ; x4 = Memory[x0 + 1] = n[1]
store %x7, 0, %x5   ; Memory[x7 + 0] = x5
```

#### 4. Branch Instructions (Conditional)
**Format**: `opcode rs1, rs2, label`

| Instruction | Condition | Description |
|-------------|-----------|-------------|
| `beq` | rs1 == rs2 | Branch if equal |
| `bne` | rs1 != rs2 | Branch if not equal |
| `blt` | rs1 < rs2 | Branch if less than |
| `bgt` | rs1 > rs2 | Branch if greater than |

**Example**:
```assembly
blt %x7, %x6, loop   ; if x7 < x6, jump to loop
```

#### 5. Jump Instruction (Unconditional)
**Format**: `jmp target`

| Instruction | Operation | Description |
|-------------|-----------|-------------|
| `jmp` | PC = target | Unconditional jump |

**Examples**:
```assembly
jmp for             ; Jump to label 'for'
jmp %x10            ; Jump to address in register x10
```

#### 6. Control Instruction
| Instruction | Description |
|-------------|-------------|
| `end` | Terminate program execution |

---

## Instruction Encoding: Assembly to Binary

### Understanding Binary Encoding

Each assembly instruction must be converted to a fixed-width binary format (typically 32 bits). The format must encode:

1. **Opcode**: Which operation to perform (5 bits for 30 instructions)
2. **Operands**: registers, immediates, or addresses

### Proposed Encoding Format (32-bit)

#### Opcode Assignment (5 bits: 0-31)

| Opcode (decimal) | Opcode (binary) | Instruction |
|------------------|-----------------|-------------|
| 0  | 00000 | add |
| 1  | 00001 | addi |
| 2  | 00010 | sub |
| 3  | 00011 | subi |
| 4  | 00100 | mul |
| 5  | 00101 | muli |
| 6  | 00110 | div |
| 7  | 00111 | divi |
| 8  | 01000 | and |
| 9  | 01001 | andi |
| 10 | 01010 | or |
| 11 | 01011 | ori |
| 12 | 01100 | xor |
| 13 | 01101 | xori |
| 14 | 01110 | slt |
| 15 | 01111 | slti |
| 16 | 10000 | sll |
| 17 | 10001 | slli |
| 18 | 10010 | srl |
| 19 | 10011 | srli |
| 20 | 10100 | sra |
| 21 | 10101 | srai |
| 22 | 10110 | load |
| 23 | 10111 | store |
| 24 | 11000 | jmp |
| 25 | 11001 | beq |
| 26 | 11010 | bne |
| 27 | 11011 | blt |
| 28 | 11100 | bgt |
| 29 | 11101 | end |

### Instruction Format Layouts

#### R3I Format (3 registers)
```
┌───────────┬──────────┬──────────┬──────────┬─────────────┐
│  opcode   │   rs1    │   rs2    │    rd    │   unused    │
├───────────┼──────────┼──────────┼──────────┼─────────────┤
│  5 bits   │  5 bits  │  5 bits  │  5 bits  │   12 bits   │
│  [31:27]  │ [26:22]  │  [21:17] │  [16:12] │   [11:0]    │
└───────────┴──────────┴──────────┴──────────┴─────────────┘
```

**Example**: `add %x3, %x4, %x5`
```
opcode(add) = 00000
rs1(x3)     = 00011
rs2(x4)     = 00100
rd(x5)      = 00101
unused      = 000000000000

Binary: 00000 00011 00100 00101 000000000000
        = 0000 0001 1001 0000 1010 0000 0000 0000
Hex:    = 0x01 90 A0 00
```

#### R2I Format (2 registers + immediate)
```
┌───────────┬──────────┬───────────────────────┬──────────┐
│  opcode   │   rs1    │      immediate        │    rd    │
├───────────┼──────────┼───────────────────────┼──────────┤
│  5 bits   │  5 bits  │       17 bits         │  5 bits  │
│  [31:27]  │ [26:22]  │       [21:5]          │  [4:0]   │
└───────────┴──────────┴───────────────────────┴──────────┘
```

**Example**: `addi %x6, 10, %x6`
```
opcode(addi) = 00001
rs1(x6)      = 00110
immediate    = 00000000000001010 (17-bit signed: 10)
rd(x6)       = 00110

Binary: 00001 00110 00000000000001010 00110
```

#### Branch Format
```
┌───────────┬──────────┬──────────┬───────────────────────┐
│  opcode   │   rs1    │   rs2    │    target offset      │
├───────────┼──────────┼──────────┼───────────────────────┤
│  5 bits   │  5 bits  │  5 bits  │       17 bits         │
│  [31:27]  │ [26:22]  │  [21:17] │       [16:0]          │
└───────────┴──────────┴──────────┴───────────────────────┘
```

**Note on Branch Targets**: The target can be:
- **Absolute address**: Jump directly to that address
- **PC-relative offset**: Current PC + offset

#### Jump Format
```
┌───────────┬──────────────────────────────────────────────┐
│  opcode   │              target address                  │
├───────────┼──────────────────────────────────────────────┤
│  5 bits   │                 27 bits                      │
│  [31:27]  │                 [26:0]                       │
└───────────┴──────────────────────────────────────────────┘
```

---

## The Two-Pass Assembly Process

Assemblers typically work in **two passes** over the source code. This is necessary because labels can be used before they are defined (forward references).

### Why Two Passes?

Consider this code:
```assembly
    jmp end_loop    ; Uses 'end_loop' BEFORE it's defined
    add %x1, %x2, %x3
end_loop:           ; 'end_loop' is defined here
    end
```

In a single pass, when we encounter `jmp end_loop`, we don't yet know the address of `end_loop`. The two-pass approach solves this.

### Pass 1: Symbol Table Construction

**Goal**: Scan the entire file and record the address of every label.

**Algorithm**:
```
1. Initialize address_counter = 0
2. Open and read the assembly file
3. For each line:
   a. If line contains ".data":
      - Continue reading data section
      - For each data label found:
        * Add label → address_counter to symbol_table
        * For each data value, increment address_counter
   
   b. If line contains ".text":
      - Record firstCodeAddress = address_counter
      - Continue reading code section
      - For each code label found:
        * Add label → address_counter to symbol_table
      - For each instruction (non-label line):
        * Increment address_counter
4. Return symbol_table and firstCodeAddress
```

**Example**:
```assembly
.data
n:          ; Symbol 'n' → address 0
    0       ; address 0, counter = 1
    1       ; address 1, counter = 2
.text       ; firstCodeAddress = 2
main:       ; Symbol 'main' → address 2
    load %x0, $n, %x3   ; address 2, counter = 3
for:        ; Symbol 'for' → address 3
    blt %x7, %x6, loop  ; address 3, counter = 4
    end                 ; address 4, counter = 5
loop:       ; Symbol 'loop' → address 5
    addi %x7, 1, %x7    ; address 5, counter = 6
    jmp for             ; address 6, counter = 7
```

**Resulting Symbol Table**:
| Symbol | Address |
|--------|---------|
| n      | 0       |
| main   | 2       |
| for    | 3       |
| loop   | 5       |

### Pass 2: Code Generation

**Goal**: Generate binary machine code for each instruction, resolving all labels using the symbol table.

**Algorithm**:
```
1. Reopen the assembly file
2. For each instruction in the .text section:
   a. Parse the instruction mnemonic and operands
   b. Determine the opcode
   c. For each operand:
      - If register: extract register number
      - If immediate: extract numeric value
      - If label: look up address in symbol_table
   d. Encode the instruction in binary format
   e. Write binary to output file
3. Close output file
```

---

## Symbol Table and Label Resolution

### Symbol Table Data Structure

The symbol table is a mapping from label names to memory addresses:

```java
HashMap<String, Integer> symtab = new HashMap<>();

// Adding entries
symtab.put("n", 0);
symtab.put("main", 2);
symtab.put("for", 3);
symtab.put("loop", 5);

// Looking up labels during code generation
int address = symtab.get("loop");  // Returns 5
```

### Label Resolution Process

When generating code for an instruction with a label operand:

1. **Extract the label name** from the operand (remove `$` prefix if present)
2. **Look up** the label in the symbol table
3. **Get the address** value
4. **Encode the address** into the instruction

**Example**:
```assembly
jmp for     ; Need to encode the address of 'for' (which is 3)
```

Resolution:
```java
String label = "for";
int targetAddress = symtab.get(label);  // Returns 3
// Now encode 3 as the target address in the jump instruction
```

---

## Object File Format

The object file contains the binary representation of the program, ready to be loaded into memory.

### Structure

```
┌────────────────────────────────────────┐
│  OBJECT FILE FORMAT                    │
├────────────────────────────────────────┤
│  1. Data Section                       │
│     - All initialized data values      │
│     - Each as a 32-bit integer         │
│                                        │
│  2. Code Section                       │
│     - Each instruction as 32 bits      │
│     - Instructions in program order    │
└────────────────────────────────────────┘
```

### Writing Binary Data

In Java, you write binary data using streams:

```java
DataOutputStream outputStream = new DataOutputStream(
    new FileOutputStream("output.obj")
);

// Write a 32-bit integer
outputStream.writeInt(instructionCode);

// Or write bytes
outputStream.write(byteArray);

outputStream.close();
```

### Byte Order (Endianness)

**Big Endian**: Most significant byte first
**Little Endian**: Least significant byte first

Example for integer `0x01234567`:
```
Big Endian:    01 23 45 67
Little Endian: 67 45 23 01
```

Java's `DataOutputStream.writeInt()` uses **Big Endian** by default.

---

## Complete Example: Step-by-Step Assembly

Let's trace through the complete assembly process for a simple program.

### Source Program (input.asm)

```assembly
.data
a:
    10
b:
    20
.text
main:
    load %x0, $a, %x3
    load %x0, $b, %x4
    add %x3, %x4, %x5
    end
```

### Pass 1: Build Symbol Table

**Scanning Data Section**:
```
Line: "a:"          → symbol "a" at address 0
Line: "10"          → store 10, address becomes 1
Line: "b:"          → symbol "b" at address 1
Line: "20"          → store 20, address becomes 2
```

**firstCodeAddress = 2**

**Scanning Text Section**:
```
Line: "main:"       → symbol "main" at address 2
Line: "load ..."    → instruction at address 2, address becomes 3
Line: "load ..."    → instruction at address 3, address becomes 4
Line: "add ..."     → instruction at address 4, address becomes 5
Line: "end"         → instruction at address 5, address becomes 6
```

**Symbol Table**:
| Symbol | Address |
|--------|---------|
| a      | 0       |
| b      | 1       |
| main   | 2       |

### Pass 2: Generate Machine Code

#### Instruction 1: `load %x0, $a, %x3`

```
Operation: load (opcode = 22 = 10110)
rs1: x0 (5 bits: 00000)
immediate/offset: address of 'a' = 0 (17 bits: 00000000000000000)
rd: x3 (5 bits: 00011)

Using R2I format:
Binary: 10110 00000 00000000000000000 00011
      = 1011 0000 0000 0000 0000 0000 0000 0011
Hex:  = 0xB0000003
```

#### Instruction 2: `load %x0, $b, %x4`

```
Operation: load (opcode = 22 = 10110)
rs1: x0 (00000)
immediate: address of 'b' = 1 (00000000000000001)
rd: x4 (00100)

Binary: 10110 00000 00000000000000001 00100
      = 1011 0000 0000 0000 0000 0010 0010 0100
```

#### Instruction 3: `add %x3, %x4, %x5`

```
Operation: add (opcode = 0 = 00000)
rs1: x3 (00011)
rs2: x4 (00100)
rd: x5 (00101)
unused: 000000000000

Binary: 00000 00011 00100 00101 000000000000
      = 0000 0001 1001 0000 1010 0000 0000 0000
Hex:  = 0x0190A000
```

#### Instruction 4: `end`

```
Operation: end (opcode = 29 = 11101)
No operands needed

Binary: 11101 000000000000000000000000000
Hex:  = 0xE8000000
```

### Final Object File Content

```
Address 0: 0x0000000A  (data: 10)
Address 1: 0x00000014  (data: 20)
Address 2: 0xB0000003  (load instruction)
Address 3: 0xB0000104  (load instruction)
Address 4: 0x0190A000  (add instruction)
Address 5: 0xE8000000  (end instruction)
```

---

## Implementing the Assembler in Java

### Overview of Implementation

You need to implement the `assemble()` method in `Simulator.java`. The existing code already provides:

1. `ParsedProgram.java` - Parses assembly files and builds the symbol table
2. `Instruction.java` - Represents a single instruction
3. `Operand.java` - Represents an operand (register, immediate, or label)

### Step-by-Step Implementation Guide

#### Step 1: Understand the Existing Code

The `ParsedProgram` class already:
- Parses the data section (`parseDataSection`)
- Parses the code section (`parseCodeSection`)
- Builds the symbol table (`symtab`)
- Creates `Instruction` objects (`code` ArrayList)

#### Step 2: Get Required Information

```java
public static void assemble() {
    // Access the parsed data
    ArrayList<Integer> data = ParsedProgram.data;           // Data values
    ArrayList<Instruction> code = ParsedProgram.code;       // Instructions
    HashMap<String, Integer> symtab = ParsedProgram.symtab; // Symbol table
    int firstCodeAddress = ParsedProgram.firstCodeAddress;  // Where code starts
    
    // Create output file
    DataOutputStream output = new DataOutputStream(
        new FileOutputStream("output.obj")
    );
    
    // ... encoding logic
}
```

#### Step 3: Write Data Section

```java
// Write data section first
for (Integer dataValue : data) {
    output.writeInt(dataValue);
}
```

#### Step 4: Encode Each Instruction

```java
for (Instruction instr : code) {
    int binaryCode = encodeInstruction(instr, symtab);
    output.writeInt(binaryCode);
}
```

#### Step 5: Implement encodeInstruction Method

```java
private static int encodeInstruction(Instruction instr, HashMap<String, Integer> symtab) {
    int opcode = getOpcode(instr.getOperationType());
    int rs1 = 0, rs2 = 0, rd = 0, immediate = 0;
    
    // Extract operand values
    Operand src1 = instr.getSourceOperand1();
    Operand src2 = instr.getSourceOperand2();
    Operand dest = instr.getDestinationOperand();
    
    // Handle each operand type
    if (src1 != null) {
        if (src1.getOperandType() == OperandType.Register) {
            rs1 = src1.getValue();
        }
    }
    
    if (src2 != null) {
        switch (src2.getOperandType()) {
            case Register:
                rs2 = src2.getValue();
                break;
            case Immediate:
                immediate = src2.getValue();
                break;
            case Label:
                immediate = symtab.get(src2.getLabelValue());
                break;
        }
    }
    
    if (dest != null) {
        switch (dest.getOperandType()) {
            case Register:
                rd = dest.getValue();
                break;
            case Immediate:
                immediate = dest.getValue();
                break;
            case Label:
                immediate = symtab.get(dest.getLabelValue());
                break;
        }
    }
    
    // Encode based on instruction type
    return assembleInstruction(opcode, rs1, rs2, rd, immediate, instr.getOperationType());
}
```

#### Step 6: Implement getOpcode Method

```java
private static int getOpcode(OperationType type) {
    switch (type) {
        case add: return 0;
        case addi: return 1;
        case sub: return 2;
        case subi: return 3;
        case mul: return 4;
        case muli: return 5;
        case div: return 6;
        case divi: return 7;
        case and: return 8;
        case andi: return 9;
        case or: return 10;
        case ori: return 11;
        case xor: return 12;
        case xori: return 13;
        case slt: return 14;
        case slti: return 15;
        case sll: return 16;
        case slli: return 17;
        case srl: return 18;
        case srli: return 19;
        case sra: return 20;
        case srai: return 21;
        case load: return 22;
        case store: return 23;
        case jmp: return 24;
        case beq: return 25;
        case bne: return 26;
        case blt: return 27;
        case bgt: return 28;
        case end: return 29;
        default: return -1;
    }
}
```

#### Step 7: Implement assembleInstruction Method

```java
private static int assembleInstruction(int opcode, int rs1, int rs2, int rd, 
                                        int immediate, OperationType type) {
    int result = 0;
    
    switch (type) {
        // R3I format: opcode[31:27] rs1[26:22] rs2[21:17] rd[16:12] unused[11:0]
        case add: case sub: case mul: case div: 
        case and: case or: case xor: case slt:
        case sll: case srl: case sra:
            result = (opcode << 27) | (rs1 << 22) | (rs2 << 17) | (rd << 12);
            break;
            
        // R2I format: opcode[31:27] rs1[26:22] immediate[21:5] rd[4:0]
        case addi: case subi: case muli: case divi:
        case andi: case ori: case xori: case slti:
        case slli: case srli: case srai:
        case load: case store:
            result = (opcode << 27) | (rs1 << 22) | ((immediate & 0x1FFFF) << 5) | (rd & 0x1F);
            break;
            
        // Branch format: opcode[31:27] rs1[26:22] rs2[21:17] target[16:0]
        case beq: case bne: case blt: case bgt:
            result = (opcode << 27) | (rs1 << 22) | (rs2 << 17) | (immediate & 0x1FFFF);
            break;
            
        // Jump format: opcode[31:27] target[26:0]
        case jmp:
            if (rd != 0) {  // Register jump
                result = (opcode << 27) | (rd << 22);  // Store register in rs1 field
            } else {
                result = (opcode << 27) | (immediate & 0x7FFFFFF);
            }
            break;
            
        // End: just opcode
        case end:
            result = (opcode << 27);
            break;
    }
    
    return result;
}
```

### Complete Implementation Template

Here's a complete template you can use:

```java
package generic;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import generic.Instruction.OperationType;
import generic.Operand.OperandType;

public class Assembler {
    
    public static void assemble(String objectFileName) {
        try {
            DataOutputStream output = new DataOutputStream(
                new FileOutputStream(objectFileName)
            );
            
            // Write data section
            for (Integer value : ParsedProgram.data) {
                output.writeInt(value);
            }
            
            // Write code section
            for (Instruction instr : ParsedProgram.code) {
                int binaryCode = encodeInstruction(instr);
                output.writeInt(binaryCode);
            }
            
            output.close();
            System.out.println("Assembly complete. Output written to: " + objectFileName);
            
        } catch (IOException e) {
            System.err.println("Error writing object file: " + e.getMessage());
        }
    }
    
    private static int encodeInstruction(Instruction instr) {
        // ... implement as shown above
    }
    
    private static int getOpcode(OperationType type) {
        // ... implement as shown above
    }
}
```

---

## Summary

### Key Concepts Recap

1. **Assembler**: Translates assembly language to binary machine code
2. **Two-Pass Process**: First builds symbol table, then generates code
3. **Symbol Table**: Maps labels to memory addresses
4. **Instruction Encoding**: Packs opcode and operands into 32-bit format
5. **Memory Layout**: Data section followed by code section

### Assembly Process Checklist

- [ ] Parse the `.data` section
- [ ] Build symbol table from data labels
- [ ] Parse the `.text` section
- [ ] Build symbol table from code labels
- [ ] For each instruction:
  - [ ] Determine opcode
  - [ ] Identify operand types
  - [ ] Resolve labels to addresses
  - [ ] Encode into binary format
- [ ] Write binary output file

### Files You Will Modify

| File | Purpose |
|------|---------|
| `Simulator.java` | Implement `assemble()` method |

### Files You Should Understand

| File | Purpose |
|------|---------|
| `Instruction.java` | Instruction representation with OperationType enum |
| `Operand.java` | Operand representation with OperandType enum |
| `ParsedProgram.java` | Parsing logic and symbol table |
| `Configuration.java` | Configuration reading |

---

## Appendix A: Quick Reference - Instruction Encoding

### R3I Type (3 Registers)
```
| 5-bit opcode | 5-bit rs1 | 5-bit rs2 | 5-bit rd | 12-bit unused |
```
Instructions: add, sub, mul, div, and, or, xor, slt, sll, srl, sra

### R2I Type (2 Registers + Immediate)
```
| 5-bit opcode | 5-bit rs1 | 17-bit immediate | 5-bit rd |
```
Instructions: addi, subi, muli, divi, andi, ori, xori, slti, slli, srli, srai, load, store

### Branch Type
```
| 5-bit opcode | 5-bit rs1 | 5-bit rs2 | 17-bit target |
```
Instructions: beq, bne, blt, bgt

### Jump Type
```
| 5-bit opcode | 27-bit target |
```
Instructions: jmp

---

## Appendix B: Debugging Tips

1. **Print the symbol table** after Pass 1 to verify labels are correct
2. **Print each instruction** before encoding to check parsing
3. **Use hex output** to verify binary encoding: `System.out.printf("0x%08X%n", encoded)`
4. **Compare with expected output** using a hex viewer/diff tool
5. **Test with simple programs first** before complex ones

---

## Appendix C: Common Mistakes

1. **Off-by-one errors** in address calculation
2. **Forgetting to handle labels** that start with `$`
3. **Wrong bit positions** in encoding
4. **Not handling negative immediates** (sign extension)
5. **Mixing up source and destination** operand order
6. **Not closing file streams** properly
