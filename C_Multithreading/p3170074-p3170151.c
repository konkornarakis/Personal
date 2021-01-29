#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "p3170074-p3170151.h"
#include <time.h>

int profit = 0;
int countTel = tel;
int plan[zoneA + zoneB + zoneC][seat];
int countCash = cash;

unsigned int  seed;

pthread_mutex_t lock;
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

struct timespec requestStart, requestEnd;
long elapsed_standBy = 0;
long elapsed_serving = 0;

void *customer(void *x) {
	int * tid = (int *)x;
	int rc;
	int error = -1;
    
	rc = pthread_mutex_lock(&lock);
	clock_gettime(CLOCK_REALTIME, &requestStart);
	while (countTel == 0) {
		rc = pthread_cond_wait(&cond, &lock);
	}
	clock_gettime(CLOCK_REALTIME, &requestEnd);
	elapsed_standBy += requestEnd.tv_sec - requestStart.tv_sec;
	countTel--;
	
	rc = pthread_mutex_unlock(&lock);

	int zone = rand_r(&seed) % (100 + 1 - 0 ) + 0;

	if (zone < 100 * pZoneA) {
		zone = 1;
	} else if (zone < 100 * pZoneB + 20) {
		zone = 2;
	} else {
		zone = 3;
	}
	
	int tickets = rand_r(&seed) % (nSeatHigh + 1 - nSeatLow) + nSeatLow;
	
	int wait = rand_r(&seed) % (tSeatHigh + 1 - tSeatLow) + tSeatLow;
    
	sleep(wait);
	
	int i; 	//counter
	int j;	//counter
	int available = 0;
	
	int seatPosRow;
	int seatPosCol;

	//seatPos = malloc(tickets * sizeof(int));
	//int cSeatPos = 0;

	rc = pthread_mutex_lock(&lock);
	
	if (zone == 1) {
		for (i = 0; i < zoneA; i++) {
			for (j = 0; j < seat; j++) {
				if ( available == tickets) {
					seatPosRow = i;
					seatPosCol = j;					
					break;
				}

				if (plan[i][j] == 0) {
					available++;
				} else {
					available = 0;
				}	
			}
		}
	} else if (zone == 2) {
		for (i = 0; i < zoneB; i++) {
			for (j = 0; j < seat; j++) {
				if ( available == tickets) {
					seatPosRow = i;
					seatPosCol = j;					
					break;
				}

				if (plan[i][j] == 0) {
					available++;
				} else {
					available = 0;
				}	
			}
		}
	} else {
		for (i = 0; i < zoneC; i++) {
			for (j = 0; j < seat; j++) {
				if ( available == tickets) {
					seatPosRow = i;
					seatPosCol = j;					
					break;
				}

				if (plan[i][j] == 0) {
					available++;
				} else {
					available = 0;
				}	
			}
		}
	}
	
	/*
	
	for (i = 0; i < seat; i++) {
		if (plan[i] == 0) {
			available++;
			seatPos[cSeatPos] = i;
			cSeatPos++;
		}
		if (available == tickets)
			break;
	}
	*/

	if (available == 0)
		    error = 2;

	if (available == tickets) {
		for (i = seatPosCol; i > seatPosCol - available ; i--) {
			plan[seatPosRow][i] = tid;
		}
	}
	else {	//Not enough available tickets
		error = 1;
	}
   
	rc = pthread_mutex_unlock(&lock);
	
	/* ****************** */
    	rc = pthread_mutex_lock(&lock);

    	countTel++;	
    	rc = pthread_cond_signal(&cond);
  	
	rc = pthread_mutex_unlock(&lock);
 	/* ***************** */

	rc = pthread_mutex_lock(&lock);

	while (countCash == 0) {
		rc = pthread_cond_wait(&cond, &lock);
	}
	countCash--;
	
	rc = pthread_mutex_unlock(&lock);

	int wait2 = rand_r(&seed) % (tCashHigh + 1 - tCashLow) + tCashLow;
	sleep(wait2);

	int probability = rand_r(&seed)%(100 + 1 - 0 ) + 0 ;

	rc = pthread_mutex_lock(&lock);
	int cost;
	char* zoneName;
	if (probability < 100 * pCardSucces) {
		if (zone == 1) {
			zoneName="A";
			cost = tickets * cZoneA;
		} else if (zone == 2) {
			zoneName="B";
			cost = tickets * cZoneB;
		} else {
			zoneName="C";
			cost = tickets * cZoneC;
		}

		profit += cost;

	} else {
		for (i = seatPosCol; i > seatPosCol - available ; i--) {
			plan[seatPosRow][i] = 0;
		}
		error = 0;
	}
    
	rc = pthread_mutex_unlock(&lock);
    /* ****************** */
    

    /* ****************** */
    rc = pthread_mutex_lock(&lock);
    countCash++;	
    rc = pthread_cond_signal(&cond);
    rc = pthread_mutex_unlock(&lock);
    /* ****************** */
    

    /* ****************** */
	rc = pthread_mutex_lock(&lock);
	printf("Customer's TID: %d.\n", tid);
	if (error != -1) {
	    	if (error == 0) {
	        	printf("Card payment was denied.\n\n");
        	} else if (error == 1) {
            	printf("This number of tickets is not available.\n\n");
        	
		} else {
            		printf("Theater is full, no tickets available.\n\n");
        	}
	} else {
		printf("Booking completed successfully. TID is:  %d, zone is: %s, seats are: ", tid,zoneName);
	    	for (i = seatPosCol; i > seatPosCol - tickets ; i--) {
			printf("%d, ", i + 1);
		}
	    	printf("and the cost of the transaction is: %d.\n\n", cost);
	}

	clock_gettime(CLOCK_REALTIME, &requestEnd);		
	elapsed_serving += requestEnd.tv_sec - requestStart.tv_sec;	

	rc = pthread_mutex_unlock(&lock);
    /* ****************** */
	pthread_exit(&rc);
}

int main(int argc, char *argv[]) {

	if (argc != 3) {
		printf("ERROR: the program should take two arguments\n");
		exit(-1);
	}
	
    char *arg1 = argv[1];
	int nCust = atoi(arg1);
	char *arg2 = argv[2];
	seed = atoi(arg2);
    
    //printf("Number of customers: %d\n", nCust);
    //printf("Seed: %d\n", seed);
	
	printf("Program started...\n\n");
    
	int i;
	int j;
	for (i = 0; i < zoneA + zoneB + zoneC;i++)
	for (j = 0; j < seat; j++) {
		plan[i][j] = 0;
	}

	int *id;
	id = malloc(nCust * sizeof(int));

	int threadCount;

	pthread_t * threads;
	threads = malloc(nCust * sizeof(pthread_t));

	int rc;

	rc = pthread_mutex_init(&lock, NULL);
	if (rc != 0) {
		printf("ERROR: return code from pthread_mutex_init() is %d\n", rc);
		exit(-1);
	}

	for (threadCount = 0; threadCount < nCust; threadCount++) {
		id[threadCount] = threadCount + 1;
		rc = pthread_create(&threads[threadCount], NULL, customer, id[threadCount]);

		if (rc != 0) {
			printf("ERROR: return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
	}

	void *status;

	for (threadCount = 0; threadCount < nCust; threadCount++) {
		rc = pthread_join(threads[threadCount], &status);

		if (rc != 0) {
			printf("ERROR: return code from pthread_join() is %d\n", rc);
			exit(-1);
		}

		//printf("Main: Thread %d returned %d as status code.\n", id[threadCount], (*(int *)status));
	}
	
	rc = pthread_mutex_destroy(&lock);
	if (rc != 0) {
		printf("ERROR: return code from pthread_mutex_destroy() is %d\n", rc);
		exit(-1);
	}
	
	printf("Theater's seat plan:\n");
	
	for (i = 0; i < zoneA; i++) {
		for(j = 0; j < seat; j++){

			if (plan[i][j] == 0) continue;
			printf("Seat %d/ Customer %d", i + 1, plan[i][j]);
			if (i == zoneA - 1) break;
			printf(", ");
		}
	}

	for (i = zoneA; i < zoneA + zoneB; i++) {
		for(j = 0; j < seat; j++){

			if (plan[i][j] == 0) continue;
			printf("Seat %d/ Customer %d", i + 1, plan[i][j]);
			if (i == zoneB - 1) break;
			printf(", ");
		}
	}
		
	for (i = zoneB; i < zoneC; i++) {
		for(j = 0; j < seat; j++){

			if (plan[i][j] == 0) continue;
			printf("Seat %d/ Customer %d", i + 1, plan[i][j]);
			if (i == zoneC - 1) break;
			printf(", ");
		}
	}

	printf("\n\n");
	printf("Total profit: %d\n",profit);

	printf("Average stand-by time: %ld: \n", elapsed_standBy/nCust);
	printf("Average customer serving time: %ld: \n", elapsed_serving/nCust);

	pthread_cond_destroy(&cond);

	free(id);

	free(threads);

	return 0;
}
