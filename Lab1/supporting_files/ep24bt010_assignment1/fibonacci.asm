    .data
n:
    10
    .text
main:
    addi %x0, 65535, %x3
    load %x0, $n, %x4
    subi %x4, 1, %x4
    add %x0, %x0, %x5
    addi %x5, 1, %x6
    store %x5, 0, %x3
    subi %x3, 1, %x3
    store %x6, 0, %x3
    subi %x3, 1, %x3
    addi %x0, 1, %x7
loop:
    beq %x7, %x4, endl
    load %x3, 1, %x8
    load %x3, 2, %x9
    add %x8, %x9, %x10
    store %x10, 0, %x3
    subi %x3, 1, %x3
    addi %x7, 1, %x7
    jmp loop
endl:
    end
