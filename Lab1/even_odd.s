	.data
a:
	11

	.text
main:
	load %x0, $a, %x3
	andi %x3, 1, %x4
	beq %x0, %x4, EVEN

ODD:
	addi %x0, %x10, 1
	end
EVEN:
	subi %x0, %x10, -1
	end
