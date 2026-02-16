	.data
a:
	10
	.text
main:
	load %x0, $a, %x3
	andi %x3, 1, %x4
	beq %x4, %x0, EVEN
ODD:
	addi %x0, 1, %x10
	end
EVEN:
	subi %x0, 1, %x10
	end
