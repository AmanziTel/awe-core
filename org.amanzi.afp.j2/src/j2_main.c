#include <stdio.h>
#include <stdlib.h>

#define MAXNAMELENGTH 256

int japa_awe(char * pctrfile);

int main(int argc, char *argv[])

{
	char TrialName[MAXNAMELENGTH];

	if (argc < 2) {
		printf("Syntax Error :: %s <input control file name>\n", argv[0]);
		return -1;
	}

	strncpy(TrialName, argv[1], MAXNAMELENGTH - 1);
	TrialName[MAXNAMELENGTH - 1] =0;

	awe_afp(TrialName);

	printf("\n\nj2 for %s finished\n\n", TrialName);

	fflush(stdin);
	fflush(stdout);

	return EXIT_SUCCESS;
}
