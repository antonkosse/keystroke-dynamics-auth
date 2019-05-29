# keystroke-dynamics-auth

Behavioral biometrics authentification designed to distinguish users using their typing pattern.

# Features
1. Check if the users pattern already exists. If not - train the program and create.
2. Authenticate the user by his pattern.

# The math is behind this

- By typing 10 times and collecting interlvals user creates a matrix of intervals with size 10x10
- The program transposes the matrix
- Count the matematical expectation for each element of the transposed matrix
- Count dispersion for each element
- Count student coefficient for each element
- Compare the calculated coefficient with table value and if it bigger, set the interval = -1
- Count the mathematical expectation, now with fixed matrix including intervlas with -1 and for each line
- Count dispersion for each line
- Count time borders for each interval for the user input
