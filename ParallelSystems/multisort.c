#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int compare (const void * a, const void * b) {
	return ( *(int*)a - *(int*)b );
}

void merge (int *a, int *a_lim, int *b, int *b_lim, int *res) {
	int i = 0;
	
	while (a <= a_lim && b <= b_lim) {
		if ((*a) < (*b)) {
			res[i++] = *a;
			a++;
		} else {
			res[i++] = *b;
			b++;
		}
	}
	
	while (a <= a_lim) {
		res[i++] = *a;
		a++;
	}
	
	while (b <= b_lim) {
		res[i++] = *b;
		b++;
	}	
}

void multisort(int *A, int *result, int N) {

	if (N < 4) {
		qsort(A, N, sizeof(int), compare);
		return;
	}
	
	int quarter = N/4;
	int *startA = A;
	int *spaceA = result;
	int *startB = startA + quarter;
	int *spaceB = spaceA + quarter;
	int *startC = startB + quarter;
	int *spaceC = spaceB + quarter;
	int *startD = startC + quarter;
	int *spaceD = spaceC + quarter;
	
	#pragma omp task
		multisort(startA, spaceA, quarter);
	#pragma omp task
		multisort(startB, spaceB, quarter);
	#pragma omp task
		multisort(startC, spaceC, quarter);
	#pragma omp task
		multisort(startD, spaceD, N-3*quarter);
		
	#pragma omp taskwait 
	
	#pragma omp task
		merge(startA, startA + quarter - 1, startB, startB + quarter - 1, spaceA);
	#pragma omp task
		merge(startC, startC + quarter - 1, startD, A + N - 1, spaceC);
	
	#pragma omp taskwait
	
	merge(spaceA, spaceC - 1, spaceC, spaceA + N - 1, startA);
	
}

int main(int argc, char *argv[])
{
	//vector dimension is given as argument
	if (argc !=2 || atoi(argv[1]) <= 1) {
		printf("N must be greater than 1.\nExecute program with N as argument.\n");
		exit(1);
	}
		
	int N = atoi(argv[1]);
	int i, *A, *result;
	double startTime, endTime;
	
	A = (int *)malloc(N*sizeof(int));
	result = (int *)malloc(N*sizeof(int));
	
	if (A == NULL || result == NULL) {
		printf("No available memory.\n");
		exit(1);
	}

	for (i = 0; i < N; i++) {
		printf("A[%d]: ", i);
		scanf("%d", &A[i]);
	}

	for (i = 0; i < N; i++)
		printf("%d  ", A[i]);
	putchar('\n');
		
	int noft;
	printf("Insert number of threads: ");
	scanf("%d", &noft);
	if (noft <= 0) {
		printf("Number of threads must be greater or equal to 1.\n");
		exit(1);		
	}
	omp_set_num_threads(noft);
	
	startTime = omp_get_wtime();

	#pragma omp parallel shared(A)
	{
		#pragma omp single
			multisort(A, result, N);	
	}
	endTime = omp_get_wtime();
	
	printf("\nSorted list:\n");
	for (i = 0; i < N; i++)
		printf("%d  ", A[i]);
	
	printf("\n\n*** Execution time: %f seconds ***\n", endTime-startTime);
	free(A);
	free(result);

	return 0;
}