## Programs using OpenMP

##### sdd.c
Checks if a NxN matrix is strictly diagonial dominant. The N dimension and the elements of the matrix and the number of threads are given by user.
If the matrix is SDD, then program will do the following:
- Find the maximum element of its principal diagonal.
- Create a new matrix based on the previous computations.
- Find the minimum element of the new matrix using reduction & critical section.

All computations are being done in parallel sections with time counting.
# 
##### multisort.c
Given an 1D array of integers, the program will assign to a single thread to call the multisort function, where the array will be sorted in parallel using OpenMP tasks. 
Each task receives a quarter of the initial array and calls recursively the multisort function until the size of the quarters is less than 4. 
In this case, integers will be sorted using standard library's qsort function. Then each thread will wait for the others and the sub-arrays will be merged by 2.