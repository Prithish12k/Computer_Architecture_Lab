    .data
a:
    59
    .text
main:
    load %x0, $a, %x3
    addi %x3, 0, %x4
    addi %x0, 2, %x5
    blt %x3, %x5, nonprime
	beq %x3, %x5, prime
loop:
    beq %x5, %x3, prime
    div %x4, %x5, %x8
    beq %x31, %x0, nonprime
    addi %x5, 1, %x5
    jmp loop
nonprime:
    subi %x0, 1, %x10
    end
prime:
    addi %x0, 1, %x10
    end