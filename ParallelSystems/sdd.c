#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <omp.h>
  
#define CHUNKSIZE 2 

int main(int argc, char *argv[])
{
	//matrix dimension is given as argument
	if (argc !=2 || atoi(argv[1]) <= 1) {
		printf("N must be greater than 1.\n. Execute program with N as argument.\n");
		exit(1);
	}
	
	int N = atoi(argv[1]);
	srand(time(NULL));
	
	int i, j, **A;
	
	A = (int**)malloc(N*sizeof(int*));
	if (A == NULL) {
		printf("No available memory.\n");
		exit(1);
	}
	for (i = 0; i < N; i++) {
		A[i] = (int*)malloc(N*sizeof(int));
		if (A[i] == NULL) {
			printf("No available memory.\n");
			exit(1);
		}
	}
	
	for (i = 0; i < N; i++) {
		for (j = 0; j < N; j++) {
			printf("A[%d][%d]: ", i, j);
			scanf("%d", &A[i][j]);
		}
		putchar('\n');
	}
	
	int noft;
	
	printf("Insert number of threads: ");
	scanf("%d", &noft);
	if (noft <= 0) {
		printf("Number of threads must be greater or equal to 1.\n");
		exit(1);		
	}
	omp_set_num_threads(noft);
	
	/* (a): Check if matrix is strictly diagonally dominant.
	shared variable flag initialized in 1. If any of the lines has diagonial element less than the sum of all others (sdd criterion), 
	flag will change to zero. Only one thread can access flag each time Each thread has its own (private) line sum. */
	
	int flag = 1, tid, lineSum = 0, chunk = CHUNKSIZE;
	double startTime, endTime;
	
	startTime = omp_get_wtime();
	#pragma omp parallel for schedule(static, chunk) shared(A, flag) private(i, j, tid, lineSum)
	for (i = 0; i < N; i++)
	{
		lineSum = 0;
		for (j = 0; j < N; j++) {
			if (j != i) lineSum += abs(A[i][j]);
		}
		if (abs(A[i][i]) <= lineSum) {
			#pragma omp atomic write
				flag = 0;
		}
    }
	endTime = omp_get_wtime();
	printf("\n*** Time for checking SDD criterion: %f ***\n", endTime - startTime);
	
	if (flag == 0) {
		printf("\nMatrix is not strictly diagonally dominant.\n");
		exit(1);
	}
	
	/* (b): Supposed that the max diagonal element is [0][0]. One for loop cause we need only one index (diagonal elements: i=j) */
	
	
	int diag_max = A[0][0];
	
	startTime = omp_get_wtime();
	#pragma omp parallel for schedule(static, chunk) private(i) reduction(max:diag_max)
	for (i = 0; i < N; i++) {
		if (abs(A[i][i]) > diag_max)
			diag_max = abs(A[i][i]);
	}
	endTime = omp_get_wtime();
	printf("\n*** Time for finding diag_max: %f ***\n", endTime-startTime);
	printf("\n----> Maximum element by absolute value in the diagonal is: %d\n", diag_max);
	
	/* (c): Collapse(2): thread computes each element of matrix B separately, parallelism is not by lines as q.(a) */
	
	int **B;
	B = (int**)malloc(N*sizeof(int*));
	if (B == NULL) {
		printf("No available memory.\n");
		exit(1);
	}
	
	for (i = 0; i < N; i++) {
		B[i] = (int*)malloc(N*sizeof(int));
		if (B[i] == NULL) {
			printf("No available memory.\n");
			exit(1);
		}
	}

	startTime = omp_get_wtime();
	#pragma omp parallel for schedule (static, chunk) collapse(2) shared(A) private(i, j, tid)
	for(i = 0; i < N; i++)
		for(j = 0; j < N; j++) {
			//tid = omp_get_thread_num();
			if (i == j)
				B[i][j] = diag_max;
			else
				B[i][j] = diag_max - abs(A[i][j]);
			//printf("Thread %d creates B[%d][%d] = %d\n", tid, i, j, B[i][j]);   
			
		}
	endTime = omp_get_wtime();
	printf("\n*** Time for fill matrix B: %f ***\n", endTime-startTime);
	
	printf("\n******************** Matrix B ********************\n");
	for (i = 0; i < N; i++) {
		for (j = 0; j < N; j++) {
			printf("%d\t", B[i][j]);
		}
		putchar('\n');
	}
	printf("\n**************************************************\n");
	
	/* (d) Minimum finding element by element with reduction clause */
	
	int Bmin = B[0][0]; //B[0][0] = diag_max so it cannot be the minimum element of B
	
	startTime = omp_get_wtime();
	#pragma omp parallel for schedule (static, chunk) collapse(2) shared(A) private(i, j, tid) reduction(min:Bmin)
	for(i = 0; i < N; i++)
		for(j = 0; j < N; j++) {
			if (B[i][j] < Bmin)
				Bmin = B[i][j];
			//tid = omp_get_thread_num();
			//printf("Thread %d checks B[%d][%d] = %d\n", tid, i, j, B[i][j]);   
			
		}
	endTime = omp_get_wtime();
	printf("\n*** Time for find min with reduction clause: %f ***\n", endTime-startTime);	
	printf("\n----> (Found with reduction) Minimum element of matrix B is %d\n", Bmin);
	
	// (d2.1) Same loop as (d), checking and possible change of Bmin is in critical clause.
	
	Bmin = B[0][0];
	
	startTime = omp_get_wtime();
	#pragma omp parallel for schedule (static, chunk) collapse(2) shared(A) private(i, j) 
	for(i = 0; i < N; i++)
		for(j = 0; j < N; j++) {
			#pragma omp critical (min_B)
			{
				if (B[i][j] < Bmin) Bmin = B[i][j];
			}			
		}
	endTime = omp_get_wtime();
	printf("\n*** Time for find min with critical clause: %f ***\n", endTime-startTime);
	
	printf("\n----> (Found with critical clause) Minimum element of matrix B is %d\n", Bmin);
	
	printf("\n********************************************************************\n");
	
	for(i = 0; i < N; i++) {
		free(A[i]);
		free(B[i]);
	}
	
	free(A);
	free(B);
	
	return 0;
}