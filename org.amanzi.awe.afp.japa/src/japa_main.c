/*
 ============================================================================
 Name        : HelloWorld.c
 Author      : 
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */
/*
#include <stdio.h>
#include <stdlib.h>

int main(void) {
	puts("!!!Hello World!!!"); // prints !!!Hello World!!! 
	return EXIT_SUCCESS;
}
*/
#include <stdio.h>
#include <stdlib.h>

#define MaxNameL 80;

int  japa_awe(char * pctrfile);

int main(void)

{

char TrialName[80];



printf("\n\nFullPath-Name of the Trial Control File:\n\n");



     scanf("%s",TrialName);



	 

	japa_awe(TrialName);





printf("\n\njapa_awe for %s finished\n\n", TrialName);



//    getch();
    return EXIT_SUCCESS;



}