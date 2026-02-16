    .data
a:
    10
    .text
main:
    load %x0, $a, %x3
    addi %x0, 0, %x4
    addi %x3, 0, %x5
    jmp loop
loop:
    beq %x5, %x0, check
    muli %x4, 10, %x6
    divi %x5, 10, %x5
    add %x6, %x31, %x4
    jmp loop
check:
    beq %x4, %x3, palindrome
npalindrome:
    subi %x0, 1, %x10
    end
palindrome:
    addi %x0, 1, %x10
    end