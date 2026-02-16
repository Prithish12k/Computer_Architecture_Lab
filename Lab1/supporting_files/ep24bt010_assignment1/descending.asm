    .data
a:
    70
    80
    40
    20
    10
    30
    50
    60
n:
    8
    .text
main:
    add %x0, %x0, %x3
    load %x0, $n, %x5
    subi %x5, 1, %x5
loop1:
    beq %x3, %x5, endl
    add %x0, %x0, %x4
    jmp loop2
loop2:
    beq %x4, %x5, increment
    load %x4, $a, %x7
    addi %x4, 1, %x4
    load %x4, $a, %x8
    blt %x7, %x8, swap
    jmp loop2
swap:
    subi %x4, 1, %x6
    store %x8, $a, %x6
    store %x7, $a, %x4
    jmp loop2
increment:
    addi %x3, 1, %x3
    jmp loop1
endl:
    end