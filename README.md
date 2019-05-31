# keystroke-dynamics-auth

Behavioral biometrics authentification designed to distinguish users using their typing pattern.

# Features
1. Check if the users pattern already exists. If not - train the program and create.
2. Authenticate the user by his pattern.

# The mathematics behind this

- By typing 10 times and collecting intervals user creates a matrix of them with size 10x10
- The program transposes the matrix
- Calculate the matematical expectation for each element of the transposed matrix
- Calculate dispersion for each element
- Calculate Student's t-distribution for each element
- Compare the calculated coefficient with table value and if it bigger, set the interval = -1
- Calculate the mathematical expectation, now with fixed matrix including intervlas with -1 and for each line
- Calculate dispersion for each line
- Calculate time borders for each interval of user input
