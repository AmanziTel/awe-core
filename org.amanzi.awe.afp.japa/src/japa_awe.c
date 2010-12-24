
/* ======================================

FILE:   japa_awe.c 

AUTHOR: JPB

DATE:«  2010-07-25

========================================= */

#include <stdlib.h>

#include <stdio.h>

//#include <conio.h>

#include <string.h>

#include <math.h>

///?UNIX #include <syscalls.h>



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

#define AllErr_(X,msg,act) if(X==NULL){printf("\n%s\n",msg);act;}





#define MAX_(a,b)     (((a)<(b)) ? (b) : (a))

#define MIN_(a,b)     (((a)<(b)) ? (a) : (b))

#define sum_B(x,a,b)  (sin_B(x)*(a) + cos_B(x)*(b))

#define PP(x)         (x++)

#define MM(x)         (x--)

#define NEXT_B(x,a)   if_else_B(x,((a) += 1),((a) -= 1))

#define SIGN_(a)      ((0 < (a)) ? 1 : -1)

#define NNSIGN_(a)    ((0 < (a)) ? 1 : 0)



#define Free_SET(M)     if(M!=NULL){free(M->Chain);free(M);M=NULL;}





#define _S____         _n____t    

#define _N______        _n______          

/*///?#define _S____         _n____t      

#define NETCARD        _N______->Card

#define CARD_(Q)       Q->Card

#define E_(i,Q)        Q->Chain[i]

///?#define CARD_(Q)       Q->Card

#define _S_____(i)      E_(i,_S____)     

#define _N___IX_(i)     E_(i,_S____)

*/

#define _N__E(i)       _N______->_N___[i]

#define ID(i)         _N______->_N___[i]->Id

#define NC(i)         _N______->_N___[i]->NC

#define CNC(i)        _N______->_N___[i]->CNC

#define COI(i)        _N______->_N___[i]->COI

#define ADI(i)        _N______->_N___[i]->ADI

#define XC(i)         _N______->_N___[i]->XC

#define LC(i)         _N______->_N___[i]->LC

#define _ED__(i,j)     _N______->_E___[i][j]

#define PERM(i)       _N______->Perm[i]



///?#define _S____         _n____t      

#define NETCARD        _N______->Card

#define CARD_(Q)       Q->Card

#define E_(i,Q)        Q->Chain[i]

///?#define CARD_(Q)       Q->Card

#define _S_____(i)      E_(i,_S____)     

#define _N___IX_(i)     E_(i,_S____)





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



#define NO_E       0

#define SCC_E      3

#define NR_E       4



#include <ctype.h>

__inline void _my_sscanf1(char* p, int* r)  

{

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





FILE *Protfp; //,*fp;



typedef char   ___P____[L__P____];



typedef struct cell {int  Nix,           

                          Cid,               

						  Fn;           

                     short fxd;        

					} T_C___;



typedef struct freqob {int   frno, frdis;} FREQOB_T;





typedef struct set { 

                    int * Chain;

                    int   Card;

					} T_S__;	



typedef struct node                        

               {

                 long      COI,            

					       ADI,            

						   Clno;           

                 char      S[MaxNameL],  SCL;   



                 int       TSS, SS,      

					       Id;            



                 int       *ff, NC,       

				           LC, 

						   XC,            

						   AxF,           

						   FxF;          

               }  _N__E_T;





typedef struct netstruct

               {

                int        B,             

					       lb,            

						   ub,            

						   FXF,          

						   Card,         

						   LB0,LB[3];	

														

                _N__E_T    ** _N___;     

                ___P____  ** _E___;      

                short     ** ENAB;      



				BYTE      * Perm;      

				}   T_N__;



typedef struct priopar

               {int   pdisr, pdism,

			          pcmax, pclb,

					  pamax, palb,

                      pcoit1;      } PPAR_T;

PPAR_T   PPar7[ParCARD];



/*typedef struct pair  {int   Left, Right;  } PAIR_T;



typedef struct intvset  { int Card;   PAIR_T  * IChain;} INVT_S__;*/

    

			



FREQOB_T FROL, FROR;

T_N__  *_n______;

T_C___ * cet, * XCET;        

T_S__  *_n____t, *Qx, *coX, *Q0, *MaxNC;

long    SoNCC=0,SONCC=0,CoNCC=0,AdNCC=0,CoIT0=0,CoIT1=0,CoIT1Done=0, ClNo=0;

int     /*UBreak=0,*/AT=0,PlExist=0,Lay = 0, FDemand=0 , NOFC=0, XSC=0, FBANDL=0, 

		MAXDIS = 0,COCard=0,UMTS = 0,PLOW=0,PROGRESS=0,

        WPL=0,GROUPING=0,ALLN=0,ELEN=0,FIX=0,EXCEPF=0,FORBF=0,

		COFSET=333,IMAX=331,SONMIN=333,NMIN=666,

		NS_N0=0,NS_N1=0,NS_SON0=0,NS_ICO=0,N2_L=0,

        SET_0_1=0,N2LLIM=0,FLAST = -1, FFLX = -1, MIN_SET = 20, MAX_SET = 200,LASTQC=0, 

        FACC1=0,FACC2=0,FB_LB, FB_RB,

		AdIT1=0,AdIT2=0,FACC=0;

        DD=1,DDo=1, NotEmpty=0, NOFFMIN=0,  OSET=0;



BOOL    FBAND=True;

double   TvA_S0 = 1.0, TvA_S1=1.0, TvAI0=10e10,TvAI1=0.0,  T2Ai = 10e10, 

         TAx =0.0,TAi=10e10; 



char * PLFILE, XSITE[MaxNameL+1];



int _C____ =3, _S___ =2, _N_____=1, _N____=2,  _O____=1, 

    ReCalAll=0,

	UTraf=0,

	USON=0,

	QUALITY=50,

    DECOMP=1,

	ExCliq=0,

	HType, UGroup, NOGR, CCard; 

    NOFF,NOC,CMaxRt,                              

   * FC____; 



char C_F___[MaxFileName+1],

     N_F___[MaxFileName+1],

     I_F___[MaxFileName+1],

     P_F___[MaxFileName+1],   ///?planfile

     ClF___[MaxFileName+1],

     PrF___[MaxFileName+1],   ///?protfile

     F_F___[MaxFileName+1],

     E_F___[MaxFileName+1];







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







void  jpb_strAsubst 

(

   char * here, 

   char * old, 

   char * neu

)

{

char *fo, *aux;

long  ol=0,nl = 2*(strlen(here)+strlen(neu));

    aux = (char *) calloc(nl,sizeof(char));

    ol = strlen(old);

    nl = strlen(neu);

    strcpy(aux,neu);

    fo = here;

    while((fo = strstr(here,old)) != NULL)

    {

       strcpy(aux+nl,fo+ol);

       strcpy(fo,aux);

    }

    free(aux);

}





void  jcpyl                        

(

  char * into,

  char * str

)

{

char * aux=(char*)calloc(strlen(into)+1,sizeof(char));

   strcpy(aux,into+strlen(str));                           

   strcpy(into,str);

   strcat(into,aux);

   free(aux);

}

void  jprtl                   

(

  char * into,

  int    value,

  int    len

)

{

char * aux=(char*)calloc(len+1,sizeof(char));

   switch(len)

   {

   case 1: sprintf(aux,"%1d",value);break;

   case 2: sprintf(aux,"%2d",value);break;

   case 3: sprintf(aux,"%3d",value);break;

   default:break;

   }

   jcpyl(into,aux);

   free(aux);   

}



T_N__ * A___N__

(

  int  dim,         

  int  bim        

)

{

int i;

T_N__ *_N______;



   _N______ = (T_N__ *) calloc(1,sizeof(T_N__));

AllErr_(_N______,"AllocErr by alloc _N______",exit(0);)

 

   _N______->_N___ = ( PTR(_N__E_T) *) calloc(dim,sizeof(PTR(_N__E_T)));      

AllErr_(_N______->_N___,"AllocErr by alloc _N______->_N___",exit(0);)

   for(i=0;i<dim;i++)

   {

    _N______->_N___[i] = (_N__E_T *) calloc(1,sizeof(_N__E_T));                

    AllErr_(_N______->_N___[i],"AllocErr by alloc _N______->_N___[]",exit(0);)

    _N______->_N___[i]->ff = (int*) calloc(bim,sizeof(int));                  

   }

   _N______->_E___ = (PTR(___P____) *) calloc(dim,sizeof(PTR(char)));         

AllErr_(_N______->_E___,"AllocErr  by alloc _N______->_E___",exit(0);)

   for(i=0;i<dim;i++)

   {

    _N______->_E___[i] = (___P____ *) calloc(dim,sizeof(___P____));          

AllErr_(_N______->_E___[i],"AllocErr by alloc _N______->_E___[]",exit(0);)

   }

   _N______->ENAB = NULL;                   

   _N______->Perm = (PTR(BYTE)) calloc(dim,sizeof(BYTE));                   

AllErr_(_N______->Perm,"AllocErr  by alloc _N______->Perm",exit(0);)

   return(_N______);

}



void  f___(void)

{

int i;

	if(_N______)  {

        for(i=0;i<_N______->Card;i++) {

			if(_N______->_N___[i]) {

				free(_N______->_N___[i]->ff);

		        free(_N______->_N___[i]);

			}

		}

 		free(_N______->_N___);

        for(i=0;i<_N______->Card;i++) 

			free(_N______->_E___[i]);

		free(_N______->_E___);

		free(_N______->Perm);

		free(_N______->ENAB);		

		free(_N______);

	}

    if(cet)     free(cet);

    if(XCET)    free(XCET);

    if(_n____t) {free(_n____t->Chain);free(_n____t);}

	if(Qx)      {free(Qx->Chain);free(Qx);}

	if(coX)     {free(coX->Chain);free(coX);}

	if(Q0)      {free(Q0->Chain);free(Q0);}



    if(Protfp)     fclose(Protfp);

    unlink(C_F___);

}



T_C___ * A___C__

(

  int    dim                

)

{

int i;

T_C___ *xet;

   xet  = (T_C___ *) calloc(dim,sizeof(T_C___)); 

   XCET = (T_C___ *) calloc(dim,sizeof(T_C___));

AllErr_(xet,"AllocErr by alloc xet",exit(0);)

AllErr_(XCET,"AllocErr by alloc XCET",exit(0);)

for(i=0;i<dim;i++) {xet[i].fxd = 0; XCET[i].fxd = 0;}        

   return(xet);

}



T_S__ * A___S__            

(

  int  card

)

{

T_S__ * M;

   M=(T_S__ *) calloc(1,sizeof(T_S__));

AllErr_(M,"AllocErr in A___S__",return(NULL);)

   M->Chain = (int *)calloc(card,sizeof(int));

AllErr_(M->Chain,"AllocErr in A___S__ by M->Chain",return(NULL);)

   M->Card = 0;

   return(M);

}





void  a_____e___

(

   int      is_fix,

   T_N__  * xnet

)

{

int i;

     if(is_fix)  xnet->B = FBANDL;                                 

     else  xnet->B = 2*(FBANDL+MAX_(MAXDIS,_S___)) + 5;



   xnet->ENAB = (PTR(short) *) calloc(xnet->Card,sizeof(PTR(short)));     

AllErr_(xnet->ENAB,"AllocErr  by alloc _N______->ENAB",exit(0);)

   for(i=0;i<xnet->Card;i++)

   {

    xnet->ENAB[i] = (short *) calloc(xnet->B+1,sizeof(short));         

AllErr_(xnet->ENAB[i],"AllocErr by alloc _N______->ENAB[]",exit(0);)

  }

   

}



void  LayerCard          

(

  int      Cmax           

)

{

int i;

 for(i=0;i<NETCARD;i++)

   LC(i) =  MIN_(Cmax,NC(i));    

}





int  In_SET

(

  int     e,

  T_S__ * S

)

{

int   i=0,a=0;

  while(!(a) && (i<S->Card)) {a = ((a||(S->Chain[i]==e)));i++;}

  return(i-sin_B(a));

}

void   SkipEl

(

  int          ix,

  T_S__      * xX

)

{

int     i=0;

if(ix == (xX->Card - 1)) {xX->Card--;return;}            

   i=ix;

   while(i < xX->Card - 1){xX->Chain[i]=xX->Chain[i+1];i++;}  

   xX->Card--;

}

void   Copy_SET                  

(

  T_S__ * Ax,

  T_S__ * Bx

)

{

  Ax->Card=0;

  while(Ax->Card<Bx->Card)

  {Ax->Chain[Ax->Card] = Bx->Chain[Ax->Card]; Ax->Card++;}

}

void   Diff_SET                     

(

  T_S__ * Ax,

  T_S__ * Bx

)

{

int i,ix;

   for(i=0;i<Bx->Card;i++)              

   {

     ix =  In_SET(Bx->Chain[i],Ax);

     if(ix<Ax->Card)  SkipEl(ix,Ax);

   }

}

void  IDform_Set

(

   int     scard,

   T_S__ * Bx

)

{

int i;

for(i=0;i<scard;i++) Bx->Chain[i] = i;

Bx->Card = scard;

}



int  g___n___(char *snxx, int ssx)

{

int i;

  for(i=0;i<_N______->Card;i++)

     if( (_N______->_N___[i]->TSS == ssx) && !strcmp(snxx,_N______->_N___[i]->S) )                  

     return(_N______->_N___[i]->Id);

   return(-1);

}



void   w_____c_______

(

  char      * fname,

  T_C___    * xN

)

{

int x,i,j=0;

FILE  * cefp;

  if((cefp =fopen(fname,"w")) == NULL){

	  printf("ERROR by open file %s",fname);;

      return;

  }



  while(j<CARD_(_N______))   

{

  fprintf(cefp,"%s %d %d %d %d ",_N______->_N___[j]->S,_N______->_N___[j]->TSS,_N______->_N___[j]->SS,_N______->_N___[j]->NC,_N______->_N___[j]->AxF);

  for(i=0;i<_N______->_N___[j]->AxF;i++) {

      x = _N______->_N___[j]->ff[i]+OSET;

	  fprintf(cefp,"%d ",x);  

  }

  fprintf(cefp,"\n");

  j++;



}

  fclose(cefp);

}



BOOL  is_fixed               

(

  int  nix,     

  int  fix      

)

{

int l;

if(!_N______->_N___[nix]->FxF) return(False);

for(l=0;l<_N______->_N___[nix]->FxF;l++) 

    if(_N______->_N___[nix]->ff[l] == fix) return(True);

return(False);

} 



void   w_____P_____

(

  T_C___  *xN

)

{

int i,j,k; 



fprintf(Protfp,"\nJPB-FA======================================================\n");



for(i=0;i<_N______->Card;i++) 

{ _N______->_N___[i]->AxF = _N______->_N___[i]->FxF; 

}



fprintf(Protfp,"\n============== %d::\n",1);                



for(i= -1;i<_N______->ub+1;i++)

{

k=0; fprintf(Protfp,"\nF:=%3d::> ",i); 

for(j=0;j<NOFC;j++)                 

{

	if(i==xN[j].Fn)          

	{

fprintf(Protfp,"%s.%d.%d| ", _N______->_N___[xN[j].Nix]->S,

                          _N______->_N___[xN[j].Nix]->TSS,

                          xN[j].Cid);

if(!is_fixed(xN[j].Nix,i))                                           

{

if(_N______->_N___[xN[j].Nix]->AxF < _N______->_N___[xN[j].Nix]->NC) {

_N______->_N___[xN[j].Nix]->ff[_N______->_N___[xN[j].Nix]->AxF] = i;     

_N______->_N___[xN[j].Nix]->AxF++;

}

}

     k++;

     if(!(k%10)) fprintf(Protfp,"\n          ");

	}

}

}

fprintf(Protfp,"\n---------------------------------------------------------------\n");

if(j<NOFC){

fprintf(Protfp,"!%4d RTs out: ",coX->Card);

for(j=0;j<coX->Card;j++)

{

fprintf(Protfp,"%s.%d.%d| ",_N______->_N___[NIX(coX->Chain[j])]->S,          

		                 _N______->_N___[NIX(coX->Chain[j])]->SS,             

						 CID(coX->Chain[j]));

if(j && !(j%6)) fprintf(Protfp,"\n              ");

}

}

fprintf(Protfp,"\nCard=%d\n",NOFC);

fprintf(Protfp,

"=========================================================================================\n");

}



int wpc___(

int pmod                  

)     

{

	if(!PlExist) return(0);

	w_____P_____(XCET);	                      



	if(pmod)  w_____c_______(PLFILE,XCET);

fflush(Protfp);

	return(1);

}



__inline Permis_net

(

  int      pos,

  int      dx,

  ___P____  ** E

)

{

int i,r=0;

   if(!(pos<0) && (PERM(pos) != 2)) 

	   PERM(pos) = 1;

   for(i=pos+1;i<CARD_(_N______);i++){

       _D___(E,pos,i,r);       

	   if(PERM(i) != 2) 

		   PERM(i) = cos_B(r<dx) * PERM(i);

  }

}

__inline void  UpdatePerm

(

  int      pos                            

)

{

int i;

   if(!(pos<0)) PERM(pos) = PERM(pos)*(PERM(pos) - 1);

   for(i=pos+1;i<CARD_(_N______);i++)

         PERM(i) = MAX_(1,PERM(i));

}

void     Invalid_set(T_S__ * Q0p)

{

int i;

   for(i=0;i<CARD_(Q0p);i++)

   {PERM__(i,Q0p) = 2; NotEmpty--;}

}





__inline int   CompCurLB

(

  int      di,

  T_S__  * xS

)

{

int i,C=0;

   for(i=0;i<CARD_(xS);i++)

     C += LC__(i,xS);

   C = 1 + (di*(C-1));

   return(C);

}



__inline int   CompCurLBs

(

  int      di,

  int      from,

  T_S__  * xS

)

{

int i,lx=0,C=0;

   lx = CompCurLB(di,xS);

   lx = lx + di - 1;

   for(i=from;i<CARD_(_S____);i++)

      C += LC_(i);

   C = 1 + (di*(C-1));

   C += lx;

   return(C);

}

__inline int  WeightOfSET

(

  T_S__   * xX

)

{

  return(CARD_(xX));

}

int    LBfilter          

(

  int      oi,

  int      ni,

  int      from,

  int    * olb,

  T_S__  * oQ,

  T_S__  * nQ

)

{

int nlb,xlb = *olb;

  nlb=CompCurLBs(ni,from,nQ);

  if(oi != ni) return( xlb < nlb );

  xlb=WeightOfSET(oQ);

  nlb=WeightOfSET(nQ) + (CARD_(_S____) - from);

  return( xlb < nlb );

}

__inline int    Admiss4Set

(

  int        prio,

  int        ix,

  int        dx,

  ___P____  ** E,

  T_S__     * Q

)

{

int i=0,r=0;

   while(i < CARD_(Q))

   {

      _P___(E,ix,E_(i,Q),r); if(r<prio)  return(False);

      _D___(E,ix,E_(i,Q),r); if(r<dx)  return(False);

	 i++;

   }

   return(True);

}



void  Extend_clqset                                                                    

(

  BOOL     * foundp,         

  int        priop,         

  int        ubp,           

  int      * rankp,          

  int      * xlbp,        

  int        card_xQ,     

  int        disp,        

  ___P____  ** Rel,

  T_S__    * xQ,

  T_S__    * Qx0 

)

{

int p,q=disp, nofmax=0, ylb = CompCurLB(disp,xQ);  

BYTE done=False, NoCh=False;

  p=card_xQ;

  while((p < CARD_(_S____) ) && (NoCh || 

	    (!*foundp && (ylb < (ubp+1)) && LBfilter(*rankp,disp,p,xlbp,Qx0,xQ)))) 

  {

    if( (PERM(p) != 1) || !(Admiss4Set(priop,p,q,Rel,xQ))) {p++;NoCh=True;continue;}

 	E_(CARD_(xQ)++,xQ) = p;

    Permis_net(p,q,Rel);

    Extend_clqset(foundp,priop,ubp,rankp,xlbp,p+1,disp,Rel,xQ,Qx0);

    done=True; NoCh=False;

    p++;

  }

  if(*foundp) return;

  if(!done)

  {

    *xlbp = CompCurLB(disp,xQ);                                

    if(_N______->LB0 < *xlbp)                                   

    {

       _N______->LB[disp-1] = *xlbp;                               

       Copy_SET(Qx0,xQ); *rankp = disp; _N______->LB0 = *xlbp;        

	   if(ubp < *xlbp) *foundp = 1;

    }

  }

  if(CARD_(xQ) > 0)

  {

   UpdatePerm(xQ->Chain[xQ->Card-1]);

   CARD_(xQ)--;

  }

}



void  get_Q

(

   int        prio,

   int        ub_p,        

   int      * rank,

   int        dis,

   ___P____  ** Rel,

   T_S__    * Qxp,

   T_S__    * Qx0

)

{

BOOL  *found= (BOOL*) calloc(1,sizeof(BOOL));

int qold= *rank, *xlb;

  *found=0;

  xlb = (int*) calloc(1,sizeof(int)); *xlb = 0;

  Extend_clqset(found,prio,ub_p,rank,xlb,CARD_(Qxp),dis,Rel,Qxp,Qx0);

  if(qold != *rank)

    _N______->LB[dis-1] = *xlb;  

  free(xlb);  free(found);

}

int get_MLB       

(

   int        Minprio,

   int        Ncar,                      

   int        Mindist,

   int        Ldis,

   ___P____  ** rel,

   T_S__    * Qxp,

   T_S__    * Qx0

)

{

int ra=1, i;

   CARD_(Qxp)  = 0; CARD_(Qx0)  = 0; _N______->LB0 = 0;

   if((Mindist<2)&&(0<Ldis))                          

   {

   _N______->LB[0]=0;

   UpdatePerm(Qxp->Card-1);                      

   get_Q(Minprio,Ncar,&ra,1,rel,Qxp,Qx0);

   }

   if((Mindist<3)&&(1<Ldis))

   {

   CARD_(Qx)  = 0;   

   _N______->LB[1]=0;

   UpdatePerm(Qxp->Card-1);

   get_Q(Minprio,Ncar,&ra,2,rel,Qxp,Qx0);            

   }

   if((Mindist<4)&&(2<Ldis))

   {

   CARD_(Qxp)  = 0;   

   _N______->LB[2]=0;

   UpdatePerm(Qxp->Card-1);

   get_Q(Minprio,Ncar,&ra,3,rel,Qxp,Qx0);

   }

  

  ClNo++;

fprintf(Protfp,"The %d maximal clique has Lower Bound = %2d and %d following sectors:\n{",

		ClNo,_N______->LB0,CARD_(Qx0));

for(i=0;i<CARD_(Qx0);i++)

{_N__E__(i,Qx0)->Clno = ClNo;

fprintf(Protfp,"%d:%s.%dx%d;",

	_N__E__(i,Qx0)->Clno,_N__E__(i,Qx0)->S,_N__E__(i,Qx0)->TSS,LC__(i,Qx0));

}

fprintf(Protfp,"}\n");



   return(_N______->LB0);

}



void  perm_net(void)       

{

int i;

  for(i=0;i<NETCARD;i++)  PERM(i) = 1;

}



void  ShiftSpaceLeft_LC(void)                        

{int i=0,j;        

for(j=0;j<NETCARD;j++) if(LC(j)) {_S_____(i) = j;i++;}

 CARD_(_S____) = i;

}



void  write_cet(char * bfname)

{

int nofc=NOFC;

FILE  * bfp;

  if((bfp = fopen(bfname,"wb")) == NULL){

	  printf("ERROR by open filr %s",bfname);;

      return;

  }



   fwrite(&nofc,sizeof(int),1,bfp); 

   fwrite(cet,sizeof(T_C___),NOFC,bfp);

   fclose(bfp);

}

void  Sect_cet(T_S__  *xQ0)                         

{

int i=0,j=0,k=0, l=0;

T_S__ * aux;



   aux = A___S__(CARD_(xQ0));CARD_(aux) = CARD_(xQ0);

   for(i=0;i<CARD_(aux);i++)

   {

      E_(i,aux) = LC__(i,xQ0);

	  k += LC__(i,xQ0);

   }

   j += NOFC; NOFC += k;              

   while(j<NOFC){

   for(i=0;i<CARD_(aux);i++)

   {

     k = _N______->_N___[_S_____(xQ0->Chain[i])]->FxF;                          

	 if(E_(i,aux))

     {

       cet[j].Nix = _N___IX__(i,xQ0); 

      cet[j].Cid = LC__(i,xQ0) - E_(i,aux); 

	   if(l<k) {cet[j].Fn = _N______->_N___[_S_____(xQ0->Chain[i])]->ff[l]; cet[j].fxd = 1;} 

	   else    {cet[j].Fn = -1; cet[j].fxd = 0;}

       E_(i,aux)--;

       j++;

     }

   }

   l++;

   }

   Free_SET(aux);

}



void   partition_net                           

(

    int    Min_prio,        

    int    Noofcar,       

    int    Min_dis,        

    int    Lim_dis,

    int    Max_cell_demand, 

	char * filna

)

{

int celonf=0;

   NOFC=0;

   perm_net();         



   LayerCard(Max_cell_demand);   

   ShiftSpaceLeft_LC();

   NotEmpty = CARD_(_S____);



   while(NotEmpty)	  {

       get_MLB(Min_prio,Noofcar,Min_dis,Lim_dis,_N______->_E___,Qx,Q0);  

       Invalid_set(Q0);                       

       Sect_cet(Q0);

   }

   write_cet(filna);

}



void  NatOrd

(

  int     c,

  T_S__  *S

)

{int i=0; S->Card=c; while(i<S->Card) {S->Chain[i]=i;i++;}}







void  OrderNet

(void)

{

int  i,j;

_N__E_T  * x0;

  for(i=0;i<_N______->Card;i++)      

  {

  for(j=i+1;j<_N______->Card;j++)

  {

   if(NC(i) < NC(j)){x0 = _N__E(j); _N__E(j) = _N__E(i); _N__E(i) = x0;}

  }

  ID(i) = i;

  }

}



__inline int NDIS(int i,int j)

{int p=0,a=0;  

_P__(_N______->_E___,NIX(i),NIX(j),p);

if( p<PLOW ) return(0);

_D__(_N______->_E___,NIX(i),NIX(j),a);

return(a);

}





void  ShiftB

(

  T_C___  * xN

)

{

int i;

  for(i=0;i<NOFC;i++)  if(xN[i].Fn > 0) xN[i].Fn -= _N______->lb;

  _N______->ub -= _N______->lb;

  _N______->lb  = 0;

}



__inline void  enab_net(T_N__ *xN,int ix)

{

int k; 

	   for(k=1;k<xN->B+1;k++) 

		   if(!xN->ENAB[NIX(ix)][k]) xN->ENAB[NIX(ix)][k]=1; 

}

__inline  int GFBNCC  

(

int a,

int b,

int ix,

int lf

)

{int i=0, d1=0, d2=0, fx1= -1, fx2= -1, done1=0,done2=0;

     FFLX = -1;

     if(a == lf) {

    	 for(i= lf;(i < b + 1)&&!done1; i++){

    	  switch(_N______->ENAB[NIX(ix)][i+1])

		  {

           case -1:  d1++;break;

           case  1:  fx1 = i; done1=1;break;

           default:  d1++;break;

		  }

		 }

         if(!done1) return(-1);

		 FFLX = fx1;

         return(d1);

	 }

     if(b == lf) {

    	 for(i= lf;(a-1 < i)&&!done1; i--){

    	  switch(_N______->ENAB[NIX(ix)][i+1])

		  {

           case -1:  d1++;break;

           case  1:  fx1 = i; done1=1;break;

           default:  d1++;break;

		  }

		 }

         if(!done1) return(-1);

         FFLX = fx1;

         return(d1);

	 }

	 for(i= lf;(i != (1-DD)*a + DD*b + 2*DD - 1)&&!done1; i = i+ 2*DD - 1){

      switch(_N______->ENAB[NIX(ix)][i+1])

      {

        case -1:  d1++;break;

        case  1:  fx1 = i; done1=1;break;

        default:  d1++;break;

      }

	 }

	 if(!done1) d1= b-a+1;

     else 

		 if(!d1) { FFLX = fx1;return(0);}

     DD = cos_B(DD);

     done2 = 0;

     for(i= lf;(i != (1-DD)*a + DD*b + 2*DD - 1)&& !done2; i = i+ 2*DD - 1){

   	  switch(_N______->ENAB[NIX(ix)][i+1])

      {

        case -1:  d2++;break;

        case  1:  fx2 = i; done2=1;break;

        default:  d2++;break;

      }

	 }   

     DD = cos_B(DD);

	 if(!done1&&!done2) return(-1);

	 if(!done2) d2= b-a+1;

     else 

		 if(!d2) {FFLX = fx2;return(0);}

     if(d1 < d2){

      FFLX = fx1;

      return(d1);

	 }

     FFLX = fx2;

     return(d2);

}



__inline  void GFB  

(

int a,

int b,

int ix

)

{int i; 

   FROL.frno = -1; FROL.frdis = 0;

   for(i= DD*a + (1-DD)*b;i != (1-DD)*a + DD*b + 2*DD - 1; i = i+ 2*DD - 1)

   switch(_N______->ENAB[NIX(ix)][i+1])

   {

     case -1:  FROL.frdis++;break;

     case  1:  FROL.frno = i; DD = cos_B(DD);return;break;

			   break;

     default:  break;

   }

}



__inline  void GFL

(int a, 

 int ix

)

{int i;

FROL.frno = _N______->B; FROL.frdis = 0;

for(i=a;MAX_(0,_N______->ub-FBANDL+1)<=i;i--)   

  switch(_N______->ENAB[NIX(ix)][i+1])

  {

     case -1:  FROL.frdis++;break;

     case  1:  FROL.frno = i;return;break;

     default:  break;

  }

}

__inline void GFU

(int a,

 int ix

)

{int i; 

FROR.frno = -1; FROR.frdis = 0;

for(i=a;i<MIN_(_N______->B,_N______->lb+FBANDL);i++)

  switch(_N______->ENAB[NIX(ix)][i+1])

  {

     case -1:  FROR.frdis++;break;

     case  1:  FROR.frno = i;return;break;

     default:  break;

  }

}



__inline int l_D(int ix,int n)

{

int a = NDIS(ix,n); 

    a = FREQ(n) - a;                

return(a);

}

__inline int r_D(int ix,int n)

{

int a = NDIS(ix,n);  

  a = FREQ(n) + a; 

return(a);

}

__inline void GLEGO(int a,int b, int ix) 

{int i;

  for(i=a+1;i<b;i++)

  {

   if((i<0)||(_N______->B - 1 < i)) continue;

   _N______->ENAB[NIX(ix)][i+1] = 

   _N______->ENAB[NIX(ix)][i+1] * (1 - _N______->ENAB[NIX(ix)][i+1]) / 2;

  if(_N______->B > i+NOFF) 

      _N______->ENAB[NIX(ix)][i+1+NOFF] = 

      _N______->ENAB[NIX(ix)][i+1+NOFF] * (1 - _N______->ENAB[NIX(ix)][i+1+NOFF]) / 2;

  if(i-NOFF > 0)  

      _N______->ENAB[NIX(ix)][i+1-NOFF] = 

      _N______->ENAB[NIX(ix)][i+1-NOFF] * (1 - _N______->ENAB[NIX(ix)][i+1-NOFF]) / 2;

  }

}

__inline void LEGO(int a,int b, int ix) 

{int i;

  for(i=a+1;i<b;i++)

  {

   if((i<0)||(_N______->B - 1 < i)) continue;

      _N______->ENAB[NIX(ix)][i+1] = 

      _N______->ENAB[NIX(ix)][i+1] * (1 - _N______->ENAB[NIX(ix)][i+1]) / 2;

  }

}

__inline void  VV

(

   int     ix,

   T_S__ * xX

)

{int i=0,l,r, esac=GROUPING;

  while(i<xX->Card)

  {

    l = l_D(ix,xX->Chain[i]);

    r = r_D(ix,xX->Chain[i]);

    if(esac && ((l < _N______->lb)||(r > _N______->ub)))

		GLEGO(l,r,ix); 

	else

		LEGO(l,r,ix); 

	i++;

  }

}

int   RhoCC

(

   int      ix,

   T_S__  * xX

)

{int a,b,x;

   enab_net(_N______,ix);

   VV(ix,xX);

   GFB(_N______->lb,_N______->ub,ix); if(FROL.frno >= 0) return(0);

   GFL(_N______->lb,ix); a=FROL.frno;

   GFU(_N______->ub,ix); b=FROR.frno;

   if(a != _N______->B){

	   if(b != -1){

        x = MIN_((_N______->lb - a - FROL.frdis),(b - _N______->ub - FROR.frdis)); 

        if((_N______->lb - a - FROL.frdis) < (b - _N______->ub - FROR.frdis))

          b = _N______->ub + 1 - a;

        else  

	      b = b + 1 - _N______->lb;

        if(b>FBANDL)   

           return(0);

        return(x);

	   }else {

        x = _N______->lb - a - FROL.frdis; 

        b = _N______->ub + 1 - a;

        if(b>FBANDL)   

           return(0);

        return(x);

	   }

   }else{

	   if(b != -1){

        x = b - _N______->ub - FROR.frdis; 

        b = b + 1 - _N______->lb;

        if(b>FBANDL)  

           return(0);

        return(x);

	   }else {

         return(0);

	   }

   }

}

int   RhoLL    

(

   int      ix,

   T_S__  * xX

)

{int x;

   FREQ(ix) = -1;

   x = FREQ(xX->Chain[xX->Card-1]);

   if(x < 0) return(-1);

   enab_net(_N______,ix);

   VV(ix,xX);

   x = GFBNCC(_N______->lb,_N______->ub,ix,x); 

   return(x);

}



int  RRRX

(

   int      nix,

   T_S__  * xX

)

{  enab_net(_N______,nix);

   FREQ(nix) = -1;

   VV(nix,xX);

   GFB(_N______->lb,_N______->ub,nix); if(FROL.frno < 0) return(0);

   FREQ(nix) = FROL.frno;    XC(NIX(nix))--;    

   return(1);

}

void  RR  

(

   int      nix,

   T_S__  * xX

)

{

   enab_net(_N______,nix);

   FREQ(nix) = -1;

   VV(nix,xX);

   GFL(_N______->lb,nix);

   GFU(_N______->ub,nix);

   if(FROL.frno < _N______->B)

   {

    if(FROR.frno > -1)

    {

     if((_N______->lb - FROL.frno - FROL.frdis) <  (FROR.frno - _N______->ub - FROR.frdis))

     {

       FREQ(nix) = FROL.frno;            

	   _N______->lb = MIN_(_N______->lb,FROL.frno); XC(NIX(nix))--;

FBAND = True && ((_N______->ub - FROL.frno + 1) < FBANDL); 

     return;

     }

     FREQ(nix) = FROR.frno;   

     _N______->ub = MAX_(_N______->ub,FROR.frno); XC(NIX(nix))--;

FBAND = True && ((FROR.frno - _N______->lb) < FBANDL);   

   return;

    }

    else

    {

     FREQ(nix) = FROL.frno;   

     _N______->lb = MIN_(_N______->lb,FROL.frno); XC(NIX(nix))--;

FBAND = True && ((_N______->ub - FROL.frno + 1) < FBANDL);    

   return;

    }

   }

   FREQ(nix) = FROR.frno;   

   _N______->ub = MAX_(_N______->ub,FROR.frno);  XC(NIX(nix))--;

FBAND = True && ((FROR.frno - _N______->lb + 1) < FBANDL);  //??JPB zu scharff!!! 

}







void  init_cr             

(

  T_N__    *xN,

  ___P____ **CRX

)

{

int nix = -1, njx = -1;

  for(nix=0;nix<xN->Card;nix++)

    for(njx=nix;njx<_N______->Card;njx++)

	{strcpy(CRX[nix][njx],CRINITVAL);strcpy(CRX[njx][nix],CRINITVAL);	}                     

}

void  init_graph

(

  T_N__    *xN,

  ___P____ **CRX

)

{

int nix = -1, k;

  ALLN = 0;ClNo=0;

  for(nix=0;nix<xN->Card;nix++)

  {

    xN->Perm[nix] = 1;

	xN->_N___[nix]->COI = 0;  xN->_N___[nix]->ADI = 0; xN->_N___[nix]->Clno = 0;

  }

  init_cr(xN,CRX);



  for(nix=0;nix<xN->Card;nix++) {

	  xN->ENAB[nix][0] = xN->B;                        

	  for(k=0;k<xN->B;k++) xN->ENAB[nix][k+1] = 0;

  }

}



void  disab_carriers(void)        

{

int cid,f;

 for(cid=0;cid<_N______->Card;cid++)  

	 for(f=0;f<NOC;f++)                             

	    _N______->ENAB[cid][FC____[f]-OSET+1] = 1;  

 for(cid=0;cid<_N______->Card;cid++)                 

	 for(f=0;f<_N______->B;f++) 

	    _N______->ENAB[cid][f+1] -= 1;

}



BYTE  read_net_new(char *fnet)                               

{

int   i, card=0, CNo = -9999;

char  *lx, *xx, aux[MaxNameL+1];

FILE *fp ; 

  FDemand = 0;

  lx = (char*) calloc(MaxLineL+1,sizeof(char));

  if((fp =fopen(fnet,"r")) == NULL){

	  printf("ERROR by open filr %s",fnet);;

      return(False);

  }

 

 while (fgets(lx,MaxLineL,fp) != NULL)                    

  {

   if(lx[0] == '!') continue;

   card++;          

  }

  rewind(fp);           



   _N______  = A___N__(card,XSC);  

   _N______->Card = 0;

   _N______->FXF  = 0;





  while (fgets(lx,MaxLineL,fp) != NULL)                      

  {

   if(lx[0] == '!') continue;



   jpb_strAsubst (lx, "  "," "); jpb_strAsubst (lx, "\t\t","\t");





   _N__E(_N______->Card)->S[0] = '\0';_N__E(_N______->Card)->SCL = '0';

   sscanf(lx,"%s",_N__E(_N______->Card)->S);      

   xx = lx;

   xx = xx + strlen(_N__E(_N______->Card)->S)+1;   

   sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;

   sscanf(aux,"%d",&_N__E(_N______->Card)->TSS);    

   sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;

   sscanf(aux,"%d",&_N__E(_N______->Card)->SS);   

   sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;

   sscanf(aux,"%d",&NC(_N______->Card));  

   _N__E(_N______->Card)->FxF=0;   _N__E(_N______->Card)->AxF=0;



   if(!(NC(_N______->Card) > 0)) {

fprintf(Protfp,"!!!ERROR reading file %s in line: wrong RT-demand > %s\n",fnet,lx);	   

	   continue;   

   }

   FDemand += NC(_N______->Card);

   if(1 == sscanf(xx,"%s",aux)) {

      xx = xx + strlen(aux)+1;

      if(1 == sscanf(aux,"%d",&_N__E(_N______->Card)->FxF)) {    

		  if(_N__E(_N______->Card)->FxF) {                

          _N______->FXF += _N__E(_N______->Card)->FxF;   

	      for(i=0;i<_N__E(_N______->Card)->FxF;i++)	     

		  {

	       if(1 != sscanf(xx,"%s",aux)) {

fprintf(Protfp,"!!!ERROR reading file %s in line %s\n",fnet,lx);

           return(0);      

		   }

           xx = xx + strlen(aux)+1;

           sscanf(aux,"%d",&_N__E(_N______->Card)->ff[i]);      

           _N__E(_N______->Card)->ff[i] -= OSET;         

		  }

		}

	  }

   }

   ID(_N______->Card) = _N______->Card;  



   _N__E(_N______->Card)->SCL = (char) ((int)'A' + _N__E(_N______->Card)->TSS - 1);

   XC(_N______->Card) = NC(_N______->Card); 

   _N______->Card++;      

  }

  free(lx);

  fclose(fp);  

  return(True);

}



BYTE  read_forbidden(char *fnet)

{

int   i, cno=0, nof=0, cid= -1, forb = -1;

char  *lx, *xx, aux[MaxNameL+1], site[MaxNameL+1];

FILE *fp;

  

  lx = (char*) calloc(MaxLineL+1,sizeof(char));

  if((fp =fopen(fnet,"r")) == NULL){

	  printf("ERROR by open filr %s",fnet);;

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

	 {printf("!!ERROR in file %s: cell %s %d not found\n",fnet,site,cno);

          return(False);

	 }  



   sscanf(xx,"%s",aux);xx = xx + strlen(aux)+1;

   sscanf(aux,"%d",&nof); if(!nof) continue;              

   _N______->ENAB[cid][0] -= nof;



	if(_N______->ENAB[cid][0] < _N______->_N___[cid]->NC){

printf(

"!!!ERROR number of frequencies allowed=%d < %d=required for the cell %s %d\n",

	   _N______->ENAB[cid][0],_N______->_N___[cid]->NC,site,cno);

   free(lx);

   fclose(fp);

          return(0);      

	}	

   

    for(i=0;i<nof;i++) {

      if(1 != sscanf(xx,"%s",aux)) {

fprintf(Protfp,"!!!ERROR reading file %s in line %s\n",fnet,lx);

  free(lx);

  fclose(fp);

           return(0);      

	  }

      xx = xx + strlen(aux)+1;

      sscanf(aux,"%d",&forb);

	  if(!(forb-OSET<0)) _N______->ENAB[cid][forb-OSET+1] = -1;

   }

 }

  free(lx);

  fclose(fp);

  return(True);

}



BYTE  read_exceptions(char *fnet, int pri, ___P____  ** CRX)  

{

int   cno1=0,cno2=0, cid1= -1,cid2= -1, dexc = 0;

char  *lx, site1[MaxNameL+1],site2[MaxNameL+1];

FILE *fp;

  

  lx = (char*) calloc(MaxNameL+1,sizeof(char));

  if((fp =fopen(fnet,"r")) == NULL){

	  fprintf(Protfp,"ERROR by open filr %s\n",fnet);;

	  printf("ERROR by open filr %s",fnet);;

      return(False);

  }

  while (fgets(lx,MaxLineL,fp) != NULL)

  {

   if(lx[0] == '!') continue;

   jpb_strAsubst (lx, "  "," "); jpb_strAsubst (lx, "\t\t","\t");

   sscanf(lx,"%s %d %s %d %d",site1,&cno1,site2,&cno2,&dexc);  

   cid1 = g___n___(site1, cno1);   cid2 = g___n___(site2, cno2);

   if((cid1<0)||(cid2<0)) {

fprintf(Protfp,"!!ERROR in file %s: on of cells %s %d, %s %d not found\n",

		fnet,site1,cno1,site2,cno2);

printf("!!ERROR in file %s: on of cells %s %d, %s %d not found",

		fnet,site1,cno1,site2,cno2);

        return(False);

   }  

   if(!(dexc<0)) {      

    	P__(CRX,cid1,cid2,pri);  P__(CRX,cid2,cid1,pri);

    	D__(CRX,cid1,cid2,dexc); D__(CRX,cid2,cid1,dexc);

   }

  }

  free(lx);

  fclose(fp);

  return(True);

}

void  son_closure_it

(

   int        p_prio,

   int        n_prio,

   int        dis,

   int        tresh,

   int        itr,

   ___P____  ** rel

)

{

int od=0,i,j,k, adj1=100,adj2=100, co1=0, co2=0;

long xx=0;



 for(i=0;i<_N______->Card;i++) 

 {   

  for(j=0;j<_N______->Card;j++)  

	 {

	  if(j != i) {

	   _P__(rel,i,j,adj1); 

	   

      if(adj1 < p_prio)  continue;                  

       for(k=0;k<_N______->Card;k++)

	   {

	      if(!((k != i)&&(k != j))) continue;                                    



	      _P__(rel,i,k,adj1);    

          if(adj1 < p_prio)  continue;                                       

          _P__(rel,j,k,adj1); _P__(rel,k,j,adj2); adj1 = MAX_(adj1,adj2);   

		  if(!(adj1 < n_prio)) continue;                                   

		  _C_(rel,j,k,co1);  _C_(rel,k,j,co2); co1 =MAX_(co1,co2);

		  if(!(tresh < co1)) continue;

          co1 = MIN_(PPar7[____NS].pcmax,co1+itr);                 xx++;

          C_(rel,j,k,co1); C_(rel,k,j,co1); 

          D__(rel,j,k,dis); D__(rel,k,j,dis);

          P__(rel,j,k,n_prio); P__(rel,k,j,n_prio); 

          if(co1) PPar7[p_prio].pcoit1++;

	   }

	  }

	 }

 }

fprintf(Protfp,"ADD so-neighbours  %ld\n",xx);

}



void  set_cr_CC_it(int o_pri,int pri, int dis, int ctre, int atre, ___P____  ** CRX)

{

int d1=0, nix = -1;

  for(nix=0;nix<_N______->Card;nix++)

  { 

	 _P__(CRX,nix,nix,d1);

     if(!(d1 < o_pri)) continue;

     P__(CRX,nix,nix,pri);

     D__(CRX,nix,nix,dis);

     C_(CRX,nix,nix,ctre);                      

	 if(ctre) PPar7[____C].pcoit1++;

     if(atre) {

	   _A__(CRX,nix,nix,d1); 

       d1 += atre; d1 = MIN_(___A__,d1);

       A__(CRX,nix,nix,d1);

	 }

  }

}



void  set_cr_SCC_it(int o_pri, int pri, int dis, int ctre, int atre, ___P____  ** CRX)         

{

int d1=0,d2=0, nix = -1, njx = -1;

  ALLN = 0;

  for(nix=0;nix<_N______->Card;nix++)for(njx=nix+1;njx<_N______->Card;njx++)

  { 

    _P__(CRX,nix,njx,d1); 

    if(!(d1 < o_pri)) continue;

    if( !strcmp(_N______->_N___[nix]->S,_N______->_N___[njx]->S) ){

     P__(CRX,nix,njx,pri); P__(CRX,njx,nix,pri);

     D__(CRX,nix,njx,dis); D__(CRX,njx,nix,dis); 

     _C_(CRX,nix,njx,d1); _C_(CRX,njx,nix,d2); 

	 d1 = MAX_(d1,d2);    

	 d1 += ctre;  d1 = MIN_(___C__,d1);                         

     C_(CRX,nix,njx,d1); C_(CRX,njx,nix,d1); 

	 PPar7[____S].pcoit1++;



     _A__(CRX,nix,njx,d1); _A__(CRX,njx,nix,d2); 

	 d1 = (d1+d2)/2;d1 += atre; d1 = MIN_(___A__,d1);

     A__(CRX,nix,njx,d1); A__(CRX,njx,nix,d1); 

	 ALLN++;                                       

	}

  }

}







BYTE  read_neighbors(char *fname,int p_pri, int mind, int cadd, int aadd, ___P____  ** CRX)      

{

char  lx[MaxLineL+1], nc[5], six[MaxNameL+1], snx[MaxNameL+1];

FILE *fp; 

int  xx=0,pri=0, sec=0, sno=0, nix = -1, njx = -1, d1,d2;



    six[0]='\0';

		

  if((fp =fopen(fname,"r")) == NULL){

	  printf("ERROR by open filr %s",fname);;

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

fprintf(Protfp,"!!ERROR in file %s: cell %s %d not found\n",fname,snx,sno);

printf("!!ERROR in file %s: cell %s %d not found\n",fname,snx,sno);

          return(False);

	 }

	 if(nix != njx){		                                   

		_P__(CRX,njx,nix,pri);      

        if(!(pri < p_pri)) continue;



			 P__(CRX,nix,njx,p_pri); P__(CRX,njx,nix,p_pri);

        	 D__(CRX,nix,njx,mind); D__(CRX,njx,nix,mind);

        	 _C_(CRX,nix,njx,d1); _C_(CRX,njx,nix,d2);  d1 = MAX_(d1,d2);

             d1 += cadd;  d1 = MIN_(d1,PPar7[____2N].pcmax);              

        	 C_(CRX,nix,njx,d1); C_(CRX,njx,nix,d1);  

	         if(d1) PPar7[____2N].pcoit1++;

             ALLN++;

     }else

	 {

fprintf(Protfp,"!!ERROR: reflexive neighborhood sector: %s.%d.Check the neighbour file.\n",six,sno);

	 }

   } 

   else

   {

    if(nc[0] != 'C') {

fprintf(Protfp,"!!ERROR in file %s\n",fname);

printf("!!ERROR in file %s\n",fname);

       return(False);

	 }

     else

 	 {

		 if( !strcmp(six,snx) && (sec==sno)) continue; 

         strcpy(six,snx);  sec = sno;

		 nix = -1; nix = g___n___(six,sec);

		 if(nix<0) 

		 {

fprintf(Protfp,"!!ERROR in file %s: cell %s %d not found\n",fname,six,sec);

printf("!!ERROR in file %s: cell %s %d not found\n",fname,six,sec);

             return(False);

		 }

	 }

   }

  }

  fclose(fp);

fprintf(Protfp,"Set neigh Prio=%d Dis=%d >>> %d \n",p_pri,mind,ALLN);

  return(True);

}





BYTE  read_it(int o_pri, int p_pri, char *fname, ___P____  ** CRX, int  traff_m)       

{

char  lx[MaxLineL+1], subci[MaxNameL+1], subcx[MaxNameL+1], s1[8], s2[MaxNameL+1];

FILE *fp;

int ssno,isno=0, nix = -1, njx = -1, lk=0, noofi, icot, iadt,xt=0;

double coa,cot,ada,adt,A = 0.0, T = 0.0;

  if((fp =fopen(fname,"r")) == NULL){

	  printf("ERROR by open filr %s",fname);;

      return(False);

  }



  while (fgets(lx,300,fp) != NULL)

  {

	  if((lx[0] != 'S') && (lx[0] != 'I'))  continue;

	  if( lx[0] != 'S') {

sscanf(lx,"%s %s %d %lf %lf %lf %lf %s",s1,s2,&lk,&coa,&cot,&ada,&adt,subci);

	      isno = ( (int)subci[strlen(subci)-1] - (int)'A' ) + 1;

	      subci[strlen(subci)-1] = '\0';

          nix = -1; nix = g___n___(subci,isno);

		  if(nix<0) {

fprintf(Protfp,"WARNING: Invalid INT %s %d in the line %s in file %s\n",

		              subci,isno,lx,fname);

			  continue;

		  }

          _P__(CRX,njx,nix,icot); _P__(CRX,nix,njx,iadt);

		  lk = MAX_(icot,iadt);



          if(!(lk < o_pri)) continue;                     

          if(!traff_m) {cot = coa; adt = ada;}

          if(T > 0.0) {

              icot=0;iadt=0;           

			  cot = cot/T;

			      icot = MIN_(___C__,MAX_(0,(int) ceil(cot * ((double)IMAX) )));        

				  if(adt>0.0) {

				  iadt = MIN_(___A__, MAX_(0,(int) ceil(100.0  * (adt/T)*0.32)));        

				  }

		  } else {

		      icot = 0; 

    		  iadt = 0; 

		  }	  

	      C_(CRX,njx,nix,icot);

    	  A__(CRX,njx,nix,iadt);

          if(lk < o_pri)

		  {P__(CRX,njx,nix,p_pri); P__(CRX,nix,njx,p_pri);}

		  _N______->_N___[njx]->COI += icot;		  _N______->_N___[njx]->ADI += iadt;

	  }

	  else {



         sscanf(lx,"%s %s %d %lf %lf %d %s",s1,s2,&lk,&A,&T,&noofi,subcx);

           if(!traff_m)  T = A;

           if(T > 0.0) {

	        ssno = ( (int)subcx[strlen(subcx)-1] - (int)'A' ) + 1;

	        subcx[strlen(subcx)-1] = '\0';

            njx = -1; njx = g___n___(subcx,ssno);

			if(njx<0) {

fprintf(Protfp,"ERROR: Invalid SUBCELL %s %d in the file %s in line:\n",subcx,ssno,fname);

fprintf(Protfp,"       %s\n",lx);

             fclose(fp);

             return(False);

			}

		   }

           else {

fprintf(Protfp,"ERROR: AREA/TRAFFIC value %lf not valid in the file %s in line:\n",T,fname);

fprintf(Protfp,"       %s\n",lx);

printf("ERROR: AREA/TRAFFIC value %lf not valid in the file %s in line:\n",T,fname);

printf("       %s\n",lx);

            fclose(fp);            return(False);

		   }       

	  }

  }

  fclose(fp);

  return(True);

}





void  coit_sym(___P____  ** CRX)                      

{

int d1=0,d2=0, nix = -1, njx = -1;

  for(nix=0;nix<_N______->Card;nix++)for(njx=nix+1;njx<_N______->Card;njx++)

  { d1=0; d2=0;

    _C_(CRX,nix,njx,d1);    

	_C_(CRX,njx,nix,d2);

    if(d1 < d2) {C_(CRX,nix,njx,d2); continue;}  

	if(d2 < d1)  C_(CRX,njx,nix,d1);

  }

}



BOOL read_IT

(

 int        o_pri,

 int        p_pri,

 char     * itfile,

 ___P____  ** rel,

 int        trafm

)

{

   if(!read_it(o_pri,p_pri,itfile, rel, trafm)) return(False);

   coit_sym(rel);

   return(True);

}



void   FA_fixed

(

   T_C___    * cetx,

   T_S__     * Qxx,

   T_S__     * cQ

)

{

int i=0, j=0;   

    while((i<_N______->FXF)&&(j<NOFC)){

		if(cetx[cQ->Chain[j]].fxd) {

			Qxx->Chain[Qxx->Card++]=cQ->Chain[j];

            SkipEl(j,cQ);

			i++;

		}else j++;

	}

}

	 





void  c_coit1

(

  ___P____  ** rel 

)

{

int i,j,a1,a2;

	for(i=0;i<CARD_(_N______);i++)for(j=i;j<CARD_(_N______);j++){

    	_C_(rel,i,j,a1); _C_(rel,j,i,a2); a1 = MAX_(a1,a2);   

        if(a1) CoIT1++;

	}

	MAX_SET = 1 + CoIT1 / 100; 

    MIN_SET = 1 + (CoIT1 / (100*(1+QUALITY))); 

fprintf(Protfp,"CoIT1 = %d, MAX_SET = %d, MIN_SET = %d\n",CoIT1,MAX_SET,MIN_SET);

}



int  set_t_it

(

  int        ncl,

  int        pri,

  int        tresh,

  ___P____  ** rel 

)

{

int x,od=0,i,j, adj1=100,adj2=100, amin=tresh, card=0;;

  for(i=0;(i<CARD_(_N______))&&(card<ncl);i++){

	  for(j=i;(j<CARD_(_N______))&&(card<ncl);j++){

       _P__(rel,i,j,adj1);	

       if(adj1 != pri) continue;

   	   _C_(rel,i,j,adj1);   _C_(rel,j,i,adj2); x = MIN_(adj1,adj2);   

	   if(amin == x) {

           P__(rel,i,j,____T);    P__(rel,j,i,____T);  	  

           card++;

	   }

	  }

  }

  return(card);

}





int   set_t_ex

(

  int        ncl,

  int        pri,

  int        odis,

  int        ndis,

  ___P____  ** rel 

)

{

int x,od=0,i,j, adj1=100,adj2=100, done=0, card=0;;

  for(i=0;(i<CARD_(_N______))&&(card<ncl);i++){

	  for(j=i+1;(j<CARD_(_N______))&&(card<ncl);j++){

       _P__(rel,i,j,adj1);	 

       if(adj1 != pri) continue;

       _D__(rel,i,j,od);  _D__(rel,j,i,x);    od=MIN_(od,x);

       if(od != odis) continue;	

       D__(rel,i,j,ndis);     D__(rel,j,i,ndis);  	  

       P__(rel,i,j,____T);    P__(rel,j,i,____T);  	  

       done=1;card++;

	  }

  }

  return(card);

}

void   set_ex

(

  int        ncl,

  int        nprio,

  ___P____  ** rel 

)

{

int i,j, adj1=100,adj2=100, done=0, card=0;;

  for(i=0;(i<CARD_(_N______))&&(card<ncl);i++)

  {   for(j=i;(j<CARD_(_N______))&&(card<ncl);j++)

	  {

       _P__(rel,i,j,adj1);	

       if(adj1 != ____T) continue; 

       P__(rel,i,j,nprio);  P__(rel,j,i,nprio);  	  

       card++;   CoIT1Done++;

	  }

  }

}



int   extract_pair

(

  int        ncl,

  int        nprio,

  int        dis,

  ___P____  ** rel 

)

{

int i,j, adj1=100,card=0;;

  for(i=0;(i<CARD_(_N______))&&(card<ncl);i++)

  {   for(j=i;(j<CARD_(_N______))&&(card<ncl);j++)

	  {

       _P__(rel,i,j,adj1);

       if(adj1 != ____T) continue;

       D__(rel,i,j,dis);   D__(rel,j,i,dis);  	  

	   P__(rel,i,j,nprio);  P__(rel,j,i,nprio);  	  

fprintf(Protfp,"!!EXTRACT pair %d, %d = %s von %d\n",i,j,_N______->_N___[i]->S,CARD_(_N______));

       card++;

	  }

  }

  return(card);

}



int   reset_ex_back

(

  int        ncl,

  int        oprio,

  int        dis,

  ___P____  ** rel 

)

{

int od=0,i,j, adj1=100,adj2=100, card=0;;

  for(i=0;(i<CARD_(_N______))&&(card<ncl);i++)

  {   for(j=i+1;(j<CARD_(_N______))&&(card<ncl);j++)

	  {

       _P__(rel,i,j,adj1);

       if(adj1 != ____T) continue;

       P__(rel,i,j,oprio);  P__(rel,j,i,oprio);  	  

       D__(rel,i,j,dis);    D__(rel,j,i,dis);  	  

	  }

  }

  return(card);

}	



int   reset_ex

(

  int        lev,

  int        mod,

  int        ncl,

  int        oprio,

  int        nprio,

  int        dis,

  int        tresh,

  ___P____  ** rel 

)

{

int od=0,i,j, adj1=100,adj2=100, card=0;;

  for(i=0;(i<CARD_(_N______))&&(card<ncl);i++)

  {   for(j=i+1;(j<CARD_(_N______))&&(card<ncl);j++)

	  {

       _P__(rel,i,j,adj1);

       if(adj1 != ____T) continue;

       D__(rel,i,j,dis);   D__(rel,j,i,dis);  	  

	   if(!lev) {

	       P__(rel,i,j,oprio);  P__(rel,j,i,oprio);  	  

	   }

	   else  {

           _C_(rel,i,j,od);    	 

		   if(od > tresh) {

			   P__(rel,i,j,oprio);      P__(rel,j,i,oprio); 

               C_(rel,i,j,(od-tresh));  C_(rel,j,i,(od-tresh));    	  

		   }

	       else  {

		       P__(rel,i,j,nprio);  P__(rel,j,i,nprio);  	  

		       CoIT1Done++;

		   }

	   }

       card++;

	  }

  }

  return(card);

}





double  DET(double A,double B,double a,double b) {return((A*b)-(B*a));}

double  V_l(double ux,double uy) {return(sqrt(Q_(ux)+Q_(uy)));}

double  SPV_(double ux,double uy,double vx,double vy) {return((ux*vx) + ((uy)*(vy)));}



//double  SPA_(ux,uy,vx,vy,a)      (V_l(ux,uy)*V_l(vx,vy)*cos(a))



double  R_60_r_x(double x,double y) {return((0.5*(x) + 0.5*sqrt(3.0)*(y)));}

double  R_60_r_y(double x,double y) {return((-0.5*sqrt(3.0)*(x) + 0.5*(y)));}

double  R_60_l_x(double x,double y) {return((0.5*(x) - 0.5*sqrt(3.0)*(y)));}

double  R_60_l_y(double x,double y) {return((0.5*(y) + 0.5*sqrt(3.0)*(x)));}





double AV_(double ux,double uy,double vx,double vy)

{

double x = SPV_(ux,uy,vx,vy)/(V_l(ux,uy)*V_l(vx,vy));

     x = MAX_(x,-1.0);

	 x = MIN_(x,1.0);

     return(acos(x));

}



double  A_L(double px,double py,double qx,double qy) {return(qy-py);}

double  B_L(double px,double py,double qx,double qy) {return(px-qx);} 

double  C_L(double px,double py,double qx,double qy) {return((py)*(qx) - (px)*(qy));}

 





double  LCut_x(double A,double B,double C,

			   double a,double b,double c) {return((-(b)*(C) + (B)*(c)) / DET(A,B,a,b));}

double  LCut_y(double A,double B,double C,

			   double a,double b,double c) {return((-(A)*(c) + (a)*(C)) / DET(A,B,a,b));}  





double JDIS_(double p0x,double p0y,double p1x,double p1y,double p2x,double p2y,double al)

{

double D=0.0;

  D = MAX_(DIS_(p0x,p0y,p1x,p1y),DIS_(p0x,p0y,p2x,p2y));

  return( D / al );	

}



BYTE  IsCut_HL

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



double sec_point_x(double lx, double ly, double az)

{

    if(!QEQ(az,0.0) && !QEQ(az,jpbPI))

	{

	 if(az < jpbPI)

	 { return(lx+1.0);} else {return(lx-1.0);}	

	}

	else   return(lx);  

}

double sec_point_y(double lx, double ly, double az)

{

    if(QEQ(az,0.0)) return(ly + 1.0);

    if(QEQ(az,jpbPI)) return(ly - 1.0);  



    if(!QEQ(az,(jpbPI/2.0)) && !QEQ(az,(3.0*jpbPI/2.0)))

	{

    	 if(az <  (jpbPI/2.0))     return(ly + tan((jpbPI/2.0) - az)); 	

	     if(az <  jpbPI)           return(ly - tan(az - (jpbPI/2.0))); 	

    	 if(az <  (3.0*jpbPI/2.0)) return(ly - tan((3.0*jpbPI/2.0) - az)); 	

         return(ly + tan(az - (3.0*jpbPI/2.0))); 	

	}

    else   return(ly);  

}



double direct_distance(double long1, double lat1, double azimuth1, double long2, double lat2, double azimuth2)

{

double  p1x=sec_point_x(long1, lat1, azimuth1),

        p1y=sec_point_y(long1, lat1, azimuth1),

        p2x=sec_point_x(long2, lat2, azimuth2),

        p2y=sec_point_y(long2, lat2, azimuth2),

		p1_60l_x,p1_60l_y,p1_60r_x,p1_60r_y,

		p2_60l_x,p2_60l_y,p2_60r_x,p2_60r_y,MDIS=9999999.0,D=0.0, ALPH=0.0;





        p1_60l_x = T_60_l_x(long1,lat1,p1x,p1y);

        p1_60l_y = T_60_l_y(long1,lat1,p1x,p1y);

        p1_60r_x = T_60_r_x(long1,lat1,p1x,p1y);

        p1_60r_y = T_60_r_y(long1,lat1,p1x,p1y);

        p2_60l_x = T_60_l_x(long2,lat2,p2x,p2y);

        p2_60l_y = T_60_l_y(long2,lat2,p2x,p2y);

        p2_60r_x = T_60_r_x(long2,lat2,p2x,p2y);

        p2_60r_y = T_60_r_y(long2,lat2,p2x,p2y);





   D = DIS_(long1,lat1,long2,lat2);

   if(D < 0.0) 

   printf("ERROR\n");

   ALPH = 1.0+AV_((p1x-long1),(p1y-lat1),(p2x-long2),(p2y-lat2));



if( BETP_(long1,lat1,long2,lat2,p2_60l_x,p2_60l_y,p2_60r_x,p2_60r_y) ||

    BETP_(long2,lat2,long1,lat1,p1_60l_x,p1_60l_y,p1_60r_x,p1_60r_y)   )

{  D = D / ALPH;



  if(D < 0.0) 

   printf("ERROR\n");



  return (D);

}



if(IsCut_HL((azimuth1-(jpbPI/3.0)),(azimuth2-(jpbPI/3.0)),

   long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y))

MDIS = JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),

	         Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),

             long1,lat1,long2,lat2,ALPH);

	

if(IsCut_HL((azimuth1-(jpbPI/3.0)),(azimuth2+(jpbPI/3.0)),

   long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y))

MDIS = MIN_(MDIS,

			JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),

	              Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),

                  long1,lat1,long2,lat2,ALPH));



if(IsCut_HL((azimuth1+(jpbPI/3.0)),(azimuth2+(jpbPI/3.0)),

   long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y))

MDIS = MIN_(MDIS,

			JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),

	              Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),

                  long1,lat1,long2,lat2,ALPH));



if(IsCut_HL((azimuth1+(jpbPI/3.0)),(azimuth2-(jpbPI/3.0)),

   long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y))

MDIS = MIN_(MDIS,

			JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),

	              Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),

                  long1,lat1,long2,lat2,ALPH));

	

return(MIN_(MDIS,5.0*D));



return (10.0*D);

}









int   F1_Next2Last1        

(

  T_S__   * Xx,

  T_S__   * Xc

)

{

int  i=0, n, n0, f, ix = -1;

///?    if(UBreak) return(ix);

    n0 = _N______->B+1;

    while(i < Xc->Card)

    {

      f = Xc->Chain[i];

      n = RhoLL(f,Xx);

      if(n<0)  {i++; continue;}

      if(!n)  

	  {FLAST = FFLX; return(i);}

	  if(n < n0) 

	  {

        FLAST = FFLX;

     	ix = i; n0 = n;

		i++; continue;

	  }

	  if(n == n0) 

	  {

		  if(XC(NIX(f)) > XC(NIX(Xc->Chain[ix]))){

            FLAST = FFLX;

         	ix = i; /////n0 = n;

		  }

	  }

      i++;

///?	  UserBreak();

    }

    return(ix);

}

int   F1_Next2Set1        

(

  T_S__   * Xx,

  T_S__   * Xc

)

{

int  i=0, n, n0, f, ix = -1;

///?   if(UBreak) return(ix);

   n0 = _N______->B;

    while(i < Xc->Card)

    {

      f = Xc->Chain[i];

      n = RhoCC(f,Xx);

      if(!n)  {i++; continue;}

      if(n < 2)   return(i);

      else

		  if(n < n0) 

		  {ix = i; n0 = n;}

      i++;

///?	  UserBreak();

    }

    return(ix);

}





BYTE  F1_ExtendB1

(

  T_S__    * xQ,

  T_S__    * cX

)

{

int  kx = -1;

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

      } else  done=False;

///?	  UserBreak();

   }

   return(ext);

}



BYTE  F1_EFInB1

(

  T_S__    * xQ,

  T_S__    * cX

)

{

int  kx = -1;

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

      } else  done=False;

///?	  UserBreak();

   }

   return(ext);

}



BYTE  F1_FillInB1

(

  T_S__    * xQ,

  T_S__    * cX

)

{

int  ix=0;

BYTE fill=False, done=True;

///?     if(UBreak) return(fill);

   while( done && (ix<cX->Card)  && cX->Card)

   {

	 if(RRRX(cX->Chain[ix],xQ))

     {

      xQ->Chain[xQ->Card++]=cX->Chain[ix];

      SkipEl(ix,coX);     

      fill=True;

     } else  done = False;

///?   UserBreak();

   }

   return(fill);

}



int  F1_XOrderNet1

(

  T_S__    * xQ,

  T_S__    * cQ

)

{

int ddo=DD;

BYTE proc=True, p1=True, p2=True;

///?    if(UBreak) return(0);

   while( proc )

   {

proc = (FBAND && F1_ExtendB1(xQ,cQ)) ||

                 F1_EFInB1(xQ,cQ)    ||

				 F1_FillInB1(xQ,cQ);

///?   UserBreak();

   }



   return(cQ->Card);



}

int  F1_get_last_enab 

(

   int   cid

) 

{

int i;

for(i=_N______->B;0<i;i--) 

if(_N______->ENAB[cid][i]>0) return(i-1);



return(-1);

}

int  F1_FxCompFa1

(

  T_C___   * xN,

  T_S__    * Qxx,

  T_S__    * cQ

)

{

int i=0;



if(!cQ->Card) return(0);	





if(!_N______->FXF) {

   enab_net(_N______, cQ->Chain[0]);



if((i = F1_get_last_enab(xN[cQ->Chain[0]].Nix)) < 0){	   

fprintf(Protfp,"!!!!!!!!ERROR whole band unabled for cell %d\n",xN[cQ->Chain[0]].Nix);

return(1);

}

     xN[cQ->Chain[0]].Fn = i;

     Qxx->Chain[Qxx->Card++]=cQ->Chain[0];

     SkipEl(0,cQ);     



FBAND= True && ((_N______->ub - _N______->lb) < FBANDL);

}	



   return(F1_XOrderNet1(Qxx,cQ));

}



int  F1_CompFa1

(

  T_C___   * xN,

  T_S__    * Qxx,

  T_S__    * cQ

)

{

int val=0;

   Qxx->Chain[0]=0; Qxx->Card++;

   xN[0].Fn = _N______->B / 2;

   _N______->lb  = xN[0].Fn;

   _N______->ub  = _N______->lb;

   FBAND=True;

   Diff_SET(cQ,Qxx);



   val = F1_XOrderNet1(Qxx,cQ);



   ShiftB(xN);

   return(val);

}



int F1_analyze_FACC

(

   T_C___    * cetx,

   T_S__     * Qxx,

   T_S__     * cQ

)

{

int i,j;

    for(i=0;i<_N______->Card;i++) XC(i) = NC(i);



	Qxx->Card = 0;

    NatOrd(NOFC,cQ);



	if(FIX) {

        _N______->ub = _N______->B -1;

        _N______->lb = 0;

		if(_N______->FXF) FA_fixed(cetx,Qxx,cQ);

		i = F1_FxCompFa1(cetx,Qxx,cQ);

	}else

    i = F1_CompFa1(cetx,Qxx,cQ);





	if(!i) {

		for(j=0;j<NOFC;j++) *(XCET+j) = *(cetx+j);  PlExist = 1;

	}else

		strcpy(XSITE,_N______->_N___[NIX(cQ->Chain[0])]->S);            

    return(i);

}

int  opt_all_it

(

   int        ncl,

   int        iprio,

   int        sprio,

   int        oprio,

   int        odis,

   int        ndis,

   int        tresh,

   ___P____  ** rel

)

{

int QC=0,bt=tresh,lev=1;

       QC = set_t_it(ncl,iprio,tresh,rel);

    if(!QC) return(0);     

fprintf(Protfp,">>>>>>>T_S__ %d for TR=%d\n",QC,tresh);

	if(!F1_analyze_FACC(cet, Q0, coX))  {

		LASTQC = QC;

		set_ex(QC,sprio,rel);     

fprintf(Protfp,">>>>>>>!!!!SET %d to %d for TR=%d\n",QC,odis,tresh);

        return(1);

	}

	if(QC < MIN_SET+1) 	{

    	fprintf(Protfp,"--NOT SET %5d-Prio%d with TR=%3d\n",QC,iprio,tresh);

		extract_pair(QC,oprio,MAX_(1,ndis),rel);

        return(2);

	}

    else 

		reset_ex_back(QC,iprio,odis,rel);

fflush(Protfp);

        LASTQC = QC;

    return(3);

}







void  set_step

(

  int prio

)

{

int QC = MIN_SET, AT = 0, AT1, lim,bt;	



    PLOW = prio + 1;

	AT = PPar7[prio].pcmax;

    while(PPar7[prio].pclb < AT)  {

         WPL=0;

		 switch(opt_all_it(QC,prio,____X,prio-1,

			    PPar7[prio].pdisr,PPar7[prio].pdisr-1,AT,_N______->_E___))

		 {

           case 0:  AT--;   QC = MAX_(QC,MIN_SET); break;

		   case 1:  WPL=1;AT1 = AT; QC = MAX_(MIN_SET,2*LASTQC);break;  

           case 2:  AT1 = AT;break; 

		   default: AT = AT1; QC = MAX_(MIN_SET,LASTQC/2); break; 			   

		 }

     lim = (int)((100.0*((float)CoIT1Done) / CoIT1));

     if(lim>PROGRESS){

		 PROGRESS = lim;

	 }

	 }	 

	 bt = DD;

     if(F1_analyze_FACC(cet, Q0, coX)) {

		DD = 1-bt;  

		F1_analyze_FACC(cet, Q0, coX);

	}

fflush(Protfp);



}





int  opt_all

(

   int        ncl,

   int        iprio,

   int        sprio,

   int        oprio,

   int        odis,

   int        ndis,

   ___P____  ** rel

)

{

int QC;

    if(!ncl) return(0);

    QC = set_t_ex(ncl,iprio,odis,ndis,rel);

    if(!QC) return(0); 

	if(!F1_analyze_FACC(cet, Q0, coX))  {

		LASTQC = QC;

        set_ex(QC,sprio,rel);     

        return(1);

	}

	if(QC < MIN_SET+1) 	{

fprintf(Protfp,"!!!!!!!!!!!Not set are:>>>>>\n");

		reset_ex(1,0,QC,iprio,oprio,odis,___C__,rel);

        return(2);

	}

    else 

		reset_ex(0,1,QC,iprio,oprio,odis,___C__,rel);

fflush(Protfp);

    return(3);

}



BYTE  set_all

(

  int prio

)

{

int noass=0;

  PLOW = prio;



  if((noass=F1_analyze_FACC(cet, Q0, coX))) {

fprintf(Protfp,"ERROR:check failed\n");

    w_____P_____(cet);

	fflush(Protfp);   



    return(False);

  }

  CoIT1Done += PPar7[prio].pcoit1;

  w_____P_____(cet);

  fflush(Protfp);      

///?UserBreak();

return(True);

}

void  write_ctrl

(

  char  * pctrfile

)

{

int i=0;

char lx[MaxLineL+1];

FILE * fctrp;



  if((fctrp =fopen(pctrfile,"r")) == NULL){

	  printf("ERROR by open filr %s",pctrfile);;

      return;

  }

  

  fprintf(Protfp,"CONTROL FILE:::>\n");

  while((i<26 )&& (fgets(lx,MaxLineL,fctrp) != NULL))

  {

     fprintf(Protfp,lx);

  }

  fprintf(Protfp,"CONTROL FILE:::<\n\n\n");

  fclose(fctrp);      

}

BOOL set_data

(

  char  * pctrfile

)

{

int i=0,j=0;

char ax[MaxLineL],lx[MaxLineL+1], *xx;

FILE * fctrp;



  XSITE[0] = '\0';

  if((fctrp =fopen(pctrfile,"r")) == NULL){

	  printf("ERROR by open filr %s",pctrfile);;

      return(False);

  }

 



  while((i<26 )&& (fgets(lx,MaxLineL,fctrp) != NULL))

  {

	switch(i){

	case 0: 

    sscanf(lx,"%s %d",ax,&_S____);i++; continue; break;

	case 1: 

    sscanf(lx,"%s %d",ax,&_C____);i++; continue; break;

	case 2: 

    sscanf(lx,"%s %d",ax,&_N____);i++; continue; break;

	case 3: 

    sscanf(lx,"%s %d",ax,&_N_____);i++; continue; break;

	case 4: 

    sscanf(lx,"%s %d",ax,&_O____);i++; continue; break;

	case 5: 

    sscanf(lx,"%s %d",ax,&ReCalAll);i++; continue; break;

	case 6: 

    sscanf(lx,"%s %d",ax,&UTraf);i++; continue; break;

	case 7: 

    sscanf(lx,"%s %d",ax,&USON);i++; continue; break;

	case 8: 

    sscanf(lx,"%s %d",ax,&QUALITY);i++; continue; break;

	case 9: 

    sscanf(lx,"%s %d",ax,&DECOMP);i++; continue; break;

	case 10: 

    sscanf(lx,"%s %d",ax,&ExCliq);i++; continue; break;

	case 11: 

    sscanf(lx,"%s %d",ax,&XSC);i++; continue; break;                    

	case 12: 

		i++; 

		continue; break;

	case 13: 

    sscanf(lx,"%s %d",ax,&HType);i++; continue; break;

	case 14: 

    sscanf(lx,"%s %d",ax,&UGroup);i++; continue; break;

	case 15: 

    sscanf(lx,"%s %d",ax,&NOGR);i++; continue; break;	



 	case 16: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(PrF___,xx+1);	

	 if( (xx = strchr(PrF___,(int)('"'))) != NULL) xx[0]='\0';

	 i++; continue;}

	else printf("ERROR by reading Congiguration File line %d\n",i+1);

	break;

	case 17: 

    sscanf(lx,"%s %d",ax,&CCard);i++; continue; break;                    

	case 18: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(C_F___,xx+1); 

	 if( (xx = strchr(C_F___,(int)('"'))) != NULL) xx[0]='\0';

	i++; continue;}

	else printf("ERROR by reading Configuration File line %d\n",i+1);

	break;

	case 19: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(N_F___,xx+1);	 

	 if( (xx = strchr(N_F___,(int)('"'))) != NULL) xx[0]='\0';

	 i++; continue;}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	case 20: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(I_F___,xx+1);	

	 if( (xx = strchr(I_F___,(int)('"'))) != NULL) xx[0]='\0';

	 i++; continue;}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	case 21: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(P_F___,xx+1);	

	 if( (xx = strchr(P_F___,(int)('"'))) != NULL) xx[0]='\0';

	 i++; continue;}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	case 22: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(ClF___,xx+1);	

	 if( (xx = strchr(ClF___,(int)('"'))) != NULL) xx[0]='\0';

	i++; continue;}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	case 23: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(F_F___,xx+1);	

	 if( (xx = strchr(F_F___,(int)('"'))) != NULL) xx[0]='\0';

	i++; continue;}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	case 24: 

    if( (xx = strchr(lx,(int)('"'))) != NULL) 

	{strcpy(E_F___,xx+1);	

	 if( (xx = strchr(E_F___,(int)('"'))) != NULL) xx[0]='\0';

	i++; continue;}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	case 25: 

	if( ((xx = strchr(lx,(int)(' '))) != NULL) ) {

      strcpy(lx,xx+1); 

	  sscanf(lx,"%d",&NOC);  ///?????

      FC____ = (int *) calloc(NOC,sizeof(int));

      AllErr_(FC____,"AllocErr  by alloc Carr",exit(0);)

      j=0;

      while( ((xx = strchr(lx,(int)(' '))) != NULL) && (j<NOC )) {

	    strcpy(lx,xx+1); 

		sscanf(lx,"%d",&FC____[j]);j++;

	  }

      i++; continue; 

	}

	else printf("ERROR by reading Configuration Fileline %d\n",i+1);

	break;

	default : 

		break;

    }

  }	

 



  OSET = FC____[0] ;

  NOFF = FC____[NOC-1] - OSET + 1;         



	

	   ALLN     = 0; SONCC =0;



   FBANDL = NOFF; 



   

   if(!read_net_new(C_F___)) return(0);       



   if(!FDemand) return(1);           

   _n____t = A___S__(FDemand);     

   cet  = A___C__(FDemand);

   Qx   = A___S__(FDemand);   

   coX  = A___S__(FDemand);

   Q0   = A___S__(FDemand);



   MAXDIS = _C____;



   FIX =  1 && (_N______->FXF + strlen(F_F___) + (int)(NOFF != NOC));

   a_____e___(FIX,_N______);                             



   init_graph(_N______,_N______->_E___);                   

   if(NOFF != NOC) disab_carriers();                    





   OrderNet();                        



   NatOrd(_N______->Card,_n____t);       



   LayerCard(XSC);                      

   

   if(strlen(F_F___))  {

	   FORBF = 1;

	   if(!read_forbidden(F_F___)) {         

           fclose(fctrp);      

		   return(False);;

	   }

   }

   GROUPING = UGroup;





   fclose(fctrp);      

  return(True);

}



  

int  japa_awe

///?japa1

(  

   char * pctrfile

)

{

BOOL cr_read=False,it_read=False, COIT=False,ADIT=False;

int  lim=0, QC=0,QC2=0, nt=0, pt=1000, AT1=0, unb=1, uit=0, p=0;

double  x=0.0;

char c='\0';





    PlExist = 0;   fflush(stdin);







///?	if(getch() > 0) printf("HALLO"); 





	if(!set_data(pctrfile))  {

		f___();

        return(1);

	}



    unb  = unb  && (_N_____+_N____);                    

	USON = USON && unb;                               



   	COFSET = 333;

    IMAX   = 331;   

    SONMIN = NNSIGN_(_N____)*USON*COFSET;  

	NMIN   = COFSET + SONMIN;



    for(p=9;0<p;p--){

	  PPar7[p].pcmax = 999;

      PPar7[p].pclb  = 0;

      PPar7[p].pcoit1  = 0;

	  PPar7[p].pdisr = 2;

	  PPar7[p].pdism = 1;

	}

	PPar7[____C].pdisr  = 3;

	PPar7[____S].pdisr  = 2;

	PPar7[____2N].pdisr = 2;

	PPar7[____NS].pdisr = 1;



	PPar7[____C].pclb   = 998;

	PPar7[____S].pclb   = 997;

	PPar7[____2N].pclb  = NMIN-1;

	PPar7[____NS].pclb  = SONMIN-1;

   

    /*
   if((Protfp =fopen(PrF___,"w")) == NULL){

	  printf("ERROR by open filr %s",PrF___);;

      return(False);

	}*/
    Protfp = stdout;

	

	write_ctrl(pctrfile);

    PLFILE = P_F___;





	if(strlen(E_F___))  {

		EXCEPF = 1;

		if(!read_exceptions(E_F___,____X,_N______->_E___)){  

fprintf(Protfp,"ERROR by reading Exception-File\n\n");

          f___();

          return(1);

		}

	}







     CoIT1 = 0;           

	 CoIT1Done = 0;      



fprintf(Protfp,"\nJPB-FA IT read ???======================\n");

  if(!uit && !read_IT(____X,____CI,I_F___,_N______->_E___,UTraf)){            

fprintf(Protfp,"\nJPB-FA IT read ======================\n");

   f___();

   return(NR_E);

}







uit = 1;

if(_C____) {

fprintf(Protfp,"??CCC-check:\n\n");

   set_cr_CC_it(____X,____C,_C____,___C__,0,_N______->_E___); 

}





if(_S____){

fprintf(Protfp,"??SCC-check:\n\n");

   set_cr_SCC_it(____C,____S,_S___,___C__,0,_N______->_E___);

}





if(unb)  {

   if(!read_neighbors(N_F___,____2N,1,NMIN,33,_N______->_E___)){

fprintf(Protfp,"ERROR by reading Neighbour-File\n\n");

     f___();

     return(NR_E);

   }

}







if(unb && USON)

  son_closure_it(____2N,____NS,1,-1,SONMIN,_N______->_E___); 





if(!DECOMP) {                      

		IDform_Set(_N______->Card,Q0);  	

	    Sect_cet(Q0);

} else {

    partition_net(____2N,NOC,1,1,XSC,ClF___); 

}



///////?if(getch()) printf("HALLO!!");



///? p=0; if((p=getchar())>0) printf("HALLO!!");



c_coit1(_N______->_E___);



PROGRESS = 0;

for(p=____C;(NOLOCL < p);p--){                         

fprintf(Protfp,"PRIO = %d, DISR = %d  >>>>>\n",p,PPar7[p].pdisr);

   if(!set_all(p))  

         set_step(p);

fprintf(Protfp,"<<<<<<<<<<<<<<<PRIO = %d, DISR = %d\n",p,PPar7[p].pdisr);

     lim = (int)((100.0*((float)CoIT1Done) / CoIT1));

     if(lim>PROGRESS){

		 PROGRESS = lim;

///?	     SetProgressBar(lim);

	 }

///?	 fflush(stdin);   

///? UNIX      UBreak = read(0,&c,1); 

///?	 UBreak = getch();

///?if(UBreak = getc(stdin)) > 0;

///?	 UBreak = getc(stdin);

///?	 UBreak++;

///?fprintf(Protfp,"DANACH   UBREAK = %d>>>>\n",UBreak);

///?	 UBreak=kbhit();

///?UserBreak;

}



w_____P_____(cet);fflush(Protfp);        

         wpc___(1);   



///?	 f___();

     return(NO_E);



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







int write_last_plan

(int pmod)                 //(void)



{

	if(!PlExist) return(0);

///?	(1,XCET);             ///?write_FA(1,XCET);

///?    write_ceplan(pmod,PLFILE,XCET,PBAND);

fflush(Protfp);

	return(1);

}





void  free_all(void)

{

int i;

	if(_N______)  {

        for(i=0;i<_N______->Card;i++) {

			if(_N______->_N___[i]) {

				free(_N______->_N___[i]->ff);

		        free(_N______->_N___[i]);

			}

		}

 		free(_N______->_N___);

        for(i=0;i<_N______->Card;i++) 

			free(_N______->_E___[i]);

		free(_N______->_E___);

		free(_N______->Perm);

		free(_N______->ENAB);		

		free(_N______);

	}

    if(cet)     free(cet);

    if(XCET)    free(XCET);

    if(_n____t) {free(_n____t->Chain);free(_n____t);}

	if(Qx)      {free(Qx->Chain);free(Qx);}

	if(coX)     {free(coX->Chain);free(coX);}

	if(Q0)      {free(Q0->Chain);free(Q0);}



    if(Protfp)     fclose(Protfp);

    unlink(ClF___);

}



