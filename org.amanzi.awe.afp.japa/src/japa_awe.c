/* ======================================
 FILE:   japa_awe.c 
 AUTHOR: JPB
 DATE:�  2010-07-25
 ========================================= */

#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
//#include <conio.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <stdarg.h>
///?UNIX #include <syscalls.h>

#ifdef POSIX
#include <unistd.h>
#include <execinfo.h>
void* callstack[10];
void print_call_stack(int frames) {
	printf("Callstack of %d frames:\n",frames);
	char ** strs = backtrace_symbols(callstack, frames);
	int i;
	for (i=0; i<frames; ++i) {
		printf("\t%s\n", strs[i]);
	}
	free(strs);
}
#define call_dump()  int frames=backtrace(callstack,sizeof(callstack));print_call_stack(frames);
#else
#define call_dump()
#endif

#define PTEST      if(ptest){
#define ETEST      }
#define PTR(t)    t*
#define BYTE      unsigned char
#define BOOL      short
#define False       0
#define True        1
#define MaxNameL    80
#define MaxLineL    1000     
#define EOChain     13
#define MaxFileName 256
#define jpbPI       3.1415926
#define sin_B(a)      (((a)!=0) ? True : False)
#define cos_B(a)      (((a)!=0) ? False : True)
#define if_else_B(x,A,B)  (sin_B(x) ? A : B);
#define AllocError(X,s,msg,act) if(X==NULL){printf("Failed to allocate memory for %s size %d: %s\n",msg,s,strerror(errno));call_dump();act;}
#define MAX_(a,b)     (((a)<(b)) ? (b) : (a))
#define MIN_(a,b)     (((a)<(b)) ? (a) : (b))
#define sum_B(x,a,b)  (sin_B(x)*(a) + cos_B(x)*(b))
#define PP(x)         (x++)
#define MM(x)         (x--)
#define NEXT_B(x,a)   if_else_B(x,((a) += 1),((a) -= 1))
#define SIGN_(a)      ((0 < (a)) ? 1 : -1)
#define NNSIGN_(a)    ((0 < (a)) ? 1 : 0)
#define Free_SET(M)     if(M!=NULL){free(M->Chain);free(M);M=NULL;}
#define NetworkSet         networkSet    
#define Network        currentNetwork          
/*///?#define NetworkSet         networkSet      
 #define NETCARD        Network->Card
 #define CARD_(Q)       Q->Card
 #define E_(i,Q)        Q->Chain[i]
 ///?#define CARD_(Q)       Q->Card
 #define _S_____(i)      E_(i,NetworkSet)     
 #define _N___IX_(i)     E_(i,NetworkSet)
 */
#define _N__E(i)       Network->_N___[i]
#define ID(i)         Network->_N___[i]->Id
#define NC(i)         Network->_N___[i]->NC
#define CNC(i)        Network->_N___[i]->CNC
#define COI(i)        Network->_N___[i]->COI
#define ADI(i)        Network->_N___[i]->ADI
#define XC(i)         Network->_N___[i]->XC
#define LC(i)         Network->_N___[i]->LC
#define _ED__(i,j)     Network->_E___[i][j]
#define PERM(i)       Network->Perm[i]
///?#define NetworkSet         networkSet      
#define NETCARD        Network->Card
#define CARD_(Q)       Q->Card
#define E_(i,Q)        Q->Chain[i]
///?#define CARD_(Q)       Q->Card
#define _S_____(i)      E_(i,NetworkSet)     
#define _N___IX_(i)     E_(i,NetworkSet)
///?#define _N__E_(i)      _N__E(_S_____(i))
///?#define ID_(i)        ID(_S_____(i))
///?#define NC_(i)        NC(_S_____(i))
#define RC_(i)        RC(_S_____(i))
#define LC_(i)        LC(_S_____(i))
#define _N__E_(i)      _N__E(_S_____(i))
#define _ED___(i,j)    _ED__(_S_____(i),_S_____(j))
#define _N___IX__(i,Q)   _N___IX_(E_(i,Q))
#define _S______(i,Q)    _S_____(E_(i,Q))
#define _N__E__(i,Q)     _N__E_(E_(i,Q))
#define ID_(i)        ID(_S_____(i))
#define NC_(i)        NC(_S_____(i))
#define ID__(i,Q)       ID_(E_(i,Q))
#define NC__(i,Q)       NC_(E_(i,Q))
#define RC__(i,Q)       RC_(E_(i,Q))
#define LC__(i,Q)       LC_(E_(i,Q))
#define _ED____(i,j,Q)   _ED___(E_(i,Q),E_(j,Q))
#define PERM__(i,Q)     PERM(E_(i,Q))
#define REL_(R,i,j)     R[ID_(i)][ID_(j)]
#define NIX(i)     cet[i].Nix
#define NID(i)     ID(NIX(i))
#define CID(i)     cet[i].Cid
#define FREQ(i)    cet[i].Fn
#define ParCARD     10
#define SONL      1
#define ADJL      2
#define COL       3
#define DISL      1  
#define PRIL      1  
#define xPropL     0            
#define yPropL     0
#define zPropL     0
#define CRINITVAL  "10        "
/*#define L__P____      (PRIL+DISL+COL+ADJL+xPropL+yPropL+zPropL+2)
 #define ____X      9                              
 #define ____T      8 
 #define ____C      7   
 #define ____S      6*/
#define ____2N     4
#define ____NS     3
#define ____CI     2
#define ____AI     1
#define NOLOCL     0
#define L__P____      (PRIL+DISL+COL+ADJL+xPropL+yPropL+zPropL+2)
#define ____X      9                              
#define ____T      8 
#define ____C      7   
#define ____S      6
#define ___C__     999
#define ___A__     99
#define NO_ERROR   0
#define FILE_ERROR 1
#define SCC_ERROR      3
#define NR_ERROR       4

#include <ctype.h>

__inline void _my_sscanf1(char* p, int* r) {
	*r = *p -'0';
}

/*
 #define _P__(R,i,j,r)      r=0;_my_sscanf1(R[i][j],&r)
 #define _D__(R,i,j,r)      r=0;_my_sscanf1(R[i][j]+PRIL,&r)
 #define _C_(R,i,j,r)       r=0;sscanf(R[i][j]+(PRIL+DISL),"%3d",&r)
 #define _A__(R,i,j,r)      r=0;sscanf(R[i][j]+(PRIL+DISL+COL+1),"%2d",&r)
 */
#define P__(R,i,j,w)      jprtl(R[i][j],w,PRIL) 
#define D__(R,i,j,w)      jprtl(R[i][j]+PRIL,w,DISL)
#define C_(R,i,j,w)       jprtl(R[i][j]+(PRIL+DISL),w,COL)
#define A__(R,i,j,w)      jprtl(R[i][j]+(PRIL+DISL+COL+1),w,ADJL)
#define _P__(R,i,j,r)      r=0;_my_sscanf1(R[i][j],&r)
#define _D__(R,i,j,r)      r=0;_my_sscanf1(R[i][j]+PRIL,&r)
#define _C_(R,i,j,r)       r=0;sscanf(R[i][j]+(PRIL+DISL),"%3d",&r)
#define _A__(R,i,j,r)      r=0;sscanf(R[i][j]+(PRIL+DISL+COL+1),"%2d",&r)
#define _P___(R,i,j,r)  _my_sscanf1(REL_(R,i,j),&r)
#define _D___(R,i,j,r)  _my_sscanf1(REL_(R,i,j)+PRIL,&r)

FILE *fp_log; //,*fp;
typedef char ___P____[L__P____];

typedef struct cell {
	int Nix, Cid, Fn;
	short fxd;
} T_C___;

typedef struct freqob {
	int frno, frdis;
} FREQOB_T;

typedef struct set {
	int * Chain;
	int Card;
} SetStruct;

typedef struct node {
	long COI, ADI, Clno;
	char S[MaxNameL], SCL;
	int TSS, SS, Id;
	int *ff, NC, LC, XC, AxF, FxF;
} _N__E_T;

typedef struct netstruct {
	int B, lb, ub, FXF, Card, LB0, LB[3];
	_N__E_T ** _N___;
	___P____ ** _E___;
	short ** ENAB;
	BYTE * Perm;
} NetworkStructure;

typedef struct priopar {
	int pdisr, pdism, pcmax, pclb, pamax, palb, pcoit1;
} PPAR_T;
PPAR_T PPar7[ParCARD];

/*typedef struct pair  {int   Left, Right;  } PAIR_T;
 typedef struct intvset  { int Card;   PAIR_T  * IChain;} INVT_S__;*/

FREQOB_T FROL, FROR;
NetworkStructure *currentNetwork;
T_C___ * cet, * XCET;
SetStruct *networkSet, *Qx, *coX, *Q0, *MaxNC;
long SoNCC=0, SONCC=0, CoNCC=0, AdNCC=0, CoIT0=0, CoIT1=0, CoIT1Done=0, ClNo=0;
int /*UBreak=0,*/AT=0, PlExist=0, Lay = 0, FDemand=0, NOFC=0, MaxRTperCell=0, FrequencyBandRange=0,
		MAXDIS = 0, COCard=0, UMTS = 0, PLOW=0, PROGRESS=0, WPL=0, GROUPING=0,
		ALLN=0, ELEN=0, FIX=0, EXCEPF=0, FORBF=0, COFSET=333, IMAX=331, SONMIN=
				333, NMIN=666, NS_N0=0, NS_N1=0, NS_SON0=0, NS_ICO=0, N2_L=0,
		SET_0_1=0, N2LLIM=0, FLAST = -1, FFLX = -1, MIN_SET = 20,
		MAX_SET = 200, LASTQC=0, FACC1=0, FACC2=0, FB_LB, FB_RB, AdIT1=0,
		AdIT2=0, FACC=0;
int DD=1, DDo=1, NotEmpty=0, NOFFMIN=0, FirstChannel=0;
BOOL FBAND=True;
double TvA_S0 = 1.0, TvA_S1=1.0, TvAI0=10e10, TvAI1=0.0, T2Ai = 10e10,
		TAx =0.0, TAi=10e10;
char * XSITE[MaxNameL+1];
int CellSpacing =3, SiteSpacing =2, MinNeighbourSpacing=1, NeighbourSpacing=2, SecondNbrSpacing=1, ReCalAll=0, UseTraffic=0,
		USON=0, QUALITY=50, DECOMP=1, ExCliq=0, HoppingType, UseGrouping, NumberOfGroups, CellCardinality;
int ChannelRange, NumberOfChannels, CMaxRt, * FreqChannels;
char CellFile[MaxFileName+1], NeighboursFile[MaxFileName+1], InterferenceFile[MaxFileName+1],
		PlanFile[MaxFileName+1], ///?planfile
		CliquesFile[MaxFileName+1], LogFile[MaxFileName+1], ///?protfile
		ForbiddenFile[MaxFileName+1], ExceptionsFile[MaxFileName+1];
#define Q_(a)                    pow(a,2)
#define TOL_                     0.000001
#define SPA_(ux,uy,vx,vy,a)      (V_l(ux,uy)*V_l(vx,vy)*cos(a))
#define T_60_l_x(px,py,x,y)      (R_60_l_x((x-px),(y-py))+px) 
#define T_60_l_y(px,py,x,y)      (R_60_l_y((x-px),(y-py))+py) 
#define T_60_r_x(px,py,x,y)      (R_60_r_x((x-px),(y-py))+px) 
#define T_60_r_y(px,py,x,y)      (R_60_r_y((x-px),(y-py))+py) 
#define ISCUT(a,b,c,d)           (DET(a,b,c,d) != 0.0)
#define QEQ(a,b)                 (fabs(a-b) < TOL_)
#define QSUM(a,b,c)              QEQ((a+b),c)
#define DIS_(p1x,p1y,p2x,p2y)    V_l((p2x-p1x),(p2y-p1y))
#define BETV_(ax,ay,ux,uy,vx,vy) (QSUM(AV_(ux,uy,ax,ay),AV_(ax,ay,vx,vy),AV_(ux,uy,vx,vy)))
#define BETP_(x,y,px,py,p1x,p1y,p2x,p2y) (BETV_((x-px),(y-py),(p1x-px),(p1y-py),(p2x-px),(p2y-py)))
#define IsCut_L(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y)  ISCUT(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y), A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y)) 
#define Cut_x(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y)  LCut_x(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),C_L(p1x,p1y,q1x,q1y), A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y),C_L(p2x,p2y,q2x,q2y))  
#define Cut_y(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y)   LCut_y(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),C_L(p1x,p1y,q1x,q1y), A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y),C_L(p2x,p2y,q2x,q2y))  

void flushLog() {
	fflush(stdout);
	if (fp_log != 0 && fp_log != stdout) {
		fflush(fp_log);
	}
}
void closeLog() {
	fflush(stdout);
	if (fp_log != 0 && fp_log != stdout) {
		fclose(fp_log);
		fp_log = 0;
	}
}
char logLine[1024];
void logInfo(char * format, ...) {
	va_list args;
	va_start (args, format);
	vsprintf(logLine, format, args);
	va_end (args);
	fprintf(stdout, logLine);
	flushLog();
}

void jpb_strAsubst(char * here, char * old, char * neu)
{
	char *fo, *aux;
	long ol=0, nl = 2*(strlen(here)+strlen(neu));
	aux = (char *) calloc(nl, sizeof(char));
	ol = strlen(old);
	nl = strlen(neu);
	strcpy(aux,neu);
	fo = here;
	while ((fo = strstr(here, old)) != NULL) {
		strcpy(aux+nl,fo+ol);
		strcpy(fo,aux);
	}
	free(aux);
}

void LogProgress(long lCoIT1Done, long lCoIT1) {
	time_t t;
	long tt;
	time(&t);
	tt = (long) t;
	if (lCoIT1Done <0)
		lCoIT1Done =0;
	logInfo("PROGRESS CoIT1Done/CoIT1,%ld,%ld,%ld,\n", tt, lCoIT1Done, lCoIT1);
}

void jcpyl(char * into, char * str) {
	char * aux=(char*)calloc(strlen(into)+1, sizeof(char));
	strcpy(aux,into+strlen(str));
	strcpy(into,str);
	strcat(into,aux);
	free(aux);
}

void jprtl(char * into, int value, int len) {
	char * aux=(char*)calloc(len+1, sizeof(char));
	switch (len) {
	case 1:
		sprintf(aux,"%1d",value);
		break;
	case 2:
		sprintf(aux,"%2d",value);
		break;
	case 3:
		sprintf(aux,"%3d",value);
		break;
	default:
		break;
	}
	jcpyl(into, aux);
	free(aux);
}

/*
 * Allocate memory for ?
 * (This looks like the main allocation routing because it started with allocating to the main object
 * _N_____
 */
NetworkStructure * AllocateNetworkMemory(int dim, int bim) {
	int i;
	NetworkStructure *Network;
	Network = (NetworkStructure *) calloc(1,sizeof(NetworkStructure));
	AllocError(Network,sizeof(NetworkStructure),"Network",exit(0);)
	Network->_N___ = ( PTR(_N__E_T) *) calloc(dim,sizeof(PTR(_N__E_T)));
	AllocError(Network->_N___,dim*sizeof(PTR(_N__E_T)),"Network->_N___",exit(0);)
	for (i=0; i<dim; i++) {
		Network->_N___[i] = (_N__E_T *) calloc(1,sizeof(_N__E_T));
		AllocError(Network->_N___[i],sizeof(_N__E_T),"Network->_N___[i]",exit(0);)
		Network->_N___[i]->ff = (int*) calloc(bim,sizeof(int));
		AllocError(Network->_N___[i]->ff,bim*sizeof(int),"Network->_N___[i]->ff",exit(0);)
	}
	Network->_E___ = (PTR(___P____) *) calloc(dim,sizeof(PTR(char)));
	AllocError(Network->_E___,dim*sizeof(PTR(char)),"Network->_E___",exit(0);)
	for (i=0; i<dim; i++) {
		Network->_E___[i] = (___P____ *) calloc(dim,sizeof(___P____));
		AllocError(Network->_E___[i],dim*sizeof(___P____),"Network->_E___[i]",exit(0);)
	}
	Network->ENAB = NULL;
	Network->Perm = (PTR(BYTE)) calloc(dim,sizeof(BYTE));
	AllocError(Network->Perm,dim*sizeof(BYTE),"Network->Perm",exit(0);)
	return (Network);
}

/*
 * Free all allocated memory
 */
void free_memory(void) {
	int i;
	if (Network) {
		for (i=0; i<Network->Card; i++) {
			if (Network->_N___[i]) {
				free(Network->_N___[i]->ff);
				free(Network->_N___[i]);
			}
		}
		free(Network->_N___);
		for (i=0; i<Network->Card; i++)
			free(Network->_E___[i]);
		free(Network->_E___);
		free(Network->Perm);
		free(Network->ENAB);
		free(Network);
	}
	if (cet)
		free(cet);
	if (XCET)
		free(XCET);
	if (networkSet) {
		free(networkSet->Chain);
		free(networkSet);
	}
	if (Qx) {
		free(Qx->Chain);
		free(Qx);
	}
	if (coX) {
		free(coX->Chain);
		free(coX);
	}
	if (Q0) {
		free(Q0->Chain);
		free(Q0);
	}
	closeLog();
	//TODO: I changed this from the CellFile to the CliquesFile, but am still not sure why it should be unlinked
	unlink(CliquesFile);
}

/*
 * Allocate memory for ?
 */
T_C___ * A___C__(int dim) {
	int i;
	T_C___ *xet;
	xet = (T_C___ *) calloc(dim, sizeof(T_C___));
	AllocError(xet,dim*sizeof(T_C___),"xet",exit(0);)
	XCET = (T_C___ *) calloc(dim, sizeof(T_C___));
	AllocError(XCET,dim*sizeof(T_C___),"XCET",exit(0);)
	for (i=0; i<dim; i++) {
		xet[i].fxd = 0;
		XCET[i].fxd = 0;
	}
	return (xet);
}

/*
 * Allocate memory for ?
 */
SetStruct * AllocateSetStruct(int card) {
	SetStruct * M;
	M=(SetStruct *) calloc(1, sizeof(SetStruct));
	AllocError(M,sizeof(SetStruct),"AllocateSetStruct:M",return(NULL);)
	M->Chain = (int *)calloc(card, sizeof(int));
	AllocError(M->Chain,card*sizeof(int),"AllocateSetStruct:M->Chain",return(NULL);)
	M->Card = 0;
	return (M);
}

/*
 * Allocate memory for ?
 */
void a_____e___(int is_fix, NetworkStructure * xnet) {
	int i;
	printf("About to determine required size of ENAB\n");
	if (is_fix) {
		printf("Have is_fix: setting B to FrequencyBandRange=%d\n",FrequencyBandRange);
		xnet->B = FrequencyBandRange;
	}
	else{
		printf("Have no is_fix: setting B to 2*(FrequencyBandRange+Max(MAXDIS,SiteSpacing))+5 (FrequencyBandRange=%d,MAXDIS=%d,SiteSpacing=%d)\n",FrequencyBandRange,MAXDIS,SiteSpacing);
		xnet->B = 2*(FrequencyBandRange+MAX_(MAXDIS,SiteSpacing)) + 5;
	}
	xnet->ENAB = (PTR(short) *) calloc(xnet->Card, sizeof(PTR(short)));
	AllocError(xnet->ENAB,xnet->Card*sizeof(PTR(short)),"Network->ENAB",exit(0);)
	for (i=0; i<xnet->Card; i++) {
		printf("About to allocate memory for xnet->ENAB[%d] as an array of %d shorts\n",i,xnet->B+1);
		xnet->ENAB[i] = (short *) calloc(xnet->B+1, sizeof(short));
		AllocError(xnet->ENAB[i],(xnet->B+1)*sizeof(short),"Network->ENAB[]",exit(0);)
	}
}

void LayerCard(int Cmax) {
	int i;
	for (i=0; i<NETCARD; i++)
		LC(i) = MIN_(Cmax,NC(i));
}

int In_SET(int e, SetStruct * S) {
	int i=0, a=0;
	while (!(a) && (i<S->Card)) {
		a = ((a||(S->Chain[i]==e)));
		i++;
	}
	return (i-sin_B(a));
}

void SkipEl(int ix, SetStruct * xX) {
	int i=0;
	if (ix == (xX->Card - 1)) {
		xX->Card--;
		return;
	}
	i=ix;
	while (i < xX->Card - 1) {
		xX->Chain[i]=xX->Chain[i+1];
		i++;
	}
	xX->Card--;
}

void Copy_SET(SetStruct * Ax, SetStruct * Bx) {
	Ax->Card=0;
	while (Ax->Card<Bx->Card) {
		Ax->Chain[Ax->Card] = Bx->Chain[Ax->Card];
		Ax->Card++;
	}
}

void Diff_SET(SetStruct * Ax, SetStruct * Bx) {
	int i, ix;
	for (i=0; i<Bx->Card; i++) {
		ix = In_SET(Bx->Chain[i], Ax);
		if (ix<Ax->Card)
			SkipEl(ix, Ax);
	}
}

void IDform_Set(int scard, SetStruct * Bx) {
	int i;
	for (i=0; i<scard; i++)
		Bx->Chain[i] = i;
	Bx->Card = scard;
}

int g___n___(char *snxx, int ssx) {
	int i;
	for (i=0; i<Network->Card; i++)
		if ( (Network->_N___[i]->TSS == ssx) && !strcmp(snxx, 
		Network->_N___[i]->S) )
			return (Network->_N___[i]->Id);
	return (-1);
}

void write_frequency_plan_to_file(char * fname, T_C___ * xN) {
	int x, i, j=0;
	FILE * cefp;
	if ((cefp =fopen(fname, "w")) == NULL) {
		logInfo("ERROR opening output file %s: %s\n", fname, strerror(errno));;
		return;
	}
	while (j<CARD_(Network)) {
		fprintf(cefp, "%s %d %d %d %d ", Network->_N___[j]->S, 
		Network->_N___[j]->TSS, Network->_N___[j]->SS, 
		Network->_N___[j]->NC, Network->_N___[j]->AxF);
		for (i=0; i<Network->_N___[j]->AxF; i++) {
			x = Network->_N___[j]->ff[i]+FirstChannel;
			fprintf(cefp, "%d ", x);
		}
		fprintf(cefp, "\n");
		j++;
	}
	fclose(cefp);
}

BOOL is_fixed
(
		int nix,
		int fix
)
{
	int l;
	if(!Network->_N___[nix]->FxF) return(False);
	for(l=0;l<Network->_N___[nix]->FxF;l++)
	if(Network->_N___[nix]->ff[l] == fix) return(True);
	return(False);
}
void print_out_plan_dump(T_C___ *xN) {
	int i, j, k;
	logInfo("\nJPB-FA======================================================\n");
	for (i=0; i<Network->Card; i++) {
		Network->_N___[i]->AxF = Network->_N___[i]->FxF;
	}
	logInfo("\n============== %d::\n", 1);
	for (i= -1; i<Network->ub+1; i++) {
		k=0;
		logInfo( "\nF:=%3d::> ", i);
		for (j=0; j<NOFC; j++) {
			if (i==xN[j].Fn) {
				logInfo( "%s.%d.%d| ", Network->_N___[xN[j].Nix]->S, 
				Network->_N___[xN[j].Nix]->TSS, xN[j].Cid);
				if (!is_fixed(xN[j].Nix, i)) {
					if (Network->_N___[xN[j].Nix]->AxF < Network->_N___[xN[j].Nix]->NC) {
						Network->_N___[xN[j].Nix]->ff[Network->_N___[xN[j].Nix]->AxF] = i;
						Network->_N___[xN[j].Nix]->AxF++;
					}
				}
				k++;
				if (!(k%10))
					logInfo( "\n          ");
			}
		}
	}
	logInfo("\n---------------------------------------------------------------\n");
	if (j<NOFC) {
		logInfo( "!%4d RTs out: ", coX->Card);
		for (j=0; j<coX->Card; j++) {
			logInfo( "%s.%d.%d| ", Network->_N___[NIX(coX->Chain[j])]->S, 
			Network->_N___[NIX(coX->Chain[j])]->SS, 
			CID(coX->Chain[j]));
			if (j && !(j%6))
				logInfo( "\n              ");
		}
	}
	logInfo( "\nCard=%d\n", NOFC);
	logInfo( "=========================================================================================\n");
}

int write_frequency_plan(int pmod) {
	if (!PlExist)
		return (0);
	print_out_plan_dump(XCET);
	if (pmod)
		write_frequency_plan_to_file(PlanFile, XCET);
	flushLog();
	return (1);
}

__inline Permis_net(int pos, int dx, ___P____ ** E) {
	int i, r=0;
	if (!(pos<0) && (PERM(pos) != 2))
		PERM(pos) = 1;
	for (i=pos+1; i<CARD_(Network); i++) {
		_D___(E,pos,i,r);
		if (PERM(i) != 2)
			PERM(i) = cos_B(r<dx) * PERM(i);
	}
}

__inline void UpdatePerm(int pos) {
	int i;
	if (!(pos<0))
		PERM(pos) = PERM(pos)*(PERM(pos) - 1);
	for (i=pos+1; i<CARD_(Network); i++)
		PERM(i) = MAX_(1,PERM(i));
}

void Invalid_set(SetStruct * Q0p) {
	int i;
	for (i=0; i<CARD_(Q0p); i++) {
		PERM__(i,Q0p) = 2;
		NotEmpty--;
	}
}

__inline int CompCurLB(int di, SetStruct * xS) {
	int i, C=0;
	for (i=0; i<CARD_(xS); i++)
		C += LC__(i,xS);
	C = 1 + (di*(C-1));
	return (C);
}

__inline int CompCurLBs(int di, int from, SetStruct * xS) {
	int i, lx=0, C=0;
	lx = CompCurLB(di, xS);
	lx = lx + di - 1;
	for (i=from; i<CARD_(NetworkSet); i++)
		C += LC_(i);
	C = 1 + (di*(C-1));
	C += lx;
	return (C);
}

__inline int WeightOfSET(SetStruct * xX) {
	return (CARD_(xX));
}

int LBfilter(int oi, int ni, int from, int * olb, SetStruct * oQ, SetStruct * nQ) {
	int nlb, xlb = *olb;
	nlb=CompCurLBs(ni, from, nQ);
	if (oi != ni)
		return (xlb < nlb );
	xlb=WeightOfSET(oQ);
	nlb=WeightOfSET(nQ) + (CARD_(NetworkSet) - from);
	return (xlb < nlb );
}

__inline int Admiss4Set(int prio, int ix, int dx, ___P____ ** E, SetStruct * Q) {
	int i=0, r=0;
	while (i < CARD_(Q)) {
		_P___(E,ix,E_(i,Q),r);
		if (r<prio)
			return (False);
		_D___(E,ix,E_(i,Q),r);
		if (r<dx)
			return (False);
		i++;
	}
	return (True);
}

void Extend_clqset(BOOL * foundp, int priop, int ubp, int * rankp, int * xlbp,
		int card_xQ, int disp, ___P____ ** Rel, SetStruct * xQ, SetStruct * Qx0) {
	int p, q=disp, nofmax=0, ylb = CompCurLB(disp, xQ);
	BYTE done=False, NoCh=False;
	p=card_xQ;
	while ((p < CARD_(NetworkSet) ) && (NoCh || (!*foundp && (ylb < (ubp+1)) && LBfilter(
			*rankp, disp, p, xlbp, Qx0, xQ)))) {
		if ( (PERM(p) != 1) || !(Admiss4Set(priop, p, q, Rel, xQ))) {
			p++;
			NoCh=True;
			continue;
		}
		E_(CARD_(xQ)++,xQ) = p;
		Permis_net(p, q, Rel);
		Extend_clqset(foundp, priop, ubp, rankp, xlbp, p+1, disp, Rel, xQ, Qx0);
		done=True;
		NoCh=False;
		p++;
	}
	if (*foundp)
		return;
	if (!done) {
		*xlbp = CompCurLB(disp, xQ);
		if (Network->LB0 < *xlbp) {
			Network->LB[disp-1] = *xlbp;
			Copy_SET(Qx0, xQ);
			*rankp = disp;
			Network->LB0 = *xlbp;
			if (ubp < *xlbp)
				*foundp = 1;
		}
	}
	if (CARD_(xQ)> 0) {
		UpdatePerm(xQ->Chain[xQ->Card-1]);
		CARD_(xQ)--;
	}
}

void get_Q(int prio, int ub_p, int * rank, int dis, ___P____ ** Rel,
		SetStruct * Qxp, SetStruct * Qx0) {
	BOOL *found= (BOOL*) calloc(1,sizeof(BOOL));
	int qold= *rank, *xlb;
	*found=0;
	xlb = (int*) calloc(1, sizeof(int));
	*xlb = 0;
	Extend_clqset(found, prio, ub_p, rank, xlb, CARD_(Qxp), dis, Rel, Qxp, Qx0);
	if (qold != *rank)
		Network->LB[dis-1] = *xlb;
	free(xlb);
	free(found);
}

int get_MLB(int Minprio, int Ncar, int Mindist, int Ldis, ___P____ ** rel,
		SetStruct * Qxp, SetStruct * Qx0) {
	int ra=1, i;
	CARD_(Qxp) = 0;
	CARD_(Qx0) = 0;
	Network->LB0 = 0;
	if ((Mindist<2)&&(0<Ldis)) {
		Network->LB[0]=0;
		UpdatePerm(Qxp->Card-1);
		get_Q(Minprio, Ncar, &ra, 1, rel, Qxp, Qx0);
	}
	if ((Mindist<3)&&(1<Ldis)) {
		CARD_(Qx) = 0;
		Network->LB[1]=0;
		UpdatePerm(Qxp->Card-1);
		get_Q(Minprio, Ncar, &ra, 2, rel, Qxp, Qx0);
	}
	if ((Mindist<4)&&(2<Ldis)) {
		CARD_(Qxp) = 0;
		Network->LB[2]=0;
		UpdatePerm(Qxp->Card-1);
		get_Q(Minprio, Ncar, &ra, 3, rel, Qxp, Qx0);
	}
	ClNo++;
	logInfo("The %d maximal clique has Lower Bound = %2d and %d following sectors:\n{",
			ClNo, Network->LB0, CARD_(Qx0));
	for (i=0; i<CARD_(Qx0); i++) {
		_N__E__(i,Qx0)->Clno = ClNo;
		logInfo( "%d:%s.%dx%d;", 
		_N__E__(i,Qx0)->Clno, _N__E__(i,Qx0)->S, _N__E__(i,Qx0)->TSS, LC__(i,Qx0));
	}
	logInfo( "}\n");
	return (Network->LB0);
}

void perm_net(void) {
	int i;
	for (i=0; i<NETCARD; i++)
		PERM(i) = 1;
}

void ShiftSpaceLeft_LC(void) {
	int i=0, j;
	for (j=0; j<NETCARD; j++)
		if (LC(j)) {
			_S_____(i) = j;
			i++;
		}
	CARD_(NetworkSet) = i;
}

void write_cet(char * bfname) {
	int nofc=NOFC;
	FILE * bfp;
	if ((bfp = fopen(bfname, "wb")) == NULL) {
		printf("ERROR opening binary file %s for writing: %s", bfname,
				strerror(errno));;
		return;
	}
	fwrite(&nofc, sizeof(int), 1, bfp);
	fwrite(cet, sizeof(T_C___), NOFC, bfp);
	fclose(bfp);
}

void Sect_cet(SetStruct *xQ0) {
	int i=0, j=0, k=0, l=0;
	SetStruct * aux;
	aux = AllocateSetStruct(CARD_(xQ0));
	CARD_(aux) = CARD_(xQ0);
	for (i=0; i<CARD_(aux); i++) {
		E_(i,aux) = LC__(i,xQ0);
		k += LC__(i,xQ0);
	}
	j += NOFC;
	NOFC += k;
	while (j<NOFC) {
		for (i=0; i<CARD_(aux); i++) {
			k = Network->_N___[_S_____(xQ0->Chain[i])]->FxF;
			if (E_(i,aux)) {
				cet[j].Nix = _N___IX__(i,xQ0);
				cet[j].Cid = LC__(i,xQ0) - E_(i,aux);
				if (l<k) {
					cet[j].Fn = Network->_N___[_S_____(xQ0->Chain[i])]->ff[l];
					cet[j].fxd = 1;
				} else {
					cet[j].Fn = -1;
					cet[j].fxd = 0;
				}
				E_(i,aux)--;
				j++;
			}
		}
		l++;
	}
	Free_SET(aux);
}

void partition_net(int Min_prio, int Noofcar, int Min_dis, int Lim_dis,
		int Max_cell_demand, char * filna) {
	int celonf=0;
	NOFC=0;
	perm_net();
	LayerCard(Max_cell_demand);
	ShiftSpaceLeft_LC();
	NotEmpty = CARD_(NetworkSet);
	while (NotEmpty) {
		get_MLB(Min_prio, Noofcar, Min_dis, Lim_dis, Network->_E___, Qx, Q0);
		Invalid_set(Q0);
		Sect_cet(Q0);
	}
	write_cet(filna);
}

void NatOrd(int c, SetStruct *S) {
	int i=0;
	S->Card=c;
	while (i<S->Card) {
		S->Chain[i]=i;
		i++;
	}
}

void OrderNet(void) {
	int i, j;
	_N__E_T * x0;
	for (i=0; i<Network->Card; i++) {
		for (j=i+1; j<Network->Card; j++) {
			if (NC(i) < NC(j)) {
				x0 = _N__E(j);
				_N__E(j) = _N__E(i);
				_N__E(i) = x0;
			}
		}
		ID(i) = i;
	}
}

__inline int NDIS(int i, int j) {
	int p=0, a=0;
	_P__(Network->_E___,NIX(i),NIX(j),p)
	;
	if (p<PLOW)
		return (0);
	_D__(Network->_E___,NIX(i),NIX(j),a)
	;
	return (a);
}

void ShiftB(T_C___ * xN) {
	int i;
	for (i=0; i<NOFC; i++)
		if (xN[i].Fn > 0)
			xN[i].Fn -= Network->lb;
	Network->ub -= Network->lb;
	Network->lb = 0;
}

__inline void enab_net(NetworkStructure *xN, int ix) {
	int k;
	for (k=1; k<xN->B+1; k++)
		if (!xN->ENAB[NIX(ix)][k])
			xN->ENAB[NIX(ix)][k]=1;
}

__inline int GFBNCC(int a, int b, int ix, int lf) {
	int i=0, d1=0, d2=0, fx1= -1, fx2= -1, done1=0, done2=0;
	FFLX = -1;
	if (a == lf) {
		for (i= lf; (i < b + 1)&&!done1; i++) {
			switch (Network->ENAB[NIX(ix)][i+1]) {
			case -1:
				d1++;
				break;
			case 1:
				fx1 = i;
				done1=1;
				break;
			default:
				d1++;
				break;
			}
		}
		if (!done1)
			return (-1);
		FFLX = fx1;
		return (d1);
	}
	if (b == lf) {
		for (i= lf; (a-1 < i)&&!done1; i--) {
			switch (Network->ENAB[NIX(ix)][i+1]) {
			case -1:
				d1++;
				break;
			case 1:
				fx1 = i;
				done1=1;
				break;
			default:
				d1++;
				break;
			}
		}
		if (!done1)
			return (-1);
		FFLX = fx1;
		return (d1);
	}
    // RJ changed
	for (i= lf; (i != (1-DD)*a + DD*b + 2*DD - 1)&&!done1; i = i+ 2*DD - 1) {
//	for (i= lf; (i != (1-DD)*a + DD*b + 2*DD - 1)&&!done1 && (i< Network->B+1); i = i+ 2*DD - 1) {
		switch (Network->ENAB[NIX(ix)][i+1]) {
		case -1:
			d1++;
			break;
		case 1:
			fx1 = i;
			done1=1;
			break;
		default:
			d1++;
			break;
		}
	}
	if (!done1)
		d1= b-a+1;
	else if (!d1) {
		FFLX = fx1;
		return (0);
	}
	DD = cos_B(DD);
	done2 = 0;
    // RJ changed
	for (i= lf; (i != (1-DD)*a + DD*b + 2*DD - 1)&& !done2 ; i = i+ 2*DD - 1) {
//	for (i= lf; (i != (1-DD)*a + DD*b + 2*DD - 1)&& !done2 && (i< Network->B+1); i = i+ 2*DD - 1) {
		switch (Network->ENAB[NIX(ix)][i+1]) {
		case -1:
			d2++;
			break;
		case 1:
			fx2 = i;
			done2=1;
			break;
		default:
			d2++;
			break;
		}
	}
	DD = cos_B(DD);
	if (!done1&&!done2)
		return (-1);
	if (!done2)
		d2= b-a+1;
	else if (!d2) {
		FFLX = fx2;
		return (0);
	}
	if (d1 < d2) {
		FFLX = fx1;
		return (d1);
	}
	FFLX = fx2;
	return (d2);
}

__inline void GFB(int a, int b, int ix) {
	int i;
	FROL.frno = -1;
	FROL.frdis = 0;
	for (i= DD*a + (1-DD)*b; i != (1-DD)*a + DD*b + 2*DD - 1; i = i+ 2*DD - 1)
		switch (Network->ENAB[NIX(ix)][i+1]) {
		case -1:
			FROL.frdis++;
			break;
		case 1:
			FROL.frno = i;
			DD = cos_B(DD);
			return;
			break;
			break;
		default:
			break;
		}
}

__inline void GFL(int a, int ix) {
	int i;
	FROL.frno = Network->B;
	FROL.frdis = 0;
	for (i=a; MAX_(0,Network->ub-FrequencyBandRange+1)<=i; i--)
		switch (Network->ENAB[NIX(ix)][i+1]) {
		case -1:
			FROL.frdis++;
			break;
		case 1:
			FROL.frno = i;
			return;
			break;
		default:
			break;
		}
}

__inline void GFU(int a, int ix) {
	int i;
	FROR.frno = -1;
	FROR.frdis = 0;
	for (i=a; i<MIN_(Network->B,Network->lb+FrequencyBandRange); i++)
		switch (Network->ENAB[NIX(ix)][i+1]) {
		case -1:
			FROR.frdis++;
			break;
		case 1:
			FROR.frno = i;
			return;
			break;
		default:
			break;
		}
}

__inline int l_D(int ix, int n) {
	int a = NDIS(ix, n);
	a = FREQ(n) - a;
	return (a);
}

__inline int r_D(int ix, int n) {
	int a = NDIS(ix, n);
	a = FREQ(n) + a;
	return (a);
}

__inline void GLEGO(int a, int b, int ix) {
	int i;
	for (i=a+1; i<b; i++) {
		if ((i<0)||(Network->B - 1 < i))
			continue;
		Network->ENAB[NIX(ix)][i+1] =
		Network->ENAB[NIX(ix)][i+1] * (1 - Network->ENAB[NIX(ix)][i+1]) / 2;
		if (Network->B> i+ChannelRange)
			Network->ENAB[NIX(ix)][i+1+ChannelRange] =
			Network->ENAB[NIX(ix)][i+1+ChannelRange] * (1 - Network->ENAB[NIX(ix)][i+1+ChannelRange]) / 2;
		if (i-ChannelRange > 0)
			Network->ENAB[NIX(ix)][i+1-ChannelRange] =
			Network->ENAB[NIX(ix)][i+1-ChannelRange] * (1 - Network->ENAB[NIX(ix)][i+1-ChannelRange]) / 2;
	}
}

__inline void LEGO(int a, int b, int ix) {
	int i;
	for (i=a+1; i<b; i++) {
		if ((i<0)||(Network->B - 1 < i))
			continue;
		Network->ENAB[NIX(ix)][i+1] =
		Network->ENAB[NIX(ix)][i+1] * (1 - Network->ENAB[NIX(ix)][i+1]) / 2;
	}
}

__inline void VV(int ix, SetStruct * xX) {
	int i=0, l, r, esac=GROUPING;
	while (i<xX->Card) {
		l = l_D(ix, xX->Chain[i]);
		r = r_D(ix, xX->Chain[i]);
		if (esac && ((l < Network->lb)||(r > Network->ub)))
			GLEGO(l, r, ix);
		else
			LEGO(l, r, ix);
		i++;
	}
}

int RhoCC(int ix, SetStruct * xX) {
	int a, b, x;
	enab_net(Network,ix);
	VV(ix, xX);
	GFB(Network->lb,Network->ub,ix);
	if (FROL.frno >= 0)
		return (0);
	GFL(Network->lb,ix);
	a=FROL.frno;
	GFU(Network->ub,ix);
	b=FROR.frno;
	if (a != Network->B) {
		if (b != -1) {
			x = MIN_((Network->lb - a - FROL.frdis),(b - Network->ub - FROR.frdis));
			if ((Network->lb - a - FROL.frdis) < (b - Network->ub
					- FROR.frdis))
				b = Network->ub + 1 - a;
			else
				b = b + 1 - Network->lb;
			if (b>FrequencyBandRange)
				return (0);
			return (x);
		} else {
			x = Network->lb - a - FROL.frdis;
			b = Network->ub + 1 - a;
			if (b>FrequencyBandRange)
				return (0);
			return (x);
		}
	} else {
		if (b != -1) {
			x = b - Network->ub - FROR.frdis;
			b = b + 1 - Network->lb;
			if (b>FrequencyBandRange)
				return (0);
			return (x);
		} else {
			return (0);
		}
	}
}

int RhoLL(int ix, SetStruct * xX) {
	int x;
	FREQ(ix) = -1;
	x = FREQ(xX->Chain[xX->Card-1]);
	if (x < 0)
		return (-1);
	enab_net(Network,ix);
	VV(ix, xX);
	x = GFBNCC(Network->lb,Network->ub,ix,x);
	return (x);
}

int RRRX(int nix, SetStruct * xX) {
	enab_net(Network,nix);
	FREQ(nix) = -1;
	VV(nix, xX);
	GFB(Network->lb,Network->ub,nix);
	if (FROL.frno < 0)
		return (0);
	FREQ(nix) = FROL.frno;
	XC(NIX(nix))--;
	return (1);
}

void RR(int nix, SetStruct * xX) {
	enab_net(Network,nix);
	FREQ(nix) = -1;
	VV(nix, xX);
	GFL(Network->lb,nix);
	GFU(Network->ub,nix);
	if (FROL.frno < Network->B) {
		if (FROR.frno > -1) {
			if ((Network->lb - FROL.frno - FROL.frdis) < (FROR.frno
					- Network->ub - FROR.frdis)) {
				FREQ(nix) = FROL.frno;
				Network->lb = MIN_(Network->lb,FROL.frno);
				XC(NIX(nix))--;
				FBAND = True && ((Network->ub - FROL.frno + 1) < FrequencyBandRange);
				return;
			}
			FREQ(nix) = FROR.frno;
			Network->ub = MAX_(Network->ub,FROR.frno);
			XC(NIX(nix))--;
			FBAND = True && ((FROR.frno - Network->lb) < FrequencyBandRange);
			return;
		} else {
			FREQ(nix) = FROL.frno;
			Network->lb = MIN_(Network->lb,FROL.frno);
			XC(NIX(nix))--;
			FBAND = True && ((Network->ub - FROL.frno + 1) < FrequencyBandRange);
			return;
		}
	}
	FREQ(nix) = FROR.frno;
	Network->ub = MAX_(Network->ub,FROR.frno);
	XC(NIX(nix))--;
	FBAND = True && ((FROR.frno - Network->lb + 1) < FrequencyBandRange); //??JPB zu scharff!!! 
}

void init_cr(NetworkStructure *xN, ___P____ **CRX) {
	int nix = -1, njx = -1;
	for (nix=0; nix<xN->Card; nix++)
		for (njx=nix; njx<Network->Card; njx++) {
			strcpy(CRX[nix][njx],CRINITVAL);
			strcpy(CRX[njx][nix],CRINITVAL);
		}
}

void init_graph(NetworkStructure *xN, ___P____ **CRX) {
	int nix = -1, k;
	ALLN = 0;
	ClNo=0;
	for (nix=0; nix<xN->Card; nix++) {
		xN->Perm[nix] = 1;
		xN->_N___[nix]->COI = 0;
		xN->_N___[nix]->ADI = 0;
		xN->_N___[nix]->Clno = 0;
	}
	init_cr(xN, CRX);
	for (nix=0; nix<xN->Card; nix++) {
		xN->ENAB[nix][0] = xN->B;
		for (k=0; k<xN->B; k++)
			xN->ENAB[nix][k+1] = 0;
	}
}

void disab_carriers(void) {
	int cid, f;
	for (cid=0; cid<Network->Card; cid++)
		for (f=0; f<NumberOfChannels; f++)
			Network->ENAB[cid][FreqChannels[f]-FirstChannel+1] = 1;
	for (cid=0; cid<Network->Card; cid++)
		for (f=0; f<Network->B; f++)
			Network->ENAB[cid][f+1] -= 1;
}

/*
 * Import the CellFile as a new network and frequency plan
 */
BYTE read_net_new(char *fnet)
{
	int i, card=0, CNo = -9999;
	char *lx, *xx, aux[MaxNameL+1];
	FILE *fp;
	FDemand = 0;
	lx = (char*) calloc(MaxLineL+1,sizeof(char));
	if((fp =fopen(fnet,"r")) == NULL) {
		printf("ERROR opening file %s for reading: %s\n",fnet,strerror(errno));;
		return(False);
	}
	while (fgets(lx,MaxLineL,fp) != NULL)
	{
		if(lx[0] == '!') continue;
		card++;
	}
	rewind(fp);
	Network = AllocateNetworkMemory(card,MaxRTperCell);
	Network->Card = 0;
	Network->FXF = 0;
	while (fgets(lx,MaxLineL,fp) != NULL)
	{
		if(lx[0] == '!') continue;
		if(lx[0] == '#') continue;
		jpb_strAsubst (lx, "  "," "); jpb_strAsubst (lx, "\t\t","\t");
		_N__E(Network->Card)->S[0] = '\0';_N__E(Network->Card)->SCL = '0';
		sscanf(lx,"%s",_N__E(Network->Card)->S);
		xx = lx;
		xx = xx + strlen(_N__E(Network->Card)->S)+1;
		sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;
		sscanf(aux,"%d",&_N__E(Network->Card)->TSS);
		sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;
		sscanf(aux,"%d",&_N__E(Network->Card)->SS);
		sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;
		sscanf(aux,"%d",&NC(Network->Card));
		_N__E(Network->Card)->FxF=0; _N__E(Network->Card)->AxF=0;
		if(!(NC(Network->Card)> 0)) {
			logInfo("!!!ERROR reading file %s in line: wrong RT-demand > %s\n",fnet,lx);
			continue;
		}
		FDemand += NC(Network->Card);
		if(1 == sscanf(xx,"%s",aux)) {
			xx = xx + strlen(aux)+1;
			if(1 == sscanf(aux,"%d",&_N__E(Network->Card)->FxF)) {
				if(_N__E(Network->Card)->FxF) {
					Network->FXF += _N__E(Network->Card)->FxF;
					for(i=0;i<_N__E(Network->Card)->FxF;i++)
					{
						if(1 != sscanf(xx,"%s",aux)) {
							logInfo("!!!ERROR reading file %s in line %s\n",fnet,lx);
							return(0);
						}
						xx = xx + strlen(aux)+1;
						sscanf(aux,"%d",&_N__E(Network->Card)->ff[i]);
						_N__E(Network->Card)->ff[i] -= FirstChannel;
					}
				}
			}
		}
		ID(Network->Card) = Network->Card;
		_N__E(Network->Card)->SCL = (char) ((int)'A' + _N__E(Network->Card)->TSS - 1);
		XC(Network->Card) = NC(Network->Card);
		Network->Card++;
	}
	free(lx);
	fclose(fp);
	return(True);
}

BYTE read_forbidden(char *fnet)
{
	int i, cno=0, nof=0, cid= -1, forb = -1;
	char *lx, *xx, aux[MaxNameL+1], site[MaxNameL+1];
	FILE *fp;
	lx = (char*) calloc(MaxLineL+1,sizeof(char));
	if((fp =fopen(fnet,"r")) == NULL) {
		printf("ERROR opening file %s for reading: %s\n",fnet,strerror(errno));;
		return(False);
	}
	while (fgets(lx,MaxLineL,fp) != NULL)
	{
		if(lx[0] == '!') continue;
		jpb_strAsubst (lx, "  "," "); jpb_strAsubst (lx, "\t\t","\t");
		sscanf(lx,"%s",site);
		xx = lx;
		xx = xx + strlen(site)+1;
		sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;
		sscanf(aux,"%d",&cno);
		cid = g___n___(site, cno);
		if(cid<0)
		{	printf("!!ERROR in file %s: cell %s %d not found\n",fnet,site,cno);
			return(False);
		}
		sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;
		sscanf(aux,"%d",&nof); if(!nof) continue;
		Network->ENAB[cid][0] -= nof;
		if(Network->ENAB[cid][0] < Network->_N___[cid]->NC) {
			printf(
					"!!!ERROR number of frequencies allowed=%d < %d=required for the cell %s %d\n",
					Network->ENAB[cid][0],Network->_N___[cid]->NC,site,cno);
			free(lx);
			fclose(fp);
			return(0);
		}
		for(i=0;i<nof;i++) {
			if(1 != sscanf(xx,"%s",aux)) {
				logInfo("!!!ERROR reading file %s in line %s\n",fnet,lx);
				free(lx);
				fclose(fp);
				return(0);
			}
			xx = xx + strlen(aux)+1;
			sscanf(aux,"%d",&forb);
			if(!(forb-FirstChannel<0)) Network->ENAB[cid][forb-FirstChannel+1] = -1;
		}
	}
	free(lx);
	fclose(fp);
	return(True);
}

BYTE read_exceptions(char *fnet, int pri, ___P____ ** CRX)
{
	int cno1=0,cno2=0, cid1= -1,cid2= -1, dexc = 0;
	char *lx, site1[MaxNameL+1],site2[MaxNameL+1];
	FILE *fp;
	lx = (char*) calloc(MaxNameL+1,sizeof(char));
	if((fp =fopen(fnet,"r")) == NULL) {
		logInfo("ERROR opening file %s for reading: %s\n",fnet,strerror(errno));;
		printf("ERROR opening file %s for reading: %s\n",fnet,strerror(errno));;
		return(False);
	}
	while (fgets(lx,MaxLineL,fp) != NULL)
	{
		if(lx[0] == '!') continue;
		jpb_strAsubst (lx, "  "," "); jpb_strAsubst (lx, "\t\t","\t");
		sscanf(lx,"%s %d %s %d %d",site1,&cno1,site2,&cno2,&dexc);
		cid1 = g___n___(site1, cno1); cid2 = g___n___(site2, cno2);
		if((cid1<0)||(cid2<0)) {
			logInfo("!!ERROR in file %s: on of cells %s %d, %s %d not found\n",
					fnet,site1,cno1,site2,cno2);
			printf("!!ERROR in file %s: on of cells %s %d, %s %d not found",
					fnet,site1,cno1,site2,cno2);
			return(False);
		}
		if(!(dexc<0)) {
			P__(CRX,cid1,cid2,pri); P__(CRX,cid2,cid1,pri);
			D__(CRX,cid1,cid2,dexc); D__(CRX,cid2,cid1,dexc);
		}
	}
	free(lx);
	fclose(fp);
	return(True);
}

void son_closure_it(int p_prio, int n_prio, int dis, int tresh, int itr,
		___P____ ** rel) {
	int od=0, i, j, k, adj1=100, adj2=100, co1=0, co2=0;
	long xx=0;
	for (i=0; i<Network->Card; i++) {
		for (j=0; j<Network->Card; j++) {
			if (j != i) {
				_P__(rel,i,j,adj1)
				;
				if (adj1 < p_prio)
					continue;
				for (k=0; k<Network->Card; k++) {
					if (!((k != i)&&(k != j)))
						continue;
					_P__(rel,i,k,adj1)
					;
					if (adj1 < p_prio)
						continue;
					_P__(rel,j,k,adj1)
					;
					_P__(rel,k,j,adj2)
					;
					adj1 = MAX_(adj1,adj2);
					if (!(adj1 < n_prio))
						continue;
					_C_(rel,j,k,co1)
					;
					_C_(rel,k,j,co2)
					;
					co1 =MAX_(co1,co2);
					if (!(tresh < co1))
						continue;
					co1 = MIN_(PPar7[____NS].pcmax,co1+itr);
					xx++;
					C_(rel,j,k,co1);
					C_(rel,k,j,co1);
					D__(rel,j,k,dis);
					D__(rel,k,j,dis);
					P__(rel,j,k,n_prio);
					P__(rel,k,j,n_prio);
					if (co1)
						PPar7[p_prio].pcoit1++;
				}
			}
		}
	}
	logInfo( "ADD so-neighbours  %ld\n", xx);
}

void set_cr_CC_it(int o_pri, int pri, int dis, int ctre, int atre,
		___P____ ** CRX) {
	int d1=0, nix = -1;
	for (nix=0; nix<Network->Card; nix++) {
		_P__(CRX,nix,nix,d1)
		;
		if (!(d1 < o_pri))
			continue;
		P__(CRX,nix,nix,pri);
		D__(CRX,nix,nix,dis);
		C_(CRX,nix,nix,ctre);
		if (ctre)
			PPar7[____C].pcoit1++;
		if (atre) {
			_A__(CRX,nix,nix,d1)
			;
			d1 += atre;
			d1 = MIN_(___A__,d1);
			A__(CRX,nix,nix,d1);
		}
	}
}

void set_cr_SCC_it(int o_pri, int pri, int dis, int ctre, int atre,
		___P____ ** CRX) {
	int d1=0, d2=0, nix = -1, njx = -1;
	ALLN = 0;
	for (nix=0; nix<Network->Card; nix++)
		for (njx=nix+1; njx<Network->Card; njx++) {
			_P__(CRX,nix,njx,d1)
			;
			if (!(d1 < o_pri))
				continue;
			if ( !strcmp(Network->_N___[nix]->S,Network->_N___[njx]->S) ) {
				P__(CRX,nix,njx,pri);
				P__(CRX,njx,nix,pri);
				D__(CRX,nix,njx,dis);
				D__(CRX,njx,nix,dis);
				_C_(CRX,nix,njx,d1)
				;
				_C_(CRX,njx,nix,d2)
				;
				d1 = MAX_(d1,d2);
				d1 += ctre;
				d1 = MIN_(___C__,d1);
				C_(CRX,nix,njx,d1);
				C_(CRX,njx,nix,d1);
				PPar7[____S].pcoit1++;
				_A__(CRX,nix,njx,d1)
				;
				_A__(CRX,njx,nix,d2)
				;
				d1 = (d1+d2)/2;
				d1 += atre;
				d1 = MIN_(___A__,d1);
				A__(CRX,nix,njx,d1);
				A__(CRX,njx,nix,d1);
				ALLN++;
			}
		}
}

BYTE read_neighbors(char *fname,int p_pri, int mind, int cadd, int aadd, ___P____ ** CRX)
{
	char lx[MaxLineL+1], nc[5], six[MaxNameL+1], snx[MaxNameL+1];
	FILE *fp;
	int xx=0,pri=0, sec=0, sno=0, nix = -1, njx = -1, d1,d2;
	six[0]='\0';
	if((fp =fopen(fname,"r")) == NULL) {
		printf("ERROR opening file %s for reading: %s\n",fname,strerror(errno));;
		return(False);
	}
	while (fgets(lx,MaxLineL,fp) != NULL)
	{
		if(lx[0] == '!') continue;
		sscanf(lx,"%s %s %d", nc, snx, &sno);
		if((nc[0] == 'N'))
		{
			njx = -1; njx = g___n___(snx,sno);
			if(njx<0)
			{
				logInfo("!!ERROR in file %s: cell %s %d not found\n",fname,snx,sno);
				printf("!!ERROR in file %s: cell %s %d not found\n",fname,snx,sno);
				return(False);
			}
			if(nix != njx) {
				_P__(CRX,njx,nix,pri);
				if(!(pri < p_pri)) continue;
				P__(CRX,nix,njx,p_pri); P__(CRX,njx,nix,p_pri);
				D__(CRX,nix,njx,mind); D__(CRX,njx,nix,mind);
				_C_(CRX,nix,njx,d1); _C_(CRX,njx,nix,d2); d1 = MAX_(d1,d2);
				d1 += cadd; d1 = MIN_(d1,PPar7[____2N].pcmax);
				C_(CRX,nix,njx,d1); C_(CRX,njx,nix,d1);
				if(d1) PPar7[____2N].pcoit1++;
				ALLN++;
			} else
			{
				logInfo("!!ERROR: reflexive neighborhood sector: %s.%d.Check the neighbour file.\n",six,sno);
			}
		}
		else
		{
			if(nc[0] != 'C') {
				logInfo("!!ERROR in file %s\n",fname);
				printf("!!ERROR in file %s\n",fname);
				return(False);
			}
			else
			{
				if( !strcmp(six,snx) && (sec==sno)) continue;
				strcpy(six,snx); sec = sno;
				nix = -1; nix = g___n___(six,sec);
				if(nix<0)
				{
					logInfo("!!ERROR in file %s: cell %s %d not found\n",fname,six,sec);
					printf("!!ERROR in file %s: cell %s %d not found\n",fname,six,sec);
					return(False);
				}
			}
		}
	}
	fclose(fp);
	logInfo("Set neigh Prio=%d Dis=%d >>> %d \n",p_pri,mind,ALLN);
	return(True);
}

/*
 * Read the interference file
 */
BYTE read_it(int o_pri, int p_pri, char *fname, ___P____ ** CRX, int traff_m)
{
	char lx[MaxLineL+1], subci[MaxNameL+1], subcx[MaxNameL+1], s1[8], s2[MaxNameL+1];
	FILE *fp;
	int ssno,isno=0, nix = -1, njx = -1, lk=0, noofi, icot, iadt,xt=0;
	double coa,cot,ada,adt,A = 0.0, T = 0.0;
	if((fp =fopen(fname,"r")) == NULL) {
		printf("ERROR opening file %s for reading: %s\n",fname,strerror(errno));;
		return(False);
	}
	while (fgets(lx,300,fp) != NULL)
	{
		if((lx[0] != 'S') && (lx[0] != 'I')) continue;
		if( lx[0] != 'S') {
			sscanf(lx,"%s %s %d %lf %lf %lf %lf %s",s1,s2,&lk,&coa,&cot,&ada,&adt,subci);
			isno = ( (int)subci[strlen(subci)-1] - (int)'A' ) + 1;
			subci[strlen(subci)-1] = '\0';
			nix = -1; nix = g___n___(subci,isno);
			if(nix<0) {
				logInfo("WARNING: Invalid INT %s %d in the line %s in file %s\n",
						subci,isno,lx,fname);
				continue;
			}
			_P__(CRX,njx,nix,icot); _P__(CRX,nix,njx,iadt);
			lk = MAX_(icot,iadt);
			if(!(lk < o_pri)) continue;
			if(!traff_m) {cot = coa; adt = ada;}
			if(T> 0.0) {
				icot=0;iadt=0;
				cot = cot/T;
				icot = MIN_(___C__,MAX_(0,(int) ceil(cot * ((double)IMAX) )));
				if(adt>0.0) {
					iadt = MIN_(___A__, MAX_(0,(int) ceil(100.0 * (adt/T)*0.32)));
				}
			} else {
				icot = 0;
				iadt = 0;
			}
			C_(CRX,njx,nix,icot);
			A__(CRX,njx,nix,iadt);
			if(lk < o_pri)
			{	P__(CRX,njx,nix,p_pri); P__(CRX,nix,njx,p_pri);}
			Network->_N___[njx]->COI += icot; Network->_N___[njx]->ADI += iadt;
		}
		else {
			sscanf(lx,"%s %s %d %lf %lf %d %s",s1,s2,&lk,&A,&T,&noofi,subcx);
			if(!traff_m) T = A;
			if(T> 0.0) {
				ssno = ( (int)subcx[strlen(subcx)-1] - (int)'A' ) + 1;
				subcx[strlen(subcx)-1] = '\0';
				njx = -1; njx = g___n___(subcx,ssno);
				if(njx<0) {
					logInfo("ERROR: Invalid SUBCELL %s %d in the file %s in line:\n",subcx,ssno,fname);
					logInfo("       %s\n",lx);
					fclose(fp);
					return(False);
				}
			}
			else {
				logInfo("ERROR: AREA/TRAFFIC value %lf not valid in the file %s in line:\n",T,fname);
				logInfo("       %s\n",lx);
				printf("ERROR: AREA/TRAFFIC value %lf not valid in the file %s in line:\n",T,fname);
				printf("       %s\n",lx);
				fclose(fp); return(False);
			}
		}
	}
	fclose(fp);
	return(True);
}

void coit_sym(___P____ ** CRX) {
	int d1=0, d2=0, nix = -1, njx = -1;
	for (nix=0; nix<Network->Card; nix++)
		for (njx=nix+1; njx<Network->Card; njx++) {
			d1=0;
			d2=0;
			_C_(CRX,nix,njx,d1)
			;
			_C_(CRX,njx,nix,d2)
			;
			if (d1 < d2) {
				C_(CRX,nix,njx,d2);
				continue;
			}
			if (d2 < d1)
				C_(CRX,njx,nix,d1);
		}
}

BOOL read_IT
(
		int o_pri,
		int p_pri,
		char * itfile,
		___P____ ** rel,
		int trafm
)
{
	if(!read_it(o_pri,p_pri,itfile, rel, trafm)) return(False);
	coit_sym(rel);
	return(True);
}

void FA_fixed(T_C___ * cetx, SetStruct * Qxx, SetStruct * cQ) {
	int i=0, j=0;
	while ((i<Network->FXF)&&(j<NOFC)) {
		if (cetx[cQ->Chain[j]].fxd) {
			Qxx->Chain[Qxx->Card++]=cQ->Chain[j];
			SkipEl(j, cQ);
			i++;
		} else
			j++;
	}
}

void c_coit1(___P____ ** rel) {
	int i, j, a1, a2;
	for (i=0; i<CARD_(Network); i++)
		for (j=i; j<CARD_(Network); j++) {
			_C_(rel,i,j,a1)
			;
			_C_(rel,j,i,a2)
			;
			a1 = MAX_(a1,a2);
			if (a1)
				CoIT1++;
		}
	MAX_SET = 1 + CoIT1 / 100;
	MIN_SET = 1 + (CoIT1 / (100*(1+QUALITY)));
	logInfo( "CoIT1 = %d, MAX_SET = %d, MIN_SET = %d\n", CoIT1, MAX_SET,
			MIN_SET);
}

int set_t_it(int ncl, int pri, int tresh, ___P____ ** rel) {
	int x, od=0, i, j, adj1=100, adj2=100, amin=tresh, card=0;
	;
	for (i=0; (i<CARD_(Network))&&(card<ncl); i++) {
		for (j=i; (j<CARD_(Network))&&(card<ncl); j++) {
			_P__(rel,i,j,adj1)
			;
			if (adj1 != pri)
				continue;
			_C_(rel,i,j,adj1)
			;
			_C_(rel,j,i,adj2)
			;
			x = MIN_(adj1,adj2);
			if (amin == x) {
				P__(rel,i,j,____T);
				P__(rel,j,i,____T);
				card++;
			}
		}
	}
	return (card);
}

int set_t_ex(int ncl, int pri, int odis, int ndis, ___P____ ** rel) {
	int x, od=0, i, j, adj1=100, adj2=100, done=0, card=0;
	;
	for (i=0; (i<CARD_(Network))&&(card<ncl); i++) {
		for (j=i+1; (j<CARD_(Network))&&(card<ncl); j++) {
			_P__(rel,i,j,adj1)
			;
			if (adj1 != pri)
				continue;
			_D__(rel,i,j,od)
			;
			_D__(rel,j,i,x)
			;
			od=MIN_(od,x);
			if (od != odis)
				continue;
			D__(rel,i,j,ndis);
			D__(rel,j,i,ndis);
			P__(rel,i,j,____T);
			P__(rel,j,i,____T);
			done=1;
			card++;
		}
	}
	return (card);
}

void set_ex(int ncl, int nprio, ___P____ ** rel) {
	int i, j, adj1=100, adj2=100, done=0, card=0;
	;
	for (i=0; (i<CARD_(Network))&&(card<ncl); i++) {
		for (j=i; (j<CARD_(Network))&&(card<ncl); j++) {
			_P__(rel,i,j,adj1)
			;
			if (adj1 != ____T)
				continue;
			P__(rel,i,j,nprio);
			P__(rel,j,i,nprio);
			card++;
			CoIT1Done++;
		}
	}
}

int extract_pair(int ncl, int nprio, int dis, ___P____ ** rel) {
	int i, j, adj1=100, card=0;
	;
	for (i=0; (i<CARD_(Network))&&(card<ncl); i++) {
		for (j=i; (j<CARD_(Network))&&(card<ncl); j++) {
			_P__(rel,i,j,adj1)
			;
			if (adj1 != ____T)
				continue;
			D__(rel,i,j,dis);
			D__(rel,j,i,dis);
			P__(rel,i,j,nprio);
			P__(rel,j,i,nprio);
			logInfo( "!!EXTRACT pair %d, %d = %s von %d\n", i, j, 
			Network->_N___[i]->S, CARD_(Network));
			card++;
		}
	}
	return (card);
}

int reset_ex_back(int ncl, int oprio, int dis, ___P____ ** rel) {
	int od=0, i, j, adj1=100, adj2=100, card=0;
	;
	for (i=0; (i<CARD_(Network))&&(card<ncl); i++) {
		for (j=i+1; (j<CARD_(Network))&&(card<ncl); j++) {
			_P__(rel,i,j,adj1)
			;
			if (adj1 != ____T)
				continue;
			P__(rel,i,j,oprio);
			P__(rel,j,i,oprio);
			D__(rel,i,j,dis);
			D__(rel,j,i,dis);
		}
	}
	return (card);
}

int reset_ex(int lev, int mod, int ncl, int oprio, int nprio, int dis,
		int tresh, ___P____ ** rel) {
	int od=0, i, j, adj1=100, adj2=100, card=0;
	;
	for (i=0; (i<CARD_(Network))&&(card<ncl); i++) {
		for (j=i+1; (j<CARD_(Network))&&(card<ncl); j++) {
			_P__(rel,i,j,adj1)
			;
			if (adj1 != ____T)
				continue;
			D__(rel,i,j,dis);
			D__(rel,j,i,dis);
			if (!lev) {
				P__(rel,i,j,oprio);
				P__(rel,j,i,oprio);
			} else {
				_C_(rel,i,j,od)
				;
				if (od > tresh) {
					P__(rel,i,j,oprio);
					P__(rel,j,i,oprio);
					C_(rel,i,j,(od-tresh));
					C_(rel,j,i,(od-tresh));
				} else {
					P__(rel,i,j,nprio);
					P__(rel,j,i,nprio);
					CoIT1Done++;
				}
			}
			card++;
		}
	}
	return (card);
}

double DET(double A, double B, double a, double b) {
	return ((A*b)-(B*a));
}
double V_l(double ux, double uy) {
	return (sqrt(Q_(ux)+Q_(uy)));
}
double SPV_(double ux, double uy, double vx, double vy) {
	return ((ux*vx) + ((uy)*(vy)));
}
//double  SPA_(ux,uy,vx,vy,a)      (V_l(ux,uy)*V_l(vx,vy)*cos(a))
double R_60_r_x(double x, double y) {
	return ((0.5*(x) + 0.5*sqrt(3.0)*(y)));
}
double R_60_r_y(double x, double y) {
	return ((-0.5*sqrt(3.0)*(x) + 0.5*(y)));
}
double R_60_l_x(double x, double y) {
	return ((0.5*(x) - 0.5*sqrt(3.0)*(y)));
}
double R_60_l_y(double x, double y) {
	return ((0.5*(y) + 0.5*sqrt(3.0)*(x)));
}
double AV_(double ux, double uy, double vx, double vy) {
	double x = SPV_(ux, uy, vx, vy)/(V_l(ux, uy)*V_l(vx, vy));
	x = MAX_(x,-1.0);
	x = MIN_(x,1.0);
	return (acos(x));
}
double A_L(double px, double py, double qx, double qy) {
	return (qy-py);
}
double B_L(double px, double py, double qx, double qy) {
	return (px-qx);
}
double C_L(double px, double py, double qx, double qy) {
	return ((py)*(qx) - (px)*(qy));
}
double LCut_x(double A, double B, double C, double a, double b, double c) {
	return ((-(b)*(C) + (B)*(c)) / DET(A, B, a, b));
}
double LCut_y(double A, double B, double C, double a, double b, double c) {
	return ((-(A)*(c) + (a)*(C)) / DET(A, B, a, b));
}
double JDIS_(double p0x, double p0y, double p1x, double p1y, double p2x,
		double p2y, double al) {
	double D=0.0;
	D = MAX_(DIS_(p0x,p0y,p1x,p1y),DIS_(p0x,p0y,p2x,p2y));
	return (D / al );
}

BYTE IsCut_HL
(
		double az1,
		double az2,
		double p1x,
		double p1y,
		double q1x,
		double q1y,
		double p2x,
		double p2y,
		double q2x,
		double q2y
)
{
	double A=A_L(p1x,p1y,q1x,q1y),B=B_L(p1x,p1y,q1x,q1y),C=C_L(p1x,p1y,q1x,q1y),
	a=A_L(p2x,p2y,q2x,q2y),b=B_L(p2x,p2y,q2x,q2y),c=C_L(p2x,p2y,q2x,q2y),
	D=DET(A,B,a,b), X0,Y0;
	if(D != 0.0) {
		X0=LCut_x(A,B,C,a,b,c),Y0=LCut_y(A,B,C,a,b,c);
	}
	if(QEQ(az1,az2)) return(0);
	if(SPV_(q1x-p1x,q1y-p1y,(X0 - p1x ),(Y0 - p1y)) < 0.0)
	return(0);
	if(SPV_(q2x-p2x,q2y-p2y,(X0 - p2x ),(Y0 - p2y)) < 0.0)
	return(0);
	return(1);
}

double sec_point_x(double lx, double ly, double az) {
	if (!QEQ(az,0.0) && !QEQ(az,jpbPI)) {
		if (az < jpbPI) {
			return (lx+1.0);
		} else {
			return (lx-1.0);
		}
	} else
		return (lx);
}

double sec_point_y(double lx, double ly, double az) {
	if (QEQ(az,0.0))
		return (ly + 1.0);
	if (QEQ(az,jpbPI))
		return (ly - 1.0);
	if (!QEQ(az,(jpbPI/2.0)) && !QEQ(az,(3.0*jpbPI/2.0))) {
		if (az < (jpbPI/2.0))
			return (ly + tan((jpbPI/2.0) - az));
		if (az < jpbPI)
			return (ly - tan(az - (jpbPI/2.0)));
		if (az < (3.0*jpbPI/2.0))
			return (ly - tan((3.0*jpbPI/2.0) - az));
		return (ly + tan(az - (3.0*jpbPI/2.0)));
	} else
		return (ly);
}

double direct_distance(double long1, double lat1, double azimuth1,
		double long2, double lat2, double azimuth2) {
	double p1x=sec_point_x(long1, lat1, azimuth1), p1y=sec_point_y(long1, lat1,
			azimuth1), p2x=sec_point_x(long2, lat2, azimuth2), p2y=sec_point_y(
			long2, lat2, azimuth2), p1_60l_x, p1_60l_y, p1_60r_x, p1_60r_y,
			p2_60l_x, p2_60l_y, p2_60r_x, p2_60r_y, MDIS=9999999.0, D=0.0,
			ALPH=0.0;
	p1_60l_x = T_60_l_x(long1,lat1,p1x,p1y);
	p1_60l_y = T_60_l_y(long1,lat1,p1x,p1y);
	p1_60r_x = T_60_r_x(long1,lat1,p1x,p1y);
	p1_60r_y = T_60_r_y(long1,lat1,p1x,p1y);
	p2_60l_x = T_60_l_x(long2,lat2,p2x,p2y);
	p2_60l_y = T_60_l_y(long2,lat2,p2x,p2y);
	p2_60r_x = T_60_r_x(long2,lat2,p2x,p2y);
	p2_60r_y = T_60_r_y(long2,lat2,p2x,p2y);
	D = DIS_(long1,lat1,long2,lat2);
	if (D < 0.0)
		printf("ERROR\n");
	ALPH = 1.0+AV_((p1x-long1), (p1y-lat1), (p2x-long2), (p2y-lat2));
	if ( BETP_(long1,lat1,long2,lat2,p2_60l_x,p2_60l_y,p2_60r_x,p2_60r_y) ||
	BETP_(long2,lat2,long1,lat1,p1_60l_x,p1_60l_y,p1_60r_x,p1_60r_y)) {
		D = D / ALPH;
		if (D < 0.0)
			printf("ERROR\n");
		return (D);
	}
	if (IsCut_HL((azimuth1-(jpbPI/3.0)), (azimuth2-(jpbPI/3.0)), long1, lat1,
			p1_60l_x, p1_60l_y, long2, lat2, p2_60l_x, p2_60l_y))
		MDIS = JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),
		Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),
		long1,lat1,long2,lat2,ALPH);
	if (IsCut_HL((azimuth1-(jpbPI/3.0)), (azimuth2+(jpbPI/3.0)), long1, lat1,
			p1_60l_x, p1_60l_y, long2, lat2, p2_60r_x, p2_60r_y))
		MDIS
				= MIN_(MDIS,
						JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),
								Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),
								long1,lat1,long2,lat2,ALPH));
	if (IsCut_HL((azimuth1+(jpbPI/3.0)), (azimuth2+(jpbPI/3.0)), long1, lat1,
			p1_60r_x, p1_60r_y, long2, lat2, p2_60r_x, p2_60r_y))
		MDIS
				= MIN_(MDIS,
						JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),
								Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),
								long1,lat1,long2,lat2,ALPH));
	if (IsCut_HL((azimuth1+(jpbPI/3.0)), (azimuth2-(jpbPI/3.0)), long1, lat1,
			p1_60r_x, p1_60r_y, long2, lat2, p2_60l_x, p2_60l_y))
		MDIS
				= MIN_(MDIS,
						JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),
								Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),
								long1,lat1,long2,lat2,ALPH));
	return (MIN_(MDIS,5.0*D));
	return (10.0*D);
}

int F1_Next2Last1(SetStruct * Xx, SetStruct * Xc) {
	int i=0, n, n0, f, ix = -1;
	///?    if(UBreak) return(ix);
	n0 = Network->B+1;
	while (i < Xc->Card) {
		f = Xc->Chain[i];
		n = RhoLL(f, Xx);
		if (n<0) {
			i++;
			continue;
		}
		if (!n) {
			FLAST = FFLX;
			return (i);
		}
		if (n < n0) {
			FLAST = FFLX;
			ix = i;
			n0 = n;
			i++;
			continue;
		}
		if (n == n0) {
			if (XC(NIX(f))> XC(NIX(Xc->Chain[ix]))) {
				FLAST = FFLX;
				ix = i; /////n0 = n;
			}
		}
		i++;
		///?	  UserBreak();
	}
	return (ix);
}

int F1_Next2Set1(SetStruct * Xx, SetStruct * Xc) {
	int i=0, n, n0, f, ix = -1;
	///?   if(UBreak) return(ix);
	n0 = Network->B;
	while (i < Xc->Card) {
		f = Xc->Chain[i];
		n = RhoCC(f, Xx);
		if (!n) {
			i++;
			continue;
		}
		if (n < 2)
			return (i);
		else if (n < n0) {
			ix = i;
			n0 = n;
		}
		i++;
		///?	  UserBreak();
	}
	return (ix);
}

BYTE F1_ExtendB1
(
		SetStruct * xQ,
		SetStruct * cX
)
{
	int kx = -1;
	BYTE done=True, ext=False;
	///?    if(UBreak) return(ext);
	while( FBAND && done && cX->Card )
	{
		kx = F1_Next2Set1(xQ,cX);
		if(-1 < kx)
		{
			RR(cX->Chain[kx],xQ);
			xQ->Chain[xQ->Card++]=cX->Chain[kx];
			SkipEl(kx,cX);
			ext = True;
		} else done=False;
		///?	  UserBreak();
	}
	return(ext);
}

BYTE F1_EFInB1
(
		SetStruct * xQ,
		SetStruct * cX
)
{
	int kx = -1;
	BYTE done=True, ext=False;
	///?     if(UBreak) return(ext);
	while( done && cX->Card )
	{
		kx = F1_Next2Last1(xQ,cX);
		if(-1 < kx)
		{
			FREQ(cX->Chain[kx]) = FLAST; XC(NIX(cX->Chain[kx]))--;
			xQ->Chain[xQ->Card++]=cX->Chain[kx];
			SkipEl(kx,cX);
			ext = True;
		} else done=False;
		///?	  UserBreak();
	}
	return(ext);
}

BYTE F1_FillInB1
(
		SetStruct * xQ,
		SetStruct * cX
)
{
	int ix=0;
	BYTE fill=False, done=True;
	///?     if(UBreak) return(fill);
	while( done && (ix<cX->Card) && cX->Card)
	{
		if(RRRX(cX->Chain[ix],xQ))
		{
			xQ->Chain[xQ->Card++]=cX->Chain[ix];
			SkipEl(ix,coX);
			fill=True;
		} else done = False;
		///?   UserBreak();
	}
	return(fill);
}

int F1_XOrderNet1(SetStruct * xQ, SetStruct * cQ) {
	int ddo=DD;
	BYTE proc=True, p1=True, p2=True;
	///?    if(UBreak) return(0);
	while (proc) {
		proc = (FBAND && F1_ExtendB1(xQ, cQ)) || F1_EFInB1(xQ, cQ)
				|| F1_FillInB1(xQ, cQ);
		///?   UserBreak();
	}
	return (cQ->Card);
}

int F1_get_last_enab(int cid) {
	int i;
	for (i=Network->B; 0<i; i--)
		if (Network->ENAB[cid][i]>0)
			return (i-1);
	return (-1);
}

int F1_FxCompFa1(T_C___ * xN, SetStruct * Qxx, SetStruct * cQ) {
	int i=0;
	if (!cQ->Card)
		return (0);
	if (!Network->FXF) {
		enab_net(Network, cQ->Chain[0]);
		if ((i = F1_get_last_enab(xN[cQ->Chain[0]].Nix)) < 0) {
			logInfo( "!!!!!!!!ERROR whole band unabled for cell %d\n",
					xN[cQ->Chain[0]].Nix);
			return (1);
		}
		xN[cQ->Chain[0]].Fn = i;
		Qxx->Chain[Qxx->Card++]=cQ->Chain[0];
		SkipEl(0, cQ);
		FBAND= True && ((Network->ub - Network->lb) < FrequencyBandRange);
	}
	return (F1_XOrderNet1(Qxx, cQ));
}

int F1_CompFa1(T_C___ * xN, SetStruct * Qxx, SetStruct * cQ) {
	int val=0;
	Qxx->Chain[0]=0;
	Qxx->Card++;
	xN[0].Fn = Network->B / 2;
	Network->lb = xN[0].Fn;
	Network->ub = Network->lb;
	FBAND=True;
	Diff_SET(cQ, Qxx);
	val = F1_XOrderNet1(Qxx, cQ);
	ShiftB(xN);
	return (val);
}

// This method appears to copy the contents of 'cet' into 'XCET' if some criteria is met
// Possibly this is the code that decides if a new plan is better, and saves is to the
// last best plan array, which is XCET.
int F1_analyze_FACC(T_C___ * cetx, SetStruct * Qxx, SetStruct * cQ) {
	int i, j;
	for (i=0; i<Network->Card; i++)
		XC(i) = NC(i);
	Qxx->Card = 0;
	NatOrd(NOFC, cQ);
	if (FIX) {
		Network->ub = Network->B -1;
		Network->lb = 0;
		if (Network->FXF)
			FA_fixed(cetx, Qxx, cQ);
		i = F1_FxCompFa1(cetx, Qxx, cQ);
	} else
		i = F1_CompFa1(cetx, Qxx, cQ);
	if (!i) {
		for (j=0; j<NOFC; j++)
			*(XCET+j) = *(cetx+j);
		PlExist = 1;
	} else
		strcpy(XSITE,Network->_N___[NIX(cQ->Chain[0])]->S);
	return (i);
}

int opt_all_it(int ncl, int iprio, int sprio, int oprio, int odis, int ndis,
		int tresh, ___P____ ** rel) {
	int QC=0, bt=tresh, lev=1;
	QC = set_t_it(ncl, iprio, tresh, rel);
	if (!QC)
		return (0);
	logInfo( ">>>>>>>SetStruct %d for TR=%d\n", QC, tresh);
	if (!F1_analyze_FACC(cet, Q0, coX)) {
		LASTQC = QC;
		set_ex(QC, sprio, rel);
		logInfo( ">>>>>>>!!!!SET %d to %d for TR=%d\n", QC, odis, tresh);
		return (1);
	}
	if (QC < MIN_SET+1) {
		logInfo( "--NOT SET %5d-Prio%d with TR=%3d\n", QC, iprio, tresh);
		extract_pair(QC, oprio, MAX_(1,ndis), rel);
		return (2);
	} else
		reset_ex_back(QC, iprio, odis, rel);
	flushLog();
	LASTQC = QC;
	return (3);
}

void set_step(int prio) {
	int QC = MIN_SET, AT = 0, AT1, lim, bt;
	PLOW = prio + 1;
	AT = PPar7[prio].pcmax;
	while (PPar7[prio].pclb < AT) {
		WPL=0;
		switch (opt_all_it(QC, prio, ____X, prio-1, PPar7[prio].pdisr,
				PPar7[prio].pdisr-1, AT, Network->_E___)) {
		case 0:
			AT--;
			QC = MAX_(QC,MIN_SET);
			break;
		case 1:
			WPL=1;
			AT1 = AT;
			QC = MAX_(MIN_SET,2*LASTQC);
			break;
		case 2:
			AT1 = AT;
			break;
		default:
			AT = AT1;
			QC = MAX_(MIN_SET,LASTQC/2);
			break;
		}
		lim = (int)((100.0*((float)CoIT1Done) / CoIT1));
		if (lim>PROGRESS) {
			PROGRESS = lim;
		}
	}
	bt = DD;
	if (F1_analyze_FACC(cet, Q0, coX)) {
		DD = 1-bt;
		F1_analyze_FACC(cet, Q0, coX);
	}
	flushLog();
}

int opt_all(int ncl, int iprio, int sprio, int oprio, int odis, int ndis,
		___P____ ** rel) {
	int QC;
	if (!ncl)
		return (0);
	QC = set_t_ex(ncl, iprio, odis, ndis, rel);
	if (!QC)
		return (0);
	if (!F1_analyze_FACC(cet, Q0, coX)) {
		LASTQC = QC;
		set_ex(QC, sprio, rel);
		return (1);
	}
	if (QC < MIN_SET+1) {
		logInfo( "!!!!!!!!!!!Not set are:>>>>>\n");
		reset_ex(1, 0, QC, iprio, oprio, odis, ___C__, rel);
		return (2);
	} else
		reset_ex(0, 1, QC, iprio, oprio, odis, ___C__, rel);
	flushLog();
	return (3);
}

BYTE set_all
(
		int prio
)
{
	int noass=0;
	PLOW = prio;
	if((noass=F1_analyze_FACC(cet, Q0, coX))) {
		logInfo("ERROR:check failed\n");
		print_out_plan_dump(cet);
		flushLog();
		return(False);
	}
	CoIT1Done += PPar7[prio].pcoit1;
	print_out_plan_dump(cet);
	flushLog();
	///?UserBreak();
	return(True);
}

void write_ctrl(char * pctrfile) {
	int i=0;
	char lx[MaxLineL+1];
	FILE * fctrp;
	if ((fctrp =fopen(pctrfile, "r")) == NULL) {
		printf("ERROR opening file %s for reading: %s\n", pctrfile,
				strerror(errno));;
		return;
	}
	logInfo( "CONTROL FILE:::>\n");
	while ((i<26 )&& (fgets(lx, MaxLineL, fctrp) != NULL)) {
		logInfo( lx);
	}
	logInfo( "CONTROL FILE:::<\n\n\n");
	fclose(fctrp);
}

/*
 * This method reads the control file setting up the configuration for the AFP optimization
 */
BOOL read_control_file(char * pctrfile) {
	int i=0,j=0;
	char ax[MaxLineL],lx[MaxLineL+1], *xx;
	FILE * fctrp;
	XSITE[0] = '\0';
	if((fctrp =fopen(pctrfile,"r")) == NULL) {
		printf("ERROR opening file %s for reading: %s\n",pctrfile,strerror(errno));;
		return(False);
	}
	while((i<26 )&& (fgets(lx,MaxLineL,fctrp) != NULL))
	{
		switch(i) {
			case 0:
				sscanf(lx,"%s %d",ax,&SiteSpacing);i++; continue; break;
				//sscanf(lx,"%s %d",ax,&NetworkSet);i++; continue; break;	// THERE WAS A BUG HERE, WRONG VARIABLE USED
			case 1:
			sscanf(lx,"%s %d",ax,&CellSpacing);i++; continue; break;		// Used as CellSpacing and MAXDIS
			case 2:
			sscanf(lx,"%s %d",ax,&NeighbourSpacing);i++; continue; break;
			case 3:
			sscanf(lx,"%s %d",ax,&MinNeighbourSpacing);i++; continue; break;
			case 4:
			sscanf(lx,"%s %d",ax,&SecondNbrSpacing);i++; continue; break;	// Ignored!!
			case 5:
			sscanf(lx,"%s %d",ax,&ReCalAll);i++; continue; break;			// Ignored!! (HOW DO WE GENERATE A NEW PLAN!!)
			case 6:
			sscanf(lx,"%s %d",ax,&UseTraffic);i++; continue; break;			// Used in read_it, simply copies area values over traffic is this is not set
			case 7:
			sscanf(lx,"%s %d",ax,&USON);i++; continue; break;
			case 8:
			sscanf(lx,"%s %d",ax,&QUALITY);i++; continue; break;
			case 9:
			sscanf(lx,"%s %d",ax,&DECOMP);i++; continue; break;
			case 10:
			sscanf(lx,"%s %d",ax,&ExCliq);i++; continue; break;
			case 11:
			sscanf(lx,"%s %d",ax,&MaxRTperCell);i++; continue; break;
			case 12:
			i++;	// Ignoring MaxRTperSite
			continue; break;
			case 13:
			sscanf(lx,"%s %d",ax,&HoppingType);i++; continue; break;	// Ignored!!
			case 14:
			sscanf(lx,"%s %d",ax,&UseGrouping);i++; continue; break;	// Used as GROUPING global
			case 15:
			sscanf(lx,"%s %d",ax,&NumberOfGroups);i++; continue; break;	// Ignored!!
			case 16:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(LogFile,xx+1);	// Logfile creation code is commented out to use STDOUT instead so AWE can react to the results - perhaps we should duplicate output to both
				if( (xx = strchr(LogFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration File line %d\n",i+1);
			break;
			case 17:
			sscanf(lx,"%s %d",ax,&CellCardinality);i++; continue; break;	// Ignored!!!
			case 18:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(CellFile,xx+1);
				if( (xx = strchr(CellFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration File line %d\n",i+1);
			break;
			case 19:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(NeighboursFile,xx+1);
				if( (xx = strchr(NeighboursFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			case 20:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(InterferenceFile,xx+1);
				if( (xx = strchr(InterferenceFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			case 21:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(PlanFile,xx+1);
				if( (xx = strchr(PlanFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			case 22:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(CliquesFile,xx+1);
				if( (xx = strchr(CliquesFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			case 23:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(ForbiddenFile,xx+1);
				if( (xx = strchr(ForbiddenFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			case 24:
			if( (xx = strchr(lx,(int)('"'))) != NULL)
			{	strcpy(ExceptionsFile,xx+1);
				if( (xx = strchr(ExceptionsFile,(int)('"'))) != NULL) xx[0]='\0';
				i++; continue;}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			case 25:
			if( ((xx = strchr(lx,(int)(' '))) != NULL) ) {
				strcpy(lx,xx+1);
				sscanf(lx,"%d",&NumberOfChannels); ///?????
				FreqChannels = (int *) calloc(NumberOfChannels,sizeof(int));
				AllocError(FreqChannels,NumberOfChannels*sizeof(int),"FreqChannels",exit(0);)
				j=0;
				while( ((xx = strchr(lx,(int)(' '))) != NULL) && (j<NumberOfChannels )) {
					strcpy(lx,xx+1);
					sscanf(lx,"%d",&FreqChannels[j]);j++;
				}
				i++; continue;
			}
			else printf("ERROR by reading Configuration Fileline %d\n",i+1);
			break;
			default :
			break;
		}
	}
	FirstChannel = FreqChannels[0];
	ChannelRange = FreqChannels[NumberOfChannels-1] - FirstChannel + 1;
	ALLN = 0; SONCC =0;
	FrequencyBandRange = ChannelRange;
	if(!read_net_new(CellFile)) return(0);
	if(!FDemand) return(1);
	networkSet = AllocateSetStruct(FDemand);
	cet = A___C__(FDemand);
	Qx = AllocateSetStruct(FDemand);
	coX = AllocateSetStruct(FDemand);
	Q0 = AllocateSetStruct(FDemand);
	MAXDIS = CellSpacing;
	FIX = 1 && (Network->FXF + strlen(ForbiddenFile) + (int)(ChannelRange != NumberOfChannels));
	a_____e___(FIX,Network);
	init_graph(Network,Network->_E___);
	if(ChannelRange != NumberOfChannels) disab_carriers();
	OrderNet();
	NatOrd(Network->Card,networkSet);
	LayerCard(MaxRTperCell);
	if(strlen(ForbiddenFile)) {
		FORBF = 1;
		if(!read_forbidden(ForbiddenFile)) {
			fclose(fctrp);
			return(False);;
		}
	}
	GROUPING = UseGrouping;
	fclose(fctrp);
	return(True);
}

int japa_awe
///?japa1
(char * pctrfile) {
	BOOL cr_read=False,it_read=False, COIT=False,ADIT=False;
	int lim=0, QC=0, QC2=0, nt=0, pt=1000, AT1=0, unb=1, uit=0, p=0;
	double x=0.0;
	char c='\0';
	PlExist = 0;
	fflush(stdin);
	if (!read_control_file(pctrfile)) {
		free_memory();
		return (FILE_ERROR);
	}
	unb = unb && (MinNeighbourSpacing+NeighbourSpacing);
	USON = USON && unb;
	COFSET = 333;
	IMAX = 331;
	SONMIN = NNSIGN_(NeighbourSpacing)*USON*COFSET;
	NMIN = COFSET + SONMIN;
	for (p=9; 0<p; p--) {
		PPar7[p].pcmax = 999;
		PPar7[p].pclb = 0;
		PPar7[p].pcoit1 = 0;
		PPar7[p].pdisr = 2;
		PPar7[p].pdism = 1;
	}
	PPar7[____C].pdisr = 3;
	PPar7[____S].pdisr = 2;
	PPar7[____2N].pdisr = 2;
	PPar7[____NS].pdisr = 1;
	PPar7[____C].pclb = 998;
	PPar7[____S].pclb = 997;
	PPar7[____2N].pclb = NMIN-1;
	PPar7[____NS].pclb = SONMIN-1;
	if ((fp_log =fopen(LogFile, "w")) == NULL) {
		logInfo("ERROR opening log file %s: %s\n", LogFile, strerror(errno));
		return (FILE_ERROR);
	}
	write_ctrl(pctrfile);
	if (strlen(ExceptionsFile)) {
		EXCEPF = 1;
		if (!read_exceptions(ExceptionsFile, ____X, Network->_E___)) {
			logInfo( "ERROR by reading Exception-File\n\n");
			free_memory();
			return (FILE_ERROR);
		}
	}
	CoIT1 = 0;
	CoIT1Done = 0;
	logInfo( "\nJPB-FA IT read ??======================\n");
	if (!uit && !read_IT(____X,____CI,InterferenceFile,Network->_E___,UseTraffic)) {
		logInfo( "\nJPB-FA IT read ======================\n");
		free_memory();
		return (NR_ERROR);
	}
	uit = 1;
	if (CellSpacing) {
		logInfo( "??CCC-check:\n\n");
		set_cr_CC_it(____X,____C,CellSpacing,___C__,0,Network->_E___);
	}
	if (NetworkSet) {
		logInfo( "??SCC-check:\n\n");
		set_cr_SCC_it(____C,____S,SiteSpacing,___C__,0,Network->_E___);
	}
	if (unb) {
		if (!read_neighbors(NeighboursFile, ____2N, 1, NMIN, 33, Network->_E___)) {
			logInfo( "ERROR by reading Neighbour-File\n\n");
			free_memory();
			return (NR_ERROR);
		}
	}
	if (unb && USON)
		son_closure_it(____2N,____NS,1,-1,SONMIN,Network->_E___);
	if (!DECOMP) {
		IDform_Set(Network->Card,Q0);
		Sect_cet(Q0);
	} else {
		partition_net(____2N,NumberOfChannels,1,1,MaxRTperCell,CliquesFile);
	}
	///////?if(getch()) printf("HALLO!!");
	///? p=0; if((p=getchar())>0) printf("HALLO!!");
	c_coit1(Network->_E___);
	PROGRESS = 0;
	for (p=____C; (NOLOCL < p); p--) {
		//logInfo("PROGRESS CoIT1Done/CoIT1,%d,%d,\n",CoIT1Done,CoIT1);
		LogProgress(CoIT1Done, CoIT1);
		logInfo( "PRIO = %d, DISR = %d  >>>>>\n", p, PPar7[p].pdisr);
		if (!set_all(p))
			set_step(p);
		logInfo( "<<<<<<<<<<<<<<<PRIO = %d, DISR = %d\n", p,
				PPar7[p].pdisr);
		lim = (int)((100.0*((float)CoIT1Done) / CoIT1));
		if (lim>PROGRESS) {
			PROGRESS = lim;
			///?	     SetProgressBar(lim);
		}
		///?	 fflush(stdin);   
		///? UNIX      UBreak = read(0,&c,1); 
		///?	 UBreak = getch();
		///?if(UBreak = getc(stdin)) > 0;
		///?	 UBreak = getc(stdin);
		///?	 UBreak++;
		///?logInfo("DANACH   UBREAK = %d>>>>\n",UBreak);
		///?	 UBreak=kbhit();
		///?UserBreak;
	}
	print_out_plan_dump(cet);
	flushLog();
	write_frequency_plan(1);
	// TODO: Why not free memory where?
	///?	 free_memory();
	return (NO_ERROR);
}

/*int main()
 {
 char TrialName[MaxNameL+1];
 printf("\n\nFullPath-Name of the Trial Control File:\n\n");
 scanf("%s",TrialName);
 japa_awe(TrialName);
 printf("\n\njapa_awe for %s finished\n\n", TrialName);
 getch();
 }
 */

// This method seems to be UNUSED, but is similar to write_frequency_plan(int), which is used
int write_last_plan(int pmod) //(void)
{
	if (!PlExist)
		return (0);
	///?	(1,XCET);             ///?write_FA(1,XCET);
	///?    write_ceplan(pmod,PlanFile,XCET,PBAND);
	flushLog();
	return (1);
}

/*
 * Free all memory
 * TODO: How does this differ from free_memory?
 * (I see no calls to free_all, but do see many calls to free_memory,
 * so perhaps free_all is the old version, and free_memory is the new one?)
 */
void free_all(void) {
	int i;
	if (Network) {
		for (i=0; i<Network->Card; i++) {
			if (Network->_N___[i]) {
				free(Network->_N___[i]->ff);
				free(Network->_N___[i]);
			}
		}
		free(Network->_N___);
		for (i=0; i<Network->Card; i++)
			free(Network->_E___[i]);
		free(Network->_E___);
		free(Network->Perm);
		free(Network->ENAB);
		free(Network);
	}
	if (cet)
		free(cet);
	if (XCET)
		free(XCET);
	if (networkSet) {
		free(networkSet->Chain);
		free(networkSet);
	}
	if (Qx) {
		free(Qx->Chain);
		free(Qx);
	}
	if (coX) {
		free(coX->Chain);
		free(coX);
	}
	if (Q0) {
		free(Q0->Chain);
		free(Q0);
	}
	closeLog();
	unlink(CliquesFile);
}
