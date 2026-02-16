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
    expected = f.readlines()

# Read actual
with open("/home/prithish/Lab1/supporting_files/actual_output.txt", "r") as f:
    actual = f.readlines()

# Find where "Main Memory Contents:" starts in actual output
start_idx = -1
for i, line in enumerate(actual):
    if "Main Memory Contents:" in line:
        start_idx = i
        break

if start_idx == -1:
    print("ERROR: Could not find 'Main Memory Contents:' in output")
    exit(1)

# Compare line by line
print("Expected lines:")
for i, line in enumerate(expected):
    print(f"{i}: {repr(line)}")

print("\nActual lines (from Main Memory Contents:):")
for i, line in enumerate(actual[start_idx:start_idx+len(expected)]):
    print(f"{i}: {repr(line)}")

print("\n\nComparison:")
for i in range(len(expected)):
    exp = expected[i]
    act = actual[start_idx + i] if start_idx + i < len(actual) else ""
    if exp == act:
        print(f"Line {i}: MATCH")
    else:
        print(f"Line {i}: MISMATCH")
        print(f"  Expected: {repr(exp)}")
        print(f"  Actual:   {repr(act)}")
