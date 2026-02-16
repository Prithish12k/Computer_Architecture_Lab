#!/usr/bin/env python3
import subprocess

# Run the emulator
result = subprocess.run(
    ["java", "-jar", "emulator.jar", "ep24bt010_assignment1/fibonacci.asm", "65526", "65535"],
    capture_output=True,
    text=True,
    cwd="/home/prithish/Lab1/supporting_files"
)

# Save output
with open("/home/prithish/Lab1/supporting_files/actual_output.txt", "w") as f:
    f.write(result.stdout)

# Read expected
with open("/home/prithish/Lab1/supporting_files/test_cases/fibonacci_1.expected", "r") as f:
    expected_lines = f.readlines()

# Read actual
with open("/home/prithish/Lab1/supporting_files/actual_output.txt", "r") as f:
    actual_lines = f.readlines()

print(f"Expected file has {len(expected_lines)} lines")
print(f"Actual output has {len(actual_lines)} lines")

# Simulate the evaluation logic
expected_file = open("/home/prithish/Lab1/supporting_files/test_cases/fibonacci_1.expected")
result_file = open("/home/prithish/Lab1/supporting_files/actual_output.txt")

expected_line = expected_file.readline()
first_line_found = False
evaluation = True

line_num = 0
for line in result_file:
    line_num += 1
    print(f"\nLine {line_num}: {repr(line)}")
    print(f"  Expected: {repr(expected_line)}")
    print(f"  first_line_found: {first_line_found}")
    
    if first_line_found == True and line != expected_line:
        print(f"  -> MISMATCH! Evaluation failed.")
        evaluation = False
        break
    if expected_line == line:
        print(f"  -> Match! Reading next expected line...")
        first_line_found = True
        expected_line = expected_file.readline()
        print(f"  -> Next expected: {repr(expected_line)}")
        if expected_line == None or expected_line == "":
            print(f"  -> No more expected lines, breaking")
            break

if first_line_found == False:
    evaluation = False
    print("\nERROR: first_line_found is False!")

expected_file.close()
result_file.close()

print(f"\n\nFinal evaluation: {evaluation}")
