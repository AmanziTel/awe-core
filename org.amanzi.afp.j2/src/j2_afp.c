/******************************************************************************
 //  Author: Jan Paul Bernert.
 //  The intellectual property of the software is limited to the author only.
 //  This version is a non-exclusive licence for AMANZITel only.
 //  It is not allowed to alter, modify, copy, or adapt the software as well 
 //  as any part of it as well as the documentation.
 //  Date: March, 3rd, 2011;  March, 22nd, 2011;
 //
 *******************************************************************************/

#include <stdlib.h>
#include <stdio.h>
//#include <conio.h>
#include <string.h>
#include <math.h>

#define PTR(t)    t*

#define L_N_Mx    80
#define L_L_Mx    30000     
#define MaxFileName 256
#define J_PI_       3.1415926

#define Bool_sin(a)      (((a)!=0) ? 1 : 0)
#define Bool_cos(a)      (((a)!=0) ? 0 : 1)

#define Error_Msg__ALLOC(X,msg,act) if(X==NULL){printf("\n%s\n",msg);act;}
#define _MAX_(a,b)     (((a)<(b)) ? (b) : (a))
#define _MIN_(a,b)     (((a)<(b)) ? (a) : (b))
#define sum_B(x,a,b)  (Bool_sin(x)*(a) + Bool_cos(x)*(b))
#define O_sign(a)      ((0 < (a)) ? 1 : -1)
#define O_N_N_sign(a)    ((0 < (a)) ? 1 : 0)

#define _freeSET(M)     if(M!=NULL){free(M->Chain);free(M);M=NULL;}
#define GRAPH        Graph        
#define G_CARD        GRAPH->Card
#define MEMBER_(i,Q)    Q->Chain[i]
#define CARD_(Q)       Q->Card
#define iVertex_(i)      MEMBER_(i,SetOfVertexes)      

#define VERTEX(i)       GRAPH->vertex[i]
#define VERTEXID(i)         GRAPH->vertex[i]->C_Cl_VId
#define iV_Color_Req(i)         GRAPH->vertex[i]->C_Cl_ColReq
#define iV_Color_Tmp(i)         GRAPH->vertex[i]->C_Cl_ColTmp
#define iV_Color_(i)         GRAPH->vertex[i]->C_Cl_Col_
#define EDGE(i,j)     GRAPH->Edge[i][j]
#define PERM(i)       GRAPH->Perm[i]

#define VERTEX_(i)      VERTEX(iVertex_(i))
#define VERTEXID_(i)        VERTEXID(iVertex_(i))
#define iV_Color__(i)        iV_Color_(iVertex_(i))
#define EDGE_(i,j)    EDGE(iVertex_(i),iVertex_(j))
#define iVertex__(i,Q)   iVertex_(MEMBER_(i,Q))
#define VERTEX__(i,Q)     VERTEX_(MEMBER_(i,Q))
#define iV_Color___(i,Q)       iV_Color__(MEMBER_(i,Q))
#define EDGE__(i,j,Q)   EDGE_(MEMBER_(i,Q),MEMBER_(j,Q))
#define PERM__(i,Q)     PERM(MEMBER_(i,Q))
#define COMPA_(R,i,j)     R[VERTEXID_(i)][VERTEXID_(j)]

#define iS_CLUSTER(i)   VERTEX(i)->C_Cl_SCl_Name
#define iC_CLUSTER(i)   VERTEX(i)->C_Cl_LettId

#define iATSet_VIx(i)     AT_Set[i].VertexIx
#define iATSetLC(i)     AT_Set[i].AT_LC_No
#define iATSetColor(i)    AT_Set[i].AT_Color

#define ParCARD     10

#define IT2_L      2
#define IT1_L      3
#define DIS_L      1  
#define PRI_L      1  
#define xDATA_L     0            
#define yDATA_L     0
#define zDATA_L     0
#define COMPREL_INITVAL  "00      "          

#define COMP_FIELD_L  (PRI_L+DIS_L+IT1_L+IT2_L+xDATA_L+yDATA_L+zDATA_L+2)

#define EX_PRIO      9                              
#define TMP_PRIO      8 
#define CCl_PRIO      7   
#define SCl_PRIO      6
#define N2_PRIO     5
#define SON_PRIO     4
#define IT1_PRIO     3
#define IT2_PRIO     2
#define NO_PRIO     1
#define NUL_PRIO     0

#define IT_MAX    999
#define ITN_OFFSET      333
#define IT1_MAX        331
#define IT2_MAX        32
#define NO_ERR       0
#define IT_R_ERR       4

#include <ctype.h>
__inline void _my_sscanf1(char* p, int* r) {
	*r = *p -'0';
}
#define R_PRIO(R,i,j,r)      r=0;_my_sscanf1(R[i][j],&r)
#define R_DIST(R,i,j,r)      r=0;_my_sscanf1(R[i][j]+PRI_L,&r)
#define R_IT1(R,i,j,r)       r=0;sscanf(R[i][j]+(PRI_L+DIS_L),"%3d",&r)
#define W_PRIO(R,i,j,w)      jprtl(R[i][j],w,PRI_L) 
#define W_DIST(R,i,j,w)      jprtl(R[i][j]+PRI_L,w,DIS_L)
#define W_IT1(R,i,j,w)       jprtl(R[i][j]+(PRI_L+DIS_L),w,IT1_L)
#define R_PRIO_(R,i,j,r)  _my_sscanf1(COMPA_(R,i,j),&r)
#define R_DIST_(R,i,j,r)  _my_sscanf1(COMPA_(R,i,j)+PRI_L,&r)

typedef struct set {
	int * Chain, Card;
} SET_T;
typedef struct atomob {
	int atno, atdist;
} ATOB_T;
typedef struct pair {
	int Left, Right;
} PAIR_T;
typedef struct priopar {
	int act_prio, pdisr, pdism, pclb;
} G_PAR_T;
G_PAR_T G_PAR[ParCARD];

typedef char R_COMPATIBILITY[COMP_FIELD_L];

typedef struct atom {
	int VertexIx, AT_LC_No, AT_Color;
	short AT_Color_found;
} AT_T;

typedef struct c_vert {
	long C_Cl_CurrCl;
	int C_Cl_No, C_Cl_waste, C_Cl_VId, *C_Cl_ColSet, C_Cl_ColReq, C_Cl_Col_,
			C_Cl_ColTmp, C_Cl_ColAct, C_Cl_ColFix;
	char C_Cl_SCl_Name[L_N_Mx], C_Cl_LettId;
} C_VERTEX_T;

typedef struct graph_s {
	int G_COLSPECTRUML, G_LowerBound, G_UpperBound, G_AllFixColCard, Card,
			G_LB0, G_LB[3];
	C_VERTEX_T ** vertex;
	R_COMPATIBILITY ** Edge;
	short ** ENAB;
	unsigned char * Perm;
} GRAPH_T;

ATOB_T AT_O_L, AT_O_R;
GRAPH_T *Graph;
AT_T * AT_Set, * ATPl_Tmp;

SET_T *SetOfVertexes, *G_S_Qx_, *G_S_coX, *G_S_Q0_;
short G_Col_Inside=1;
long G_ToComp=0, CurrClNo=0;

int Assignment_Found=0, NoOfAtoms=0, xx_NoOfCol=0, ColorSpectrum_L=0, Max_DIST =
		0, LOW_PRIO=0, SON_IT_Min, N_IT_Min, LAST_Col = -1, ACT_Col = -1,
		G_Min_SET = 20, G_Max_SET = 200, G_Cur_SET=0, G_Act_d=1, G_Not_Empty=0,
		G_Col_Offset=0;

int C_DIST=0, S_DIST=0, N_DIST_REQ=0, SON_DIST=0, N_DIST_MIN=0, ReCalc_All=0,
		PARTITION_Exist=0, HOPPING_Type, USE_Grouping, GROUP_CARD, Max_ATCell,
		Max_ATSCl, Use_TRAF=0, Use_SON=0, QUALITY=50, PARTITION=1, CCl_CARD,
		ColInterval_L, ColorsCard, *Colors;
char GraphFile[MaxFileName+1], NeigFile[MaxFileName+1],
		IntfFile[MaxFileName+1], ProtFile[MaxFileName+1],
		ColorFile[MaxFileName+1], CliqFile[MaxFileName+1],
		ForbFile[MaxFileName+1], ExcpFile[MaxFileName+1];

#define P_2(a)                    pow(a,2)
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
#define BETP_(x,y,px,py,p1x,p1y,p2x,p2y) \
        (BETV_((x-px),(y-py),(p1x-px),(p1y-py),(p2x-px),(p2y-py)))

#define IsCut_L(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y)  \
                                 ISCUT(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),\
                                       A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y)) 

#define Cut_x(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y)   \
        LCut_x(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),C_L(p1x,p1y,q1x,q1y),\
               A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y),C_L(p2x,p2y,q2x,q2y))  

#define Cut_y(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y)   \
        LCut_y(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),C_L(p1x,p1y,q1x,q1y),\
               A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y),C_L(p2x,p2y,q2x,q2y))  

void jpb_strAsubst(char * here, char * old, char * neu) {
	char *l_chp, *l_xstr;
	long l_ol=0, nl = 2*(strlen(here)+strlen(neu));
	l_xstr = (char *) calloc(nl, sizeof(char));
	l_ol = strlen(old);
	nl = strlen(neu);
	strcpy(l_xstr, neu);
	l_chp = here;
	while ((l_chp = strstr(here, old)) != NULL) {
		strcpy(l_xstr+nl, l_chp+l_ol);
		strcpy(l_chp, l_xstr);
	}
	free(l_xstr);
}

void jcpyl(char * into, char * str) {
	char * l_xstr=(char*)calloc(strlen(into)+1, sizeof(char));
	strcpy(l_xstr, into+strlen(str)); //? wie lang ist str?? muss < into sein
	strcpy(into, str);
	strcat(into, l_xstr);
	free(l_xstr);
}

void jprtl(char * into, int value, int len) {
	char * l_xstr=(char*)calloc(len+1, sizeof(char));
	switch (len) {
	case 1:
		sprintf(l_xstr, "%1d", value);
		break;
	case 2:
		sprintf(l_xstr, "%2d", value);
		break;
	case 3:
		sprintf(l_xstr, "%3d", value);
		break;
	default:
		break;
	}
	jcpyl(into, l_xstr);
	free(l_xstr);
}

GRAPH_T * _a_GRAPH(int noofccl, int maxatccl) {
	int i;
	GRAPH_T *GRAPH;

	GRAPH = (GRAPH_T *) calloc(1,sizeof(GRAPH_T));
	Error_Msg__ALLOC(GRAPH,"AllocErr by alloc GRAPH",exit(0);)

	GRAPH->vertex = ( PTR(C_VERTEX_T) *) calloc(noofccl,sizeof(PTR(C_VERTEX_T)));
	Error_Msg__ALLOC(GRAPH->vertex,"AllocErr by alloc GRAPH->vertex",exit(0);)
	for (i=0; i<noofccl; i++) {
		GRAPH->vertex[i] = (C_VERTEX_T *) calloc(1,sizeof(C_VERTEX_T));
		Error_Msg__ALLOC(GRAPH->vertex[i],"AllocErr by alloc GRAPH->vertex[]",exit(0);)
		GRAPH->vertex[i]->C_Cl_ColSet = (int*) calloc(maxatccl,sizeof(int));
	}
	GRAPH->Edge = (PTR(R_COMPATIBILITY) *) calloc(noofccl,sizeof(PTR(char)));
	Error_Msg__ALLOC(GRAPH->Edge,"AllocErr  by alloc GRAPH->Edge",exit(0);)
	for (i=0; i<noofccl; i++) {
		GRAPH->Edge[i] = (R_COMPATIBILITY *) calloc(noofccl,sizeof(R_COMPATIBILITY));
		Error_Msg__ALLOC(GRAPH->Edge[i],"AllocErr by alloc GRAPH->Edge[]",exit(0);)
	}
	GRAPH->ENAB = NULL;
	// GRAPH->INCI = NULL;
	GRAPH->Perm = (PTR(unsigned char)) calloc(noofccl,sizeof(unsigned char));
	Error_Msg__ALLOC(GRAPH->Perm,"AllocErr  by alloc GRAPH->Perm",exit(0);)
	return (GRAPH);
}

void _f_ALL(void) {
	int i;
	if (GRAPH) {
		for (i=0; i<GRAPH->Card; i++) {
			if (GRAPH->vertex[i]) {
				free(GRAPH->vertex[i]->C_Cl_ColSet);
				free(GRAPH->vertex[i]);
			}
		}
		free(GRAPH->vertex);
		for (i=0; i<GRAPH->Card; i++)
			free(GRAPH->Edge[i]);
		free(GRAPH->Edge);
		free(GRAPH->Perm);
		free(GRAPH->ENAB);
		free(GRAPH);
	}
	if (AT_Set)
		free(AT_Set);
	if (ATPl_Tmp)
		free(ATPl_Tmp);
	if (SetOfVertexes) {
		free(SetOfVertexes->Chain);
		free(SetOfVertexes);
	}
	if (G_S_Qx_) {
		free(G_S_Qx_->Chain);
		free(G_S_Qx_);
	}
	if (G_S_coX) {
		free(G_S_coX->Chain);
		free(G_S_coX);
	}
	if (G_S_Q0_) {
		free(G_S_Q0_->Chain);
		free(G_S_Q0_);
	}

}

AT_T * _a_AT_set(int p_card) {
	int i;
	AT_T *l_atset;
	l_atset = (AT_T *) calloc(p_card, sizeof(AT_T));
	ATPl_Tmp = (AT_T *) calloc(p_card, sizeof(AT_T));
	Error_Msg__ALLOC(l_atset,"AllocErr by alloc l_atset",exit(0);)
	Error_Msg__ALLOC(ATPl_Tmp,"AllocErr by alloc ATPl_Tmp",exit(0);)
	for (i=0; i<p_card; i++) {
		l_atset[i].AT_Color_found = 0;
		ATPl_Tmp[i].AT_Color_found = 0;
	}
	return (l_atset);
}

SET_T * _a_SET(int p_card) {
	SET_T * M;
	M=(SET_T *) calloc(1, sizeof(SET_T));
	Error_Msg__ALLOC(M,"AllocErr in _a_SET",return(NULL);)
	M->Chain = (int *)calloc(p_card, sizeof(int));
	Error_Msg__ALLOC(M->Chain,"AllocErr in _a_SET by M->Chain",return(NULL);)
	M->Card = 0;
	return (M);
}

int _L_eOfSet(int p_el, SET_T * p_set) {
	int i=0, a=0;
	while (!(a) && (i<p_set->Card)) {
		a = ((a||(p_set->Chain[i]==p_el)));
		i++;
	}
	return (i-Bool_sin(a));
}
void _T_skip_ixOfSet(int p_ix, SET_T * p_set) {
	int i=0;
	if (p_ix == (p_set->Card - 1)) {
		p_set->Card--;
		return;
	}
	i=p_ix;
	while (i < p_set->Card - 1) {
		p_set->Chain[i]=p_set->Chain[i+1];
		i++;
	}
	p_set->Card--;
}
void _O_copy_Set(SET_T * p_set0, SET_T * p_set) {
	p_set0->Card=0;
	while (p_set0->Card<p_set->Card) {
		p_set0->Chain[p_set0->Card] = p_set->Chain[p_set0->Card];
		p_set0->Card++;
	}
}
void _O_diff_Set(void) {
	int i, l_ix;
	for (i=0; i<G_S_Q0_->Card; i++) {
		l_ix = _L_eOfSet(G_S_Q0_->Chain[i], G_S_coX);
		if (l_ix<G_S_coX->Card)
			_T_skip_ixOfSet(l_ix, G_S_coX);
	}
}
void _h_SET_id(void) {
	int i;
	for (i=0; i<GRAPH->Card; i++)
		G_S_Q0_->Chain[i] = i;
	G_S_Q0_->Card = GRAPH->Card;
}

void _OUT_coloring2file(void) {
	int l_x, i, j=0;
	FILE * l_filep;
	if ((l_filep =fopen(ColorFile, "w")) == NULL) {
		printf("ERROR:: opening file %s", ColorFile);;
		return;
	}

	while (j<CARD_(GRAPH)) {
		fprintf(l_filep, "%s %d %d %d %d ", GRAPH->vertex[j]->C_Cl_SCl_Name,
				GRAPH->vertex[j]->C_Cl_No, GRAPH->vertex[j]->C_Cl_waste,
				GRAPH->vertex[j]->C_Cl_ColReq, GRAPH->vertex[j]->C_Cl_ColAct);
		for (i=0; i<GRAPH->vertex[j]->C_Cl_ColAct; i++) {
			l_x = GRAPH->vertex[j]->C_Cl_ColSet[i]+G_Col_Offset;
			fprintf(l_filep, "%d ", l_x); //  fprintf(l_filep,"%d ",x%p_model + OffSet);
		}
		fprintf(l_filep, "\n");
		j++;

	}
	fclose(l_filep);
}

short _h_s_col_fix(int p_v_ix, int p_col_ix) {
	int l;
	if (!GRAPH->vertex[p_v_ix]->C_Cl_ColFix)
		return (0);
	for (l=0; l<GRAPH->vertex[p_v_ix]->C_Cl_ColFix; l++)
		if (GRAPH->vertex[p_v_ix]->C_Cl_ColSet[l] == p_col_ix)
			return (1);
	return (0);
}

void _OUT_coloring2protfile(void) {
	int i, j, l_ik;

	for (i=0; i<GRAPH->Card; i++) {
		GRAPH->vertex[i]->C_Cl_ColAct = GRAPH->vertex[i]->C_Cl_ColFix;
	}

	for (i= -1; i<GRAPH->G_UpperBound+1; i++) {
		l_ik=0;
		for (j=0; j<xx_NoOfCol; j++) {
			if (i==ATPl_Tmp[j].AT_Color) {
				if (!_h_s_col_fix(ATPl_Tmp[j].VertexIx, i)) {
					if (GRAPH->vertex[ATPl_Tmp[j].VertexIx]->C_Cl_ColAct < GRAPH->vertex[ATPl_Tmp[j].VertexIx]->C_Cl_ColReq) {
						GRAPH->vertex[ATPl_Tmp[j].VertexIx]->C_Cl_ColSet[GRAPH->vertex[ATPl_Tmp[j].VertexIx]->C_Cl_ColAct] = i;
						GRAPH->vertex[ATPl_Tmp[j].VertexIx]->C_Cl_ColAct++;
					}
				}
				l_ik++;
			}
		}
	}

	if (j<xx_NoOfCol) {
		for (j=0; j<G_S_coX->Card; j++) {
		}
	}

}
int _OUT_current_coloring(int p_model) {
	if (!Assignment_Found)
		return (0);
	_OUT_coloring2protfile();
	if (p_model)
		_OUT_coloring2file();
	return (1);
}

__inline void _h_fl_v_G_perm(int p_ix, int p_dis) {
	int i, p_x=0;
	if (!(p_ix<0) && (PERM(p_ix) != 2))
		PERM(p_ix) = 1;
	for (i=p_ix+1; i<CARD_(GRAPH); i++) {
		R_DIST_(GRAPH->Edge,p_ix,i,p_x);
		if (PERM(i) != 2)
			PERM(i) = Bool_cos(p_x<p_dis) * PERM(i);
	}
}
__inline void _h_fl_v_up_perm(int p_ix) {
	int i;
	if (!(p_ix<0))
		PERM(p_ix) = PERM(p_ix)*(PERM(p_ix) - 1);
	for (i=p_ix+1; i<CARD_(GRAPH); i++)
		PERM(i) = _MAX_(1,PERM(i));
}
void _h_SET_false(void) {
	int i;
	for (i=0; i<CARD_(G_S_Q0_); i++) {
		PERM__(i,G_S_Q0_) = 2;
		G_Not_Empty--;
	}
}

__inline int _O_calc_curr_LB(int p_dis) {
	int i, l_c=0;
	for (i=0; i<CARD_(G_S_Qx_); i++)
		l_c += iV_Color___(i,G_S_Qx_);
	l_c = 1 + (p_dis*(l_c-1));
	return (l_c);
}
__inline int _h_fl_calc_curr_LBs(int p_dis, int p_beg) {
	int i, l_lx=0, l_c=0;
	l_lx = _O_calc_curr_LB(p_dis);
	l_lx = l_lx + p_dis - 1;
	for (i=p_beg; i<CARD_(SetOfVertexes); i++)
		l_c += iV_Color__(i);
	l_c = 1 + (p_dis*(l_c-1));
	l_c += l_lx;
	return (l_c);
}
int _h_i_LB_const(int p_oix, int p_nix, int p_beg, int * p_oldlb) {
	int l_lb, l_varlb = *p_oldlb;
	l_lb=_h_fl_calc_curr_LBs(p_nix, p_beg);
	if (p_oix != p_nix)
		return (l_varlb < l_lb );
	l_varlb=CARD_(G_S_Q0_);
	l_lb=CARD_(G_S_Qx_) + (CARD_(SetOfVertexes) - p_beg);
	return (l_varlb < l_lb );
}
__inline int _h_fl_adm4set(int p_prio, int p_ix, int p_dis) {
	int i=0, p_x=0;
	while (i < CARD_(G_S_Qx_)) {
		R_PRIO_(GRAPH->Edge,p_ix,MEMBER_(i,G_S_Qx_),p_x);
		if (p_x<p_prio)
			return (0);
		R_DIST_(GRAPH->Edge,p_ix,MEMBER_(i,G_S_Qx_),p_x);
		if (p_x<p_dis)
			return (0);
		//  RAPP_(GRAPH->Edge,p_ix,MEMBER_(i,G_S_Qx_),p_x);   if(p_x<nx)  return(0);  
		i++;
	}
	return (1);
}

void _O_expand_cl(short * p_found, int p_prio, int p_ub, int * p_rg,
		int * p_varlb, int p_card, int p_dis) {
	int l_n, l_m=p_dis, l_xlb = _O_calc_curr_LB(p_dis);
	unsigned char L_done=0, L_noch=0;
	l_n=p_card;
	while ((l_n < CARD_(SetOfVertexes) ) && (L_noch || (!*p_found && (l_xlb < (p_ub+1))
			&& /*?????*/_h_i_LB_const(*p_rg, p_dis, l_n, p_varlb)))) {
		if ( (PERM(l_n) != 1) || !(_h_fl_adm4set(p_prio, l_n, l_m))) {
			l_n++;
			L_noch=1;
			continue;
		}
		MEMBER_(CARD_(G_S_Qx_)++,G_S_Qx_) = l_n;
		_h_fl_v_G_perm(l_n, l_m);
		_O_expand_cl(p_found, p_prio, p_ub, p_rg, p_varlb, l_n+1, p_dis);
		L_done=1;
		L_noch=0;
		l_n++;
	}
	if (*p_found)
		return;
	if (!L_done) {
		*p_varlb = _O_calc_curr_LB(p_dis);
		if (GRAPH->G_LB0 < *p_varlb) {
			GRAPH->G_LB[p_dis-1] = *p_varlb;
			_O_copy_Set(G_S_Q0_, G_S_Qx_);
			*p_rg = p_dis;
			GRAPH->G_LB0 = *p_varlb;
			if (p_ub < *p_varlb)
				*p_found = 1;
		}
	}
	if (CARD_(G_S_Qx_)> 0) {
		_h_fl_v_up_perm(G_S_Qx_->Chain[G_S_Qx_->Card-1]);
		CARD_(G_S_Qx_)--;
	}
}

void _GET_CL(int p_prio, int p_up, int * p_rg, int p_dis) {
	short *l_found= (short*) calloc(1, sizeof(short));
	int l_q= *p_rg, *l_x;
	*l_found=0;
	l_x = (int*) calloc(1, sizeof(int));
	*l_x = 0;
	_O_expand_cl(l_found, p_prio, p_up, p_rg, l_x, CARD_(G_S_Qx_), p_dis);
	if (l_q != *p_rg)
		GRAPH->G_LB[p_dis-1] = *l_x;
	free(l_x);
	free(l_found);
}

int _GET_m_LB(int p_mprio, int p_ncard, int p_mdis, int p_ldis) {
	int l_rg=1, i;
	CARD_(G_S_Qx_) = 0;
	CARD_(G_S_Q0_) = 0;
	GRAPH->G_LB0 = 0;
	if ((p_mdis<2)&&(0<p_ldis)) {
		GRAPH->G_LB[0]=0;
		_h_fl_v_up_perm(G_S_Qx_->Card-1);
		_GET_CL(p_mprio, p_ncard, &l_rg, 1);
	}
	if ((p_mdis<3)&&(1<p_ldis)) {
		CARD_(G_S_Qx_) = 0;
		GRAPH->G_LB[1]=0;
		_h_fl_v_up_perm(G_S_Qx_->Card-1);
		_GET_CL(p_mprio, p_ncard, &l_rg, 2);
	}
	if ((p_mdis<4)&&(2<p_ldis)) {
		CARD_(G_S_Qx_) = 0;
		GRAPH->G_LB[2]=0;
		_h_fl_v_up_perm(G_S_Qx_->Card-1);
		_GET_CL(p_mprio, p_ncard, &l_rg, 3);
	}
	CurrClNo++;
	for (i=0; i<CARD_(G_S_Q0_); i++) {
		VERTEX__(i,G_S_Q0_)->C_Cl_CurrCl = CurrClNo;
	}
	return (GRAPH->G_LB0);
}

void O_perm_ccl(void) {
	int i;
	for (i=0; i<G_CARD; i++)
		PERM(i) = 1;
}

void O_All_C_Left(void) {
	int i=0, j;
	for (j=0; j<G_CARD; j++)
		if (iV_Color_(j)) {
			iVertex_(i) = j;
			i++;
		}
	CARD_(SetOfVertexes) = i;
}

void C_Cl_Set(void) {
	int i=0, j=0, l_ik=0, l=0;
	SET_T * l_xset;
	l_xset = _a_SET(CARD_(G_S_Q0_));
	CARD_(l_xset) = CARD_(G_S_Q0_);
	for (i=0; i<CARD_(l_xset); i++) {
		MEMBER_(i,l_xset) = iV_Color___(i,G_S_Q0_);
		l_ik += iV_Color___(i,G_S_Q0_);
	}
	j += xx_NoOfCol;
	xx_NoOfCol += l_ik;
	while (j<xx_NoOfCol) {
		for (i=0; i<CARD_(l_xset); i++) {
			l_ik = GRAPH->vertex[iVertex_(G_S_Q0_->Chain[i])]->C_Cl_ColFix;
			if (MEMBER_(i,l_xset)) {
				AT_Set[j].VertexIx = iVertex__(i,G_S_Q0_);
				AT_Set[j].AT_LC_No = iV_Color___(i,G_S_Q0_) - MEMBER_(i,l_xset);
				if (l<l_ik) {
					AT_Set[j].AT_Color = GRAPH->vertex[iVertex_(G_S_Q0_->Chain[i])]->C_Cl_ColSet[l];
					AT_Set[j].AT_Color_found = 1;
				} else {
					AT_Set[j].AT_Color = -1;
					AT_Set[j].AT_Color_found = 0;
				}
				MEMBER_(i,l_xset)--;
				j++;
			}
		}
		l++;
	}
	_freeSET(l_xset);
}

void _PUT_card_L(int p_cmac_col) {
	int i;
	for (i=0; i<G_CARD; i++)
		iV_Color_(i) = _MIN_(p_cmac_col,iV_Color_Req(i));
}
void O_decompose_GRAPH(int p_mprio, int p_noofat, int p_mdis, int p_ldis,
		int p_mx_c_d) {
	xx_NoOfCol=0;
	O_perm_ccl();
	_PUT_card_L(p_mx_c_d);
	O_All_C_Left();
	G_Not_Empty = CARD_(SetOfVertexes);

	while (G_Not_Empty) {
		_GET_m_LB(p_mprio, p_noofat, p_mdis, p_ldis);
		_h_SET_false();
		C_Cl_Set();
	}
}

__inline int get_DIST(int i, int j) {
	int p=0, a=0;
	R_PRIO(GRAPH->Edge,iATSet_VIx(i),iATSet_VIx(j),p)
	;
	if (p<LOW_PRIO)
		return (0);
	R_DIST(GRAPH->Edge,iATSet_VIx(i),iATSet_VIx(j),a)
	;
	return (a);
}
void O_move_B(void) {
	int i;
	for (i=0; i<xx_NoOfCol; i++)
		if (AT_Set[i].AT_Color > 0)
			AT_Set[i].AT_Color -= GRAPH->G_LowerBound;
	GRAPH->G_UpperBound -= GRAPH->G_LowerBound;
	GRAPH->G_LowerBound = 0;
}

__inline void O_enable_All(int p_ix) {
	int l_ik;
	for (l_ik=1; l_ik<GRAPH->G_COLSPECTRUML+1; l_ik++)
		if (!GRAPH->ENAB[iATSet_VIx(p_ix)][l_ik])
			GRAPH->ENAB[iATSet_VIx(p_ix)][l_ik]=1;
}
__inline int _GET_c_ncc(int p_L, int p_R, int p_ix, int p_fx) {
	int i=0, l_a1=0, l_b2=0, l_cx2= -1, l_cx1= -1, l_L_fin2=0, l_L_fin1=0;
	ACT_Col = -1;
	if (p_L == p_fx) {
		for (i= p_fx; (i < p_R + 1)&&!l_L_fin2; i++) {
			switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
			case 1:
				l_cx2 = i;
				l_L_fin2=1;
				break;
			default:
				l_a1++;
				break;
			}
		}
		if (!l_L_fin2)
			return (-1);
		ACT_Col = l_cx2;
		return (l_a1);
	}
	if (p_R == p_fx) {
		for (i= p_fx; (p_L-1 < i)&&!l_L_fin2; i--) {
			switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
			case 1:
				l_cx2 = i;
				l_L_fin2=1;
				break;
			default:
				l_a1++;
				break;
			}
		}
		if (!l_L_fin2)
			return (-1);
		ACT_Col = l_cx2;
		return (l_a1);
	}
	for (i= p_fx; (i != (1-G_Act_d)*p_L + G_Act_d*p_R + 2*G_Act_d - 1)
			&&!l_L_fin2; i = i+ 2*G_Act_d - 1) {
		switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
		case 1:
			l_cx2 = i;
			l_L_fin2=1;
			break;
		default:
			l_a1++;
			break;
		}
	}
	if (!l_L_fin2)
		l_a1= p_R-p_L+1;
	else if (!l_a1) {
		ACT_Col = l_cx2;
		return (0);
	}
	G_Act_d = Bool_cos(G_Act_d);
	l_L_fin1 = 0;
	for (i= p_fx; (i != (1-G_Act_d)*p_L + G_Act_d*p_R + 2*G_Act_d - 1)
			&& !l_L_fin1; i = i+ 2*G_Act_d - 1) {
		switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
		case 1:
			l_cx1 = i;
			l_L_fin1=1;
			break;
		default:
			l_b2++;
			break;
		}
	}
	G_Act_d = Bool_cos(G_Act_d);
	if (!l_L_fin2&&!l_L_fin1)
		return (-1);
	if (!l_L_fin1)
		l_b2= p_R-p_L+1;
	else if (!l_b2) {
		ACT_Col = l_cx1;
		return (0);
	}
	if (l_a1 < l_b2) {
		ACT_Col = l_cx2;
		return (l_a1);
	}
	ACT_Col = l_cx1;
	return (l_b2);
}

__inline void _GET_c_R(int p_L, int p_R, int p_ix) {
	int i;
	AT_O_L.atno = -1;
	AT_O_L.atdist = 0;
	for (i= G_Act_d*p_L + (1-G_Act_d)*p_R; i != (1-G_Act_d)*p_L + G_Act_d*p_R
			+ 2*G_Act_d - 1; i = i+ 2*G_Act_d - 1)
		switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
		case -1:
			AT_O_L.atdist++;
			break;
		case 1:
			AT_O_L.atno = i;
			G_Act_d = Bool_cos(G_Act_d);
			return;
			break;
		default:
			break;
		}
}

__inline void _GET_c_L(int p_L, int p_ix) {
	int i;
	AT_O_L.atno = GRAPH->G_COLSPECTRUML;
	AT_O_L.atdist = 0;
	for (i=p_L; _MAX_(0,GRAPH->G_UpperBound-ColorSpectrum_L+1)<=i; i--)
		switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
		case -1:
			AT_O_L.atdist++;
			break;
		case 1:
			AT_O_L.atno = i;
			return;
			break;
		default:
			break;
		}
}
__inline void _GET_c_bb(int p_L, int p_ix) {
	int i;
	AT_O_R.atno = -1;
	AT_O_R.atdist = 0;
	for (i=p_L; i<_MIN_(GRAPH->G_COLSPECTRUML,GRAPH->G_LowerBound+ColorSpectrum_L); i++)
		switch (GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) {
		case -1:
			AT_O_R.atdist++;
			break;
		case 1:
			AT_O_R.atno = i;
			return;
			break;
		default:
			break;
		}
}

__inline int _GET_d_l(int p_ix, int n) {
	int l_L = get_DIST(p_ix, n);
	l_L = iATSetColor(n) - l_L;
	return (l_L);
}
__inline int _GET_d_r(int p_ix, int n) {
	int l_L = get_DIST(p_ix, n);
	l_L = iATSetColor(n) + l_L;
	return (l_L);
}
__inline void _O_c_down(int p_L, int p_R, int p_ix) {
	int i;
	for (i=p_L+1; i<p_R; i++) {
		if ((i<0)||(GRAPH->G_COLSPECTRUML - 1 < i))
			continue;
		GRAPH->ENAB[iATSet_VIx(p_ix)][i+1] =
		GRAPH->ENAB[iATSet_VIx(p_ix)][i+1] * (1 - GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) / 2;
		if (GRAPH->G_COLSPECTRUML> i+ColInterval_L)
			GRAPH->ENAB[iATSet_VIx(p_ix)][i+1+ColInterval_L] =
			GRAPH->ENAB[iATSet_VIx(p_ix)][i+1+ColInterval_L] * (1 - GRAPH->ENAB[iATSet_VIx(p_ix)][i+1+ColInterval_L]) / 2;
		if (i-ColInterval_L > 0)
			GRAPH->ENAB[iATSet_VIx(p_ix)][i+1-ColInterval_L] =
			GRAPH->ENAB[iATSet_VIx(p_ix)][i+1-ColInterval_L] * (1 - GRAPH->ENAB[iATSet_VIx(p_ix)][i+1-ColInterval_L]) / 2;
	}
}
__inline void _O_c_(int p_L, int p_R, int p_ix) {
	int i;
	for (i=p_L+1; i<p_R; i++) {
		if ((i<0)||(GRAPH->G_COLSPECTRUML - 1 < i))
			continue;
		GRAPH->ENAB[iATSet_VIx(p_ix)][i+1] =
		GRAPH->ENAB[iATSet_VIx(p_ix)][i+1] * (1 - GRAPH->ENAB[iATSet_VIx(p_ix)][i+1]) / 2;
	}
}
__inline void _hfl_v_2v(int p_ix) {
	int i=0, l, p_x;
	while (i<G_S_Q0_->Card) {
		l = _GET_d_l(p_ix, G_S_Q0_->Chain[i]);
		p_x = _GET_d_r(p_ix, G_S_Q0_->Chain[i]);
		_O_c_(l, p_x, p_ix);
		i++;
	}
}
int _hp_i_rhc(int p_ix) {
	int l_L, l_R, l_iy;
	O_enable_All(p_ix);
	_hfl_v_2v(p_ix);
	_GET_c_R(GRAPH->G_LowerBound,GRAPH->G_UpperBound,p_ix);
	if (AT_O_L.atno >= 0)
		return (0);
	_GET_c_L(GRAPH->G_LowerBound,p_ix);
	l_L=AT_O_L.atno;
	_GET_c_bb(GRAPH->G_UpperBound,p_ix);
	l_R=AT_O_R.atno;
	if (l_L != GRAPH->G_COLSPECTRUML) {
		if (l_R != -1) {
			l_iy = _MIN_((GRAPH->G_LowerBound - l_L - AT_O_L.atdist),(l_R - GRAPH->G_UpperBound - AT_O_R.atdist));
			if ((GRAPH->G_LowerBound - l_L - AT_O_L.atdist) < (l_R
					- GRAPH->G_UpperBound - AT_O_R.atdist))
				l_R = GRAPH->G_UpperBound + 1 - l_L;
			else
				l_R = l_R + 1 - GRAPH->G_LowerBound;
			if (l_R>ColorSpectrum_L)
				return (0);
			return (l_iy);
		} else {
			l_iy = GRAPH->G_LowerBound - l_L - AT_O_L.atdist;
			l_R = GRAPH->G_UpperBound + 1 - l_L;
			if (l_R>ColorSpectrum_L)
				return (0);
			return (l_iy);
		}
	} else {
		if (l_R != -1) {
			l_iy = l_R - GRAPH->G_UpperBound - AT_O_R.atdist;
			l_R = l_R + 1 - GRAPH->G_LowerBound;
			if (l_R>ColorSpectrum_L)
				return (0);
			return (l_iy);
		} else {
			return (0);
		}
	}
}
int _hp_i_rhl(int p_ix) {
	int l_iy;
	iATSetColor(p_ix) = -1;
	l_iy = iATSetColor(G_S_Q0_->Chain[G_S_Q0_->Card-1]);
	if (l_iy < 0)
		return (-1);
	O_enable_All(p_ix);
	_hfl_v_2v(p_ix);

	l_iy = _GET_c_ncc(GRAPH->G_LowerBound,GRAPH->G_UpperBound,p_ix,l_iy);
	return (l_iy);
}

void _hp_v_2r(int p_v_ix) {
	O_enable_All(p_v_ix);
	iATSetColor(p_v_ix) = -1;
	_hfl_v_2v(p_v_ix);
	_GET_c_L(GRAPH->G_LowerBound,p_v_ix);
	_GET_c_bb(GRAPH->G_UpperBound,p_v_ix);
	if (AT_O_L.atno < GRAPH->G_COLSPECTRUML) {
		if (AT_O_R.atno > -1) {
			if ((GRAPH->G_LowerBound - AT_O_L.atno - AT_O_L.atdist)
					< (AT_O_R.atno - GRAPH->G_UpperBound - AT_O_R.atdist)) {
				iATSetColor(p_v_ix) = AT_O_L.atno;
				GRAPH->G_LowerBound = _MIN_(GRAPH->G_LowerBound,AT_O_L.atno);
				iV_Color_Tmp(iATSet_VIx(p_v_ix))--;
				G_Col_Inside = 1 && ((GRAPH->G_UpperBound - AT_O_L.atno + 1)
						< ColorSpectrum_L);
				return;
			}
			iATSetColor(p_v_ix) = AT_O_R.atno;
			GRAPH->G_UpperBound = _MAX_(GRAPH->G_UpperBound,AT_O_R.atno);
			iV_Color_Tmp(iATSet_VIx(p_v_ix))--;
			G_Col_Inside = 1 && ((AT_O_R.atno - GRAPH->G_LowerBound)
					< ColorSpectrum_L);
			return;
		} else {
			iATSetColor(p_v_ix) = AT_O_L.atno;
			GRAPH->G_LowerBound = _MIN_(GRAPH->G_LowerBound,AT_O_L.atno);
			iV_Color_Tmp(iATSet_VIx(p_v_ix))--;
			G_Col_Inside = 1 && ((GRAPH->G_UpperBound - AT_O_L.atno + 1)
					< ColorSpectrum_L);
			return;
		}
	}
	iATSetColor(p_v_ix) = AT_O_R.atno;
	GRAPH->G_UpperBound = _MAX_(GRAPH->G_UpperBound,AT_O_R.atno);
	iV_Color_Tmp(iATSet_VIx(p_v_ix))--;
	G_Col_Inside = 1 && ((AT_O_R.atno - GRAPH->G_LowerBound + 1)
			< ColorSpectrum_L);
}
int _hp_L_x3r(int p_v_ix) {
	O_enable_All(p_v_ix);
	iATSetColor(p_v_ix) = -1;
	_hfl_v_2v(p_v_ix);
	_GET_c_R(GRAPH->G_LowerBound,GRAPH->G_UpperBound,p_v_ix);
	if (AT_O_L.atno < 0)
		return (0);
	iATSetColor(p_v_ix) = AT_O_L.atno;
	iV_Color_Tmp(iATSet_VIx(p_v_ix))--;
	return (1);
}

unsigned char read_GRAPH(void) {
	int i, l_icile=0;
	char *l_xline, *l_strx, l_xstr[L_N_Mx+1];
	FILE *fp;

	NoOfAtoms = 0;

	l_xline = (char*) calloc(L_L_Mx+1,sizeof(char));
	if ((fp =fopen(GraphFile, "r")) == NULL) {
		printf("ERROR:: opening file %s", GraphFile);;
		return (0);
	}

	while (fgets(l_xline, L_L_Mx, fp) != NULL) {
		if (l_xline[0] != '!')
			l_icile++;
	}

	rewind(fp);
	GRAPH = _a_GRAPH(l_icile,Max_ATCell);
	GRAPH->Card = 0;
	GRAPH->G_AllFixColCard = 0;

	while (fgets(l_xline, L_L_Mx, fp) != NULL) {
		if (l_xline[0] != '!') {

			jpb_strAsubst(l_xline, "  ", " ");
			jpb_strAsubst(l_xline, "\t\t", "\t");

			VERTEX(GRAPH->Card)->C_Cl_SCl_Name[0] = '\0';
			VERTEX(GRAPH->Card)->C_Cl_LettId = '0';
			sscanf(l_xline, "%s", VERTEX(GRAPH->Card)->C_Cl_SCl_Name);
			l_strx = l_xline;
			l_strx = l_strx + strlen(VERTEX(GRAPH->Card)->C_Cl_SCl_Name)+1;
			sscanf(l_strx, "%s", l_xstr);
			l_strx = l_strx + strlen(l_xstr)+1;
			sscanf(l_xstr, "%d", &VERTEX(GRAPH->Card)->C_Cl_No);

			sscanf(l_strx, "%s", l_xstr);
			l_strx = l_strx + strlen(l_xstr)+1;
			sscanf(l_xstr, "%d", &VERTEX(GRAPH->Card)->C_Cl_waste);

			sscanf(l_strx, "%s", l_xstr);
			l_strx = l_strx + strlen(l_xstr)+1;
			sscanf(l_xstr, "%d", &iV_Color_Req(GRAPH->Card));
			VERTEX(GRAPH->Card)->C_Cl_ColFix=0;
			VERTEX(GRAPH->Card)->C_Cl_ColAct=0;

			if (!(iV_Color_Req(GRAPH->Card)> 0)) {
				continue;
			}
			NoOfAtoms += iV_Color_Req(GRAPH->Card);
			if (1 == sscanf(l_strx, "%s", l_xstr)) {
				l_strx = l_strx + strlen(l_xstr)+1;
				if (1 == sscanf(l_xstr, "%d", &VERTEX(GRAPH->Card)->C_Cl_ColFix)) {
					if (VERTEX(GRAPH->Card)->C_Cl_ColFix) {
						GRAPH->G_AllFixColCard += VERTEX(GRAPH->Card)->C_Cl_ColFix;
						for (i=0; i<VERTEX(GRAPH->Card)->C_Cl_ColFix; i++) {
							if (1 != sscanf(l_strx, "%s", l_xstr)) {
								return (0);
							}
							l_strx = l_strx + strlen(l_xstr)+1;
							sscanf(l_xstr, "%d", &VERTEX(GRAPH->Card)->C_Cl_ColSet[i]);
							VERTEX(GRAPH->Card)->C_Cl_ColSet[i] -= G_Col_Offset;
						}
					}
				}
			}
			VERTEXID(GRAPH->Card) = GRAPH->Card;

			VERTEX(GRAPH->Card)->C_Cl_LettId = (char) ((int)'A' + VERTEX(GRAPH->Card)->C_Cl_No - 1);
			iV_Color_Tmp(GRAPH->Card) = iV_Color_Req(GRAPH->Card);

			GRAPH->Card++;
			///? Card of all nodes in GraphFile
		}
	}
	free(l_xline);
	fclose(fp);
	return (1);
}

void _a_COL_set(void) {
	int i;
	GRAPH->G_COLSPECTRUML = 2*(ColorSpectrum_L+_MAX_(Max_DIST,S_DIST)) + 5;

	GRAPH->ENAB = (PTR(short) *) calloc(GRAPH->Card,sizeof(PTR(short)));
	Error_Msg__ALLOC(GRAPH->ENAB,"AllocErr  by alloc GRAPH->ENAB",exit(0);)
	for (i=0; i<GRAPH->Card; i++) {
		GRAPH->ENAB[i] = (short *) calloc(GRAPH->G_COLSPECTRUML+1,sizeof(short));
		Error_Msg__ALLOC(GRAPH->ENAB[i],"AllocErr by alloc GRAPH->ENAB[]",exit(0);)
	}
}

void _h_init_CR(void) {
	int l_v_ix = -1, l_v_jx = -1;
	for (l_v_ix=0; l_v_ix<GRAPH->Card; l_v_ix++)
		for (l_v_jx=l_v_ix; l_v_jx<GRAPH->Card; l_v_jx++) {
			strcpy(GRAPH->Edge[l_v_ix][l_v_jx],COMPREL_INITVAL);
			strcpy(GRAPH->Edge[l_v_jx][l_v_ix],COMPREL_INITVAL);
		}
}
void _h_init_GRAPH(void) {
	int l_v_ix = -1, l_ik;
	_a_COL_set();
	CurrClNo=0;
	for (l_v_ix=0; l_v_ix<GRAPH->Card; l_v_ix++) {
		GRAPH->Perm[l_v_ix] = 1;
		GRAPH->vertex[l_v_ix]->C_Cl_CurrCl = 0;
	}
	_h_init_CR();

	for (l_v_ix=0; l_v_ix<GRAPH->Card; l_v_ix++) {
		GRAPH->ENAB[l_v_ix][0] = GRAPH->G_COLSPECTRUML;
		for (l_ik=0; l_ik<GRAPH->G_COLSPECTRUML; l_ik++)
			GRAPH->ENAB[l_v_ix][l_ik+1] = 0;
	}
}

void _h_COL_disa(void) {
	int l_cix, l_if;
	for (l_cix=0; l_cix<GRAPH->Card; l_cix++)
		for (l_if=0; l_if<ColorsCard; l_if++)
			GRAPH->ENAB[l_cix][Colors[l_if]-G_Col_Offset+1] = 1;
	for (l_cix=0; l_cix<GRAPH->Card; l_cix++)
		for (l_if=0; l_if<GRAPH->G_COLSPECTRUML; l_if++)
			GRAPH->ENAB[l_cix][l_if+1] -= 1;
}

void _h_V_colord(void) {
	int i, j;
	C_VERTEX_T * l_vx;
	for (i=0; i<GRAPH->Card; i++) {
		for (j=i+1; j<GRAPH->Card; j++) {
			if (iV_Color_Req(i) < iV_Color_Req(j)) {
				l_vx = VERTEX(j);
				VERTEX(j) = VERTEX(i);
				VERTEX(i) = l_vx;
			}
		}
		VERTEXID(i) = i;
	}
}
void _h_SET_nord(int p_iile, SET_T *p_set) {
	int i=0;
	p_set->Card=p_iile;
	while (i<p_set->Card) {
		p_set->Chain[i]=i;
		i++;
	}
}

int get_vertex_id(int ccl_x, char *scl_x) {
	int i;
	for (i=0; i<GRAPH->Card; i++)
		if ( (GRAPH->vertex[i]->C_Cl_No != ccl_x) || strcmp(scl_x,
				GRAPH->vertex[i]->C_Cl_SCl_Name) )
			continue;
		else
			return (GRAPH->vertex[i]->C_Cl_VId);
	return (-1);
}

void put_SON_CR(int p_prio, int n_prio, int p_dis, int p_tr, int p_trx) {
	int i, j, l_ik, l_co1=100, l_co2=100, l_ic2=0, l_ic1=0;

	for (i=0; i<GRAPH->Card; i++) {
		for (j=0; j<GRAPH->Card; j++)

		{
			if (!(j != i))
				continue;
			R_PRIO(GRAPH->Edge,i,j,l_co1)
			;

			if (l_co1 < p_prio)
				continue;

			for (l_ik=0; l_ik<GRAPH->Card; l_ik++) {
				if (!((l_ik != i)&&(l_ik != j)))
					continue;

				R_PRIO(GRAPH->Edge,i,l_ik,l_co1)
				;
				R_PRIO(GRAPH->Edge,l_ik,i,l_co2)
				;
				l_co1 = _MAX_(l_co1,l_co2);
				if (l_co1 < p_prio)
					continue;

				R_PRIO(GRAPH->Edge,j,l_ik,l_co1)
				;
				R_PRIO(GRAPH->Edge,l_ik,j,l_co2)
				;
				l_co1 = _MAX_(l_co1,l_co2);
				if (!(l_co1 < n_prio))
					continue;
				R_IT1(GRAPH->Edge,j,l_ik,l_ic2)
				;
				R_IT1(GRAPH->Edge,l_ik,j,l_ic1)
				;
				l_ic2 =_MAX_(l_ic2,l_ic1);
				if (!(p_tr < l_ic2))
					continue;

				l_ic2 = _MIN_(IT_MAX,l_ic2+p_trx);
				W_IT1(GRAPH->Edge,j,l_ik,l_ic2);
				W_IT1(GRAPH->Edge,l_ik,j,l_ic2);
				W_DIST(GRAPH->Edge,j,l_ik,p_dis);
				W_DIST(GRAPH->Edge,l_ik,j,p_dis);
				W_PRIO(GRAPH->Edge,j,l_ik,n_prio);
				W_PRIO(GRAPH->Edge,l_ik,j,n_prio);
			}
		}
	}
}

void put_SCL_CR(int p_cpr, int p_dis, int p_cval) {
	int l_a1=0, l_v_ix = -1;
	for (l_v_ix=0; l_v_ix<GRAPH->Card; l_v_ix++) {
		R_PRIO(GRAPH->Edge,l_v_ix,l_v_ix,l_a1)
		;
		if (!(l_a1 < p_cpr))
			continue;
		W_PRIO(GRAPH->Edge,l_v_ix,l_v_ix,p_cpr);
		W_DIST(GRAPH->Edge,l_v_ix,l_v_ix,p_dis);
		W_IT1(GRAPH->Edge,l_v_ix,l_v_ix,p_cval);
	}
}

void put_CCL_CR(int p_npr, int p_dis, int p_cval) {
	int l_a1=0, l_b2=0, l_v_ix = -1, l_v_jx = -1;

	for (l_v_ix=0; l_v_ix<GRAPH->Card; l_v_ix++)
		for (l_v_jx=l_v_ix+1; l_v_jx<GRAPH->Card; l_v_jx++) {
			R_PRIO(GRAPH->Edge,l_v_ix,l_v_jx,l_a1)
			;
			if (!(l_a1 < p_npr))
				continue;

			if ( !strcmp(GRAPH->vertex[l_v_ix]->C_Cl_SCl_Name,GRAPH->vertex[l_v_jx]->C_Cl_SCl_Name) ) {
				W_PRIO(GRAPH->Edge,l_v_ix,l_v_jx,p_npr);
				W_PRIO(GRAPH->Edge,l_v_jx,l_v_ix,p_npr);
				W_DIST(GRAPH->Edge,l_v_ix,l_v_jx,p_dis);
				W_DIST(GRAPH->Edge,l_v_jx,l_v_ix,p_dis);
				W_IT1(GRAPH->Edge,l_v_ix,l_v_jx,p_cval);
				W_IT1(GRAPH->Edge,l_v_jx,l_v_ix,p_cval);
			}
		}
}

unsigned char read_NEIGHBOURS(int p_np, int mind, int itnmin) {
	char l_lst[L_L_Mx+1], l_xxst[5], xscl_lname[L_N_Mx+1], scl_lname[L_N_Mx+1];
	FILE *fp;
	int l_pr=0, l_icno= 0, l_jcno=0, l_v_ix = -1, l_v_jx = -1, l_a1, l_b2;

	xscl_lname[0]='\0';

	if ((fp =fopen(NeigFile, "r")) == NULL) {
		printf("ERROR:: opening file %s", NeigFile);;
		return (0);
	}
	while (fgets(l_lst, L_L_Mx, fp) != NULL) {
		if (l_lst[0] == '!')
			continue;
		sscanf(l_lst, "%s %s %d", l_xxst, scl_lname, &l_jcno);
		if ((l_xxst[0] == 'N')) {
			l_v_jx = -1;
			l_v_jx = get_vertex_id(l_jcno, scl_lname);
			if (l_v_jx<0) {
				printf("!!ERROR:: cell not found, file: %s, cell %s %d \n",
						NeigFile, scl_lname, l_jcno);

				return (0);
			}
			if (l_v_ix != l_v_jx) {
				R_PRIO(GRAPH->Edge,l_v_ix,l_v_jx,l_pr)
				;
				if (!(l_pr < p_np))
					continue;

				W_PRIO(GRAPH->Edge,l_v_ix,l_v_jx,p_np);
				W_PRIO(GRAPH->Edge,l_v_jx,l_v_ix,p_np);
				W_DIST(GRAPH->Edge,l_v_ix,l_v_jx,mind);
				W_DIST(GRAPH->Edge,l_v_jx,l_v_ix,mind);
				R_IT1(GRAPH->Edge,l_v_ix,l_v_jx,l_a1)
				;
				R_IT1(GRAPH->Edge,l_v_jx,l_v_ix,l_b2)
				;
				l_a1 = _MAX_(l_a1,l_b2);
				l_a1 += itnmin;
				l_a1 = _MIN_(l_a1,IT_MAX);
				W_IT1(GRAPH->Edge,l_v_ix,l_v_jx,l_a1);
				W_IT1(GRAPH->Edge,l_v_jx,l_v_ix,l_a1);
			} else {
			}
		} else {
			if (l_xxst[0] != 'C') {
				printf("!!ERROR:: file: %s\n", NeigFile);
				return (0);
			} else {
				if ( !strcmp(xscl_lname, scl_lname) && (l_icno==l_jcno))
					continue;
				strcpy(xscl_lname, scl_lname);
				l_icno = l_jcno;
				l_v_ix = -1;
				l_v_ix = get_vertex_id(l_icno, xscl_lname);
				if (l_v_ix<0) {
					printf("!!ERROR:: cell not found: file: %s: cell %s %d \n",
							NeigFile, xscl_lname, l_icno);

					return (0);
				}
			}
		}
	}
	fclose(fp);
	return (1);
}

unsigned char read_InterferenceMatrix(int p_np, int p_itd) {
	char l_xline[L_L_Mx+1], l_cstr1[L_N_Mx+1], l_cstr2[L_N_Mx+1], l_xs[8],
			l_xsl2[L_N_Mx+1];
	FILE *fp;
	int l_ino, l_kno=0, l_v_ix = -1, l_v_jx = -1, l_ixx=0, l_noif, l_coi,
			l_iix=0;
	double l_xco_a, l_xco_t, l_xad_a, l_xad_t, l_aval = 0.0, l_tval = 0.0;

	if ((fp =fopen(IntfFile, "r")) == NULL) {
		printf("ERROR:: opening file: %s", IntfFile);;
		return (0);
	}

	while (fgets(l_xline, 300, fp) != NULL) {
		if ((l_xline[0] != 'S') && (l_xline[0] != 'I'))
			continue;
		if (l_xline[0] != 'S') {
			sscanf(l_xline, "%s %s %d %lf %lf %lf %lf %s", l_xs, l_xsl2,
					&l_ixx, &l_xco_a, &l_xco_t, &l_xad_a, &l_xad_t, l_cstr1);
			l_kno = ( (int)l_cstr1[strlen(l_cstr1)-1] - (int)'A' ) + 1;
			l_cstr1[strlen(l_cstr1)-1] = '\0';
			l_v_ix = -1;
			l_v_ix = get_vertex_id(l_kno, l_cstr1);
			if (l_v_ix<0) {
				continue;
			}
			R_PRIO(GRAPH->Edge,l_v_jx,l_v_ix,l_coi)
			;
			if (l_coi > p_np)
				continue;
			if (Use_TRAF>1) {
				l_xco_t = l_xco_a;
				l_xad_t = l_xad_a;
			}

			if (l_tval > 0.0) {

				l_coi=0;
				l_xco_t = l_xco_t/l_tval;
				l_coi = _MIN_(IT_MAX,_MAX_(0,(int) ceil(l_xco_t * ((double)IT1_MAX) )));
			} else {
				l_coi = 0;
			}

			R_IT1(GRAPH->Edge,l_v_ix,l_v_jx,l_iix)
			;
			l_coi = _MAX_(l_iix,l_coi);
			W_IT1(GRAPH->Edge,l_v_ix,l_v_jx,l_coi);
			W_IT1(GRAPH->Edge,l_v_jx,l_v_ix,l_coi);
			W_PRIO(GRAPH->Edge,l_v_jx,l_v_ix,p_np);
			W_PRIO(GRAPH->Edge,l_v_ix,l_v_jx,p_np);
			W_DIST(GRAPH->Edge,l_v_jx,l_v_ix,p_itd);
			W_DIST(GRAPH->Edge,l_v_ix,l_v_jx,p_itd);

		} else {
			sscanf(l_xline, "%s %s %d %lf %lf %d %s", l_xs, l_xsl2, &l_ixx,
					&l_aval, &l_tval, &l_noif, l_cstr2);
			if (Use_TRAF>1)
				l_tval = l_aval;

			if ((l_tval > 0.0) && (l_noif > 0)) {
				l_ino = ( (int)l_cstr2[strlen(l_cstr2)-1] - (int)'A' ) + 1;
				l_cstr2[strlen(l_cstr2)-1] = '\0';
				l_v_jx = -1;
				l_v_jx = get_vertex_id(l_ino, l_cstr2);
				if (l_v_jx<0) {
					fclose(fp);
					return (0);
				}
			} else {
				printf(
						"ERROR:: AREA/TRAFFIC value %lf or number of interferers %d not valid, file: %s, l_lst:\n",
						l_tval, l_noif, IntfFile);
				printf("       %s\n", l_xline);
				fclose(fp);
				return (0);
			}
		}
	}
	fclose(fp);
	return (1);
}

void _h_sym_cr(void) {
	int l_a1=0, l_b2=0, l_v_ix = -1, l_v_jx = -1;
	for (l_v_ix=0; l_v_ix<GRAPH->Card; l_v_ix++)
		for (l_v_jx=l_v_ix; l_v_jx<GRAPH->Card; l_v_jx++) {
			l_a1=0;
			l_b2=0;
			R_IT1(GRAPH->Edge,l_v_ix,l_v_jx,l_a1)
			;
			R_IT1(GRAPH->Edge,l_v_jx,l_v_ix,l_b2)
			;
			if (l_a1 < l_b2)
				W_IT1(GRAPH->Edge,l_v_ix,l_v_jx,l_b2);
			if (l_b2 < l_a1)
				W_IT1(GRAPH->Edge,l_v_jx,l_v_ix,l_a1);
		}
}

short read_ITM(int p_prn, int p_ittr) {
	if (!read_InterferenceMatrix(p_prn, p_ittr))
		return (0);
	_h_sym_cr();
	return (1);
}

void h_def_mset(void) {
	int i, j, l_n1=0, l_n2=0;
	double l_nnx=0.0;
	G_ToComp = 0;
	for (i=0; i<CARD_(GRAPH); i++)
		for (j=i; j<CARD_(GRAPH); j++) {
			R_IT1(GRAPH->Edge,i,j,l_n1)
			;
			R_IT1(GRAPH->Edge,j,i,l_n2)
			;
			if (l_n1+l_n2) {
				G_ToComp++;
			}
		}

	l_nnx = (double) G_ToComp;

	l_n1 = (int) ((l_nnx-1) / 100);

	l_n1 = 1 + (100-QUALITY)*l_n1;
	l_n1 = _MAX_(l_n1,1);

	G_Min_SET = _MIN_(G_ToComp, l_n1);
	
	//TODO: This is how Rahul output progress to the console. We need to fine the right place, since this seems unlikely to be correct
	printf("CoIT1 = %d, MAX_SET = %d, MIN_SET = %d\n", G_ToComp, l_n1,
			G_Min_SET);

}

int _i_get_P_CO(int p_ile, int p_pr, int p_tr) {
	int l_card=0, i, j, l_co1=100, l_co2=100;
	for (i=0; (i<CARD_(GRAPH))&&(l_card<p_ile); i++) {
		for (j=i; (j<CARD_(GRAPH))&&(l_card<p_ile); j++) {
			R_PRIO(GRAPH->Edge,i,j,l_co1)
			;
			if (l_co1 != p_pr)
				continue;
			R_IT1(GRAPH->Edge,i,j,l_co1)
			;

			if (p_tr != l_co1)
				continue;
			W_PRIO(GRAPH->Edge,i,j,TMP_PRIO);
			W_PRIO(GRAPH->Edge,j,i,TMP_PRIO);
			l_card++;
		}
	}
	return (l_card);
}

int _h_put_tout(int p_ile, int p_pr, int p_od, int p_nd) {
	int i, j, l_cm=0, l_co1=100, l_co2=100;
	for (i=0; (i<CARD_(GRAPH))&&(l_cm<p_ile); i++) {
		for (j=i+1; (j<CARD_(GRAPH))&&(l_cm<p_ile); j++) {
			R_PRIO(GRAPH->Edge,i,j,l_co1)
			;
			if (l_co1 != p_pr)
				continue;
			R_DIST(GRAPH->Edge,i,j,l_co1)
			;
			R_DIST(GRAPH->Edge,j,i,l_co2)
			;
			l_co1=_MIN_(l_co1,l_co2);
			if (l_co1 != p_od)
				continue;
			W_DIST(GRAPH->Edge,i,j,p_nd);
			W_DIST(GRAPH->Edge,j,i,p_nd);
			W_PRIO(GRAPH->Edge,i,j,TMP_PRIO);
			W_PRIO(GRAPH->Edge,j,i,TMP_PRIO);
			l_cm++;
		}
	}
	return (l_cm);
}
void _h_put_out(int p_ile, int p_np) {
	int i, j, l_co1=100, l_cm=0;
	;
	for (i=0; (i<CARD_(GRAPH))&&(l_cm<p_ile); i++) {
		for (j=i; (j<CARD_(GRAPH))&&(l_cm<p_ile); j++) {
			R_PRIO(GRAPH->Edge,i,j,l_co1)
			;
			if (l_co1 != TMP_PRIO)
				continue;
			W_PRIO(GRAPH->Edge,i,j,p_np);
			W_PRIO(GRAPH->Edge,j,i,p_np);
			l_cm++;
		}
	}
}

int _h_rem_duo(int p_ile, int p_np, int p_dis) {
	int i, j, l_co1=100, l_cm=0;
	;
	for (i=0; (i<CARD_(GRAPH))&&(l_cm<p_ile); i++) {
		for (j=i; (j<CARD_(GRAPH))&&(l_cm<p_ile); j++) {
			R_PRIO(GRAPH->Edge,i,j,l_co1)
			;
			if (l_co1 != TMP_PRIO)
				continue;
			W_DIST(GRAPH->Edge,i,j,p_dis);
			W_DIST(GRAPH->Edge,j,i,p_dis);
			W_PRIO(GRAPH->Edge,i,j,p_np);
			W_PRIO(GRAPH->Edge,j,i,p_np);
			l_cm++;
		}
	}
	return (l_cm);
}

void _h_put_back(int p_dm, int p_po, int p_dis) {
	int i, j, l_co1=100;
	for (i=0; (i<CARD_(GRAPH))&&(0<p_dm); i++) {
		for (j=i+1; (j<CARD_(GRAPH))&&(0<p_dm); j++) {
			R_PRIO(GRAPH->Edge,i,j,l_co1)
			;
			if (l_co1 != TMP_PRIO)
				continue;
			W_PRIO(GRAPH->Edge,i,j,p_po);
			W_PRIO(GRAPH->Edge,j,i,p_po);
			W_DIST(GRAPH->Edge,i,j,p_dis);
			W_DIST(GRAPH->Edge,j,i,p_dis);
		}
	}
}

double DET(double A, double B, double a, double b) {
	return ((A*b)-(B*a));
}
double V_l(double ux, double uy) {
	return (sqrt(P_2(ux)+P_2(uy)));
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
	x = _MAX_(x,-1.0);
	x = _MIN_(x,1.0);
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
	D = _MAX_(DIS_(p0x,p0y,p1x,p1y),DIS_(p0x,p0y,p2x,p2y));
	return (D / al );
}

unsigned char IsCut_HL(double az1, double az2, double p1x, double p1y,
		double q1x, double q1y, double p2x, double p2y, double q2x, double q2y) {
	double A=A_L(p1x, p1y, q1x, q1y), B=B_L(p1x, p1y, q1x, q1y), C=C_L(p1x,
			p1y, q1x, q1y), a=A_L(p2x, p2y, q2x, q2y), b=
			B_L(p2x, p2y, q2x, q2y), c=C_L(p2x, p2y, q2x, q2y), D=DET(A, B, a,
			b), X0, Y0;

	if (D != 0.0) {
		X0=LCut_x(A, B, C, a, b, c), Y0=LCut_y(A, B, C, a, b, c);
	}
	if (QEQ(az1,az2))
		return (0);
	if (SPV_(q1x-p1x, q1y-p1y, (X0 - p1x ), (Y0 - p1y)) < 0.0)
		return (0);
	if (SPV_(q2x-p2x, q2y-p2y, (X0 - p2x ), (Y0 - p2y)) < 0.0)
		return (0);
	return (1);
}

double sec_point_x(double lx, double ly, double az) {
	if (!QEQ(az,0.0) && !QEQ(az,J_PI_)) {
		if (az < J_PI_) {
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
	if (QEQ(az,J_PI_))
		return (ly - 1.0);

	if (!QEQ(az,(J_PI_/2.0)) && !QEQ(az,(3.0*J_PI_/2.0))) {
		if (az < (J_PI_/2.0))
			return (ly + tan((J_PI_/2.0) - az));
		if (az < J_PI_)
			return (ly - tan(az - (J_PI_/2.0)));
		if (az < (3.0*J_PI_/2.0))
			return (ly - tan((3.0*J_PI_/2.0) - az));
		return (ly + tan(az - (3.0*J_PI_/2.0)));
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
		printf("ERROR::\n");
	ALPH = 1.0+AV_((p1x-long1), (p1y-lat1), (p2x-long2), (p2y-lat2));

	if ( BETP_(long1,lat1,long2,lat2,p2_60l_x,p2_60l_y,p2_60r_x,p2_60r_y) ||
	BETP_(long2,lat2,long1,lat1,p1_60l_x,p1_60l_y,p1_60r_x,p1_60r_y)) {
		D = D / ALPH;

		if (D < 0.0)
			printf("ERROR::\n");

		return (D);
	}

	if (IsCut_HL((azimuth1-(J_PI_/3.0)), (azimuth2-(J_PI_/3.0)), long1, lat1,
			p1_60l_x, p1_60l_y, long2, lat2, p2_60l_x, p2_60l_y))
		MDIS = JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),
		Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),
		long1,lat1,long2,lat2,ALPH);

	if (IsCut_HL((azimuth1-(J_PI_/3.0)), (azimuth2+(J_PI_/3.0)), long1, lat1,
			p1_60l_x, p1_60l_y, long2, lat2, p2_60r_x, p2_60r_y))
		MDIS
				= _MIN_(MDIS,
						JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),
								Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),
								long1,lat1,long2,lat2,ALPH));

	if (IsCut_HL((azimuth1+(J_PI_/3.0)), (azimuth2+(J_PI_/3.0)), long1, lat1,
			p1_60r_x, p1_60r_y, long2, lat2, p2_60r_x, p2_60r_y))
		MDIS
				= _MIN_(MDIS,
						JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),
								Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),
								long1,lat1,long2,lat2,ALPH));

	if (IsCut_HL((azimuth1+(J_PI_/3.0)), (azimuth2-(J_PI_/3.0)), long1, lat1,
			p1_60r_x, p1_60r_y, long2, lat2, p2_60l_x, p2_60l_y))
		MDIS
				= _MIN_(MDIS,
						JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),
								Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),
								long1,lat1,long2,lat2,ALPH));

	return (_MIN_(MDIS,5.0*D));

	return (10.0*D);
}

int _G_close_OST(void) {
	int i=0, l_xm, l_xn, l_xh, l_ix = -1;
	l_xn = GRAPH->G_COLSPECTRUML+1;
	while (i < G_S_coX->Card) {
		l_xh = G_S_coX->Chain[i];
		l_xm = _hp_i_rhl(l_xh);
		if (l_xm<0) {
			i++;
			continue;
		}
		if (!l_xm) {
			LAST_Col = ACT_Col;
			return (i);
		}
		if (l_xm < l_xn) {
			LAST_Col = ACT_Col;
			l_ix = i;
			l_xn = l_xm;
			i++;
			continue;
		}
		if (l_xm == l_xn) {
			if (iV_Color_Tmp(iATSet_VIx(l_xh))> iV_Color_Tmp(iATSet_VIx(G_S_coX->Chain[l_ix]))) {
				LAST_Col = ACT_Col;
				l_ix = i; /////l_xn = l_xm;
			}
		}
		i++;
	}
	return (l_ix);
}
int _G_close_CAS(void) {
	int i=0, l_xn, l_xm, l_xh, l_ix = -1;
	l_xm = GRAPH->G_COLSPECTRUML;
	while (i < G_S_coX->Card) {
		l_xh = G_S_coX->Chain[i];
		l_xn = _hp_i_rhc(l_xh);
		if (!l_xn) {
			i++;
			continue;
		}
		if (l_xn < 2)
			return (i);
		else if (l_xn < l_xm) {
			l_ix = i;
			l_xm = l_xn;
		}
		i++;
	}
	return (l_ix);
}

unsigned char _O_ext_CI(void) {
	int l_vk = -1;
	unsigned char L_done=1, l_ext=0;
	while (G_Col_Inside && L_done && G_S_coX->Card) {
		l_vk = _G_close_CAS();
		if (-1 < l_vk) {
			_hp_v_2r(G_S_coX->Chain[l_vk]);
			G_S_Q0_->Chain[G_S_Q0_->Card++]=G_S_coX->Chain[l_vk];
			_T_skip_ixOfSet(l_vk, G_S_coX);
			l_ext = 1;
		} else
			L_done=0;
	}
	return (l_ext);
}

unsigned char _O_in_CI(void) {
	int l_vk = -1;
	unsigned char L_done=1, l_ext=0;
	while (L_done && G_S_coX->Card) {
		l_vk = _G_close_OST();
		if (-1 < l_vk) {
			iATSetColor(G_S_coX->Chain[l_vk]) = LAST_Col;
			iV_Color_Tmp(iATSet_VIx(G_S_coX->Chain[l_vk]))--;
			G_S_Q0_->Chain[G_S_Q0_->Card++]=G_S_coX->Chain[l_vk];
			_T_skip_ixOfSet(l_vk, G_S_coX);
			l_ext = 1;
		} else
			L_done=0;
	}
	return (l_ext);
}

unsigned char _O_assin_CI(void) {
	int l_ix=0;
	unsigned char l_llif=0, L_done=1;
	while (L_done && (l_ix<G_S_coX->Card) && G_S_coX->Card) {
		if (_hp_L_x3r(G_S_coX->Chain[l_ix])) {
			G_S_Q0_->Chain[G_S_Q0_->Card++]=G_S_coX->Chain[l_ix];
			_T_skip_ixOfSet(l_ix, G_S_coX);
			l_llif=1;
		} else
			L_done = 0;
	}
	return (l_llif);
}

void i_O_fa_ord(void) {
	while ( ((G_Col_Inside && _O_ext_CI()) ||_O_in_CI()||_O_assin_CI())) {
	}
}

int h_get_lenab(int p_cix) {
	int i;
	for (i=GRAPH->G_COLSPECTRUML; 0<i; i--)
		if (GRAPH->ENAB[p_cix][i]>0)
			return (i-1);
	return (-1);
}

void _i_O_fa_(void) {
	G_S_Q0_->Chain[0]=0;
	G_S_Q0_->Card++;
	AT_Set[0].AT_Color = GRAPH->G_COLSPECTRUML / 2;
	GRAPH->G_LowerBound = AT_Set[0].AT_Color;
	GRAPH->G_UpperBound = GRAPH->G_LowerBound;
	G_Col_Inside=1;
	_O_diff_Set();
	i_O_fa_ord();
	O_move_B();
}

int _O_test_CC(void) {
	int i, j;
	for (i=0; i<GRAPH->Card; i++)
		iV_Color_Tmp(i) = iV_Color_Req(i);

	G_S_Q0_->Card = 0;
	_h_SET_nord(xx_NoOfCol, G_S_coX);

	_i_O_fa_();

	if ( !G_S_coX->Card) {
		for (j=0; j<xx_NoOfCol; j++)
			*(ATPl_Tmp+j) = *(AT_Set+j);
		Assignment_Found = 1;
	}
	return (G_S_coX->Card);
}

int _h_get_trile(int p_ile, int p_pr, int p_npr, int p_opr, int p_od, int p_nd,
		int p_tr) {
	int l_ffound=0;

	l_ffound = _i_get_P_CO(p_ile, p_pr, p_tr);
	if (!l_ffound)
		return (0);
	if (!_O_test_CC()) {
		G_Cur_SET = l_ffound;
		_h_put_out(l_ffound, p_npr);
		return (1);
	}
	if (l_ffound < G_Min_SET+1) {
		_h_rem_duo(l_ffound, p_opr, _MAX_(1,p_nd));
		return (2);
	} else
		_h_put_back(l_ffound, p_pr, p_od);
	G_Cur_SET = l_ffound;
	return (3);
}

void _O_test_subset(int p_pr) {
	int l_ffound = G_Min_SET, l_trx = 0, l_try, l_trz;

	LOW_PRIO = p_pr + 1;
	l_trx = IT_MAX;
	while (G_PAR[p_pr].pclb < l_trx) {
		switch (_h_get_trile(l_ffound, p_pr, EX_PRIO, p_pr-1,
				G_PAR[p_pr].pdisr, G_PAR[p_pr].pdisr-1, l_trx)) {
		case 0:
			l_trx--;
			l_ffound = _MAX_(l_ffound,G_Min_SET);
			break;
		case 1:
			l_try = l_trx;
			l_ffound = _MAX_(G_Min_SET,2*G_Cur_SET);
			break;
		case 2:
			l_try = l_trx;
			break;
		default:
			l_trx = l_try;
			l_ffound = _MAX_(G_Min_SET,G_Cur_SET/2);
			break;
		}
	}
	l_trz = G_Act_d;
	if (_O_test_CC()) {
		G_Act_d = 1-l_trz;
		_O_test_CC();
	}
}

unsigned char _O_test_all(int p_pr) {
	int l_xn=0;
	LOW_PRIO = p_pr;

	if ((l_xn=_O_test_CC())) {
		_OUT_coloring2protfile();
		return (0);
	}
	_OUT_coloring2protfile();
	///fflush(ProtocolFile);      
	return (1);
}

void _i_X_AFP_(void) {
	int LOW_PRIO, l_prio;

	if (!PARTITION) {
		_h_SET_id();
		C_Cl_Set();
	} else {
		O_decompose_GRAPH(N2_PRIO,ColorsCard,1,1,Max_ATCell);
	}

	h_def_mset();

	LOW_PRIO = NO_PRIO;

	for (l_prio=CCl_PRIO; (2 < l_prio); l_prio--) {

		if (!G_PAR[l_prio].act_prio)
			continue;
		//fprintf(ProtocolFile,"FA for PRIO = %d, DIST = %d  >>>>>\n",l_prio,G_PAR[l_prio].pdisr);
		if ( !_O_test_all(l_prio))
			_O_test_subset(l_prio);
	}

	_OUT_coloring2protfile();
	///fflush(ProtocolFile);           
	_OUT_current_coloring(1);

}

short read_GRAPH_data(char * p_ctrfile) {
	int i=0, j=0, l_prio;
	char l_xstr[L_L_Mx], l_xline[L_L_Mx+1], *l_strx;
	FILE * ControlFile;

	if ((ControlFile =fopen(p_ctrfile, "r")) == NULL) {
		printf("ERROR:: opening file: %s", p_ctrfile);;
		return (0);
	}

	/*  while((i<26 )&& (fgets(l_xline,L_L_Mx,ControlFile) != NULL))
	 {
	 switch(i){
	 case 0: 
	 sscanf(l_xline,"%s %d",l_xstr,&S_DIST);i++; continue; break;
	 case 1: 
	 sscanf(l_xline,"%s %d",l_xstr,&C_DIST);i++; continue; break;
	 case 2: 
	 sscanf(l_xline,"%s %d",l_xstr,&N_DIST_REQ);i++; continue; break;
	 case 3: 
	 sscanf(l_xline,"%s %d",l_xstr,&N_DIST_MIN);i++; continue; break;
	 case 4: 
	 sscanf(l_xline,"%s %d",l_xstr,&SON_DIST);i++; continue; break;
	 case 5: 
	 sscanf(l_xline,"%s %d",l_xstr,&ReCalc_All);i++; continue; break;
	 case 6: 
	 sscanf(l_xline,"%s %d",l_xstr,&Use_TRAF);i++; continue; break;              //0=no it used, 1=Traffic, 2=Area
	 case 7: 
	 sscanf(l_xline,"%s %d",l_xstr,&Use_SON);i++; continue; break;
	 case 8: 
	 sscanf(l_xline,"%s %d",l_xstr,&QUALITY);i++; continue; break;
	 case 9: 
	 sscanf(l_xline,"%s %d",l_xstr,&PARTITION);i++; continue; break;
	 case 10: 
	 sscanf(l_xline,"%s %d",l_xstr,&PARTITION_Exist);i++; continue; break;
	 case 11: 
	 sscanf(l_xline,"%s %d",l_xstr,&Max_ATCell);i++; continue; break;                  
	 case 12: 
	 sscanf(l_xline,"%s %d",l_xstr,&Max_ATSCl);i++; continue; break;                   
	 case 13: 
	 sscanf(l_xline,"%s %d",l_xstr,&HOPPING_Type);i++; continue; break;
	 case 14: 
	 sscanf(l_xline,"%s %d",l_xstr,&USE_Grouping);i++; continue; break;
	 case 15: 
	 sscanf(l_xline,"%s %d",l_xstr,&GROUP_CARD);i++; continue; break;	

	 case 16: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(ProtFile,l_strx+1);	
	 if( (l_strx = strchr(ProtFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 17: 
	 sscanf(l_xline,"%s %d",l_xstr,&CCl_CARD);i++; continue; break;                    
	 case 18: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(GraphFile,l_strx+1); 
	 if( (l_strx = strchr(GraphFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 19: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(NeigFile,l_strx+1);	 
	 if( (l_strx = strchr(NeigFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 20: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(IntfFile,l_strx+1);	
	 if( (l_strx = strchr(IntfFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 21: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(ColorFile,l_strx+1);	
	 if( (l_strx = strchr(ColorFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 22: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(CliqFile,l_strx+1);	
	 if( (l_strx = strchr(CliqFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 23: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(ForbFile,l_strx+1);	
	 if( (l_strx = strchr(ForbFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 24: 
	 if( (l_strx = strchr(l_xline,(int)('"'))) != NULL) 
	 {strcpy(ExcpFile,l_strx+1);	
	 if( (l_strx = strchr(ExcpFile,(int)('"'))) != NULL) l_strx[0]='\0';
	 i++; continue;}
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 case 25: 
	 if( ((l_strx = strchr(l_xline,(int)(' '))) != NULL) ) {
	 strcpy(l_xline,l_strx+1); 
	 sscanf(l_xline,"%d",&ColorsCard);  
	 Colors = (int *) calloc(ColorsCard,sizeof(int));      
	 Error_Msg__ALLOC(Colors,"AllocErr  by alloc Colors",exit(0);)
	 j=0;
	 while( ((l_strx = strchr(l_xline,(int)(' '))) != NULL) && (j<ColorsCard )) {
	 strcpy(l_xline,l_strx+1); 
	 sscanf(l_xline,"%d",&Colors[j]);j++;	

	 }
	 i++; continue; 
	 }
	 else printf("ERROR:: reading file: %s line: %s\n",p_ctrfile,l_xline);
	 break;
	 default : break;
	 }
	 }	
	 */

	while ((i<13 )&& (fgets(l_xline, L_L_Mx, ControlFile) != NULL)) {
		switch (i) {
		case 0:
			if ( (l_strx = strchr(l_xline, (int)('"'))) != NULL) {
				strcpy(GraphFile, l_strx+1);
				if ( (l_strx = strchr(GraphFile, (int)('"'))) != NULL)
					l_strx[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR:: reading file: %s line: %s\n", p_ctrfile,
						l_xline);
			break;
		case 1:
			sscanf(l_xline, "%s %d", l_xstr, &S_DIST);
			i++;
			continue;
			break;
		case 2:
			sscanf(l_xline, "%s %d", l_xstr, &C_DIST);
			i++;
			continue;
			break;
		case 3:
			sscanf(l_xline, "%s %d", l_xstr, &Max_ATCell);
			i++;
			continue;
			break; ///? max rt per cell            
		case 4:
			sscanf(l_xline, "%s %d", l_xstr, &N_DIST_REQ);
			i++;
			continue;
			break;
		case 5:
			sscanf(l_xline, "%s %d", l_xstr, &SON_DIST);
			i++;
			continue;
			break;
		case 6:
			if ( (l_strx = strchr(l_xline, (int)('"'))) != NULL) {
				strcpy(NeigFile, l_strx+1);
				if ( (l_strx = strchr(NeigFile, (int)('"'))) != NULL)
					l_strx[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR:: reading file: %s line: %s\n", p_ctrfile,
						l_xline);
			break;
		case 7:
			sscanf(l_xline, "%s %d", l_xstr, &Use_TRAF);
			i++;
			continue;
			break;
		case 8:
			sscanf(l_xline, "%s %d", l_xstr, &Use_SON);
			i++;
			continue;
			break;
		case 9:
			sscanf(l_xline, "%s %d", l_xstr, &QUALITY);
			i++;
			continue;
			break;
		case 16:
			if ( (l_strx = strchr(l_xline, (int)('"'))) != NULL) {
				strcpy(ProtFile, l_strx+1);
				if ( (l_strx = strchr(ProtFile, (int)('"'))) != NULL)
					l_strx[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR:: reading file: %s line: %s\n", p_ctrfile,
						l_xline);
			break;
		case 10:
			if ( (l_strx = strchr(l_xline, (int)('"'))) != NULL) {
				strcpy(IntfFile, l_strx+1);
				if ( (l_strx = strchr(IntfFile, (int)('"'))) != NULL)
					l_strx[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR:: reading file: %s line: %s\n", p_ctrfile,
						l_xline);
			break;
		case 11:
			if ( (l_strx = strchr(l_xline, (int)('"'))) != NULL) {
				strcpy(ColorFile, l_strx+1);
				if ( (l_strx = strchr(ColorFile, (int)('"'))) != NULL)
					l_strx[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR:: reading file: %s line: %s\n", p_ctrfile,
						l_xline);
			break;
		case 12:
			if ( ((l_strx = strchr(l_xline, (int)(' '))) != NULL)) {
				strcpy(l_xline, l_strx+1);
				sscanf(l_xline, "%d", &ColorsCard); ///?????
				Colors = (int *) calloc(ColorsCard, sizeof(int)); ///???FCarriers = (int *) calloc(ColorsCard,sizeof(int));
				Error_Msg__ALLOC(Colors,"AllocErr  by alloc Colors",exit(0);)
				j=0;
				while ( ((l_strx = strchr(l_xline, (int)(' '))) != NULL) && (j
						<ColorsCard )) {
					strcpy(l_xline, l_strx+1);
					sscanf(l_xline, "%d", &Colors[j]);
					j++; ///???	sscanf(l_xline,"%d",&FCarriers[j]);j++;

				}
				i++;
				continue;
			} else
				printf("ERROR:: reading file: %s line: %s\n", p_ctrfile,
						l_xline);
			break;
		default:
			break;
		}
	}

	fclose(ControlFile);

	G_Col_Offset = Colors[0];

	ColInterval_L = Colors[ColorsCard-1] - G_Col_Offset + 1;

	ColorSpectrum_L = ColInterval_L;

	if (!read_GRAPH())
		return (0);
	if (!NoOfAtoms)
		return (1);

	SetOfVertexes = _a_SET(NoOfAtoms);
	AT_Set = _a_AT_set(NoOfAtoms);
	G_S_Qx_ = _a_SET(NoOfAtoms);
	G_S_coX = _a_SET(NoOfAtoms);
	G_S_Q0_ = _a_SET(NoOfAtoms);

	Max_DIST = _MAX_(C_DIST,S_DIST);
	Max_DIST = _MAX_(Max_DIST,N_DIST_REQ);
	Max_DIST = _MAX_(Max_DIST,SON_DIST);

	_h_init_GRAPH();

	if (ColInterval_L != ColorsCard)
		_h_COL_disa();

	_h_V_colord();

	_h_SET_nord(GRAPH->Card,SetOfVertexes);
	_PUT_card_L(Max_ATCell);
	SON_IT_Min = O_N_N_sign(N_DIST_REQ)*Use_SON*ITN_OFFSET;
	N_IT_Min = ITN_OFFSET + SON_IT_Min;

	for (l_prio=EX_PRIO; NUL_PRIO<l_prio; l_prio--) {
		G_PAR[l_prio].act_prio = 0;
		G_PAR[l_prio].pclb = 0;
		G_PAR[l_prio].pdisr = 1;
		G_PAR[l_prio].pdism = 1;
	}
	G_PAR[CCl_PRIO].pdisr = C_DIST;
	G_PAR[SCl_PRIO].pdisr = S_DIST;
	G_PAR[N2_PRIO].pdisr = N_DIST_REQ;
	G_PAR[SON_PRIO].pdisr = SON_DIST;

	G_PAR[CCl_PRIO].pclb = 998;
	G_PAR[SCl_PRIO].pclb = 997;
	G_PAR[N2_PRIO].pclb = N_IT_Min-1;
	G_PAR[SON_PRIO].pclb = SON_IT_Min-1;

	return (1);
}

awe_afp(char * control_file) {
	Assignment_Found = 0;
	fflush(stdin);

	if (!read_GRAPH_data(control_file)) {
		_f_ALL();
		return (1);
	}
	Use_SON = Use_SON && N_DIST_REQ && SON_DIST;

	//LN, 28.03.2011, removed USE_TRAF condition, since it disable
	//computing freq on only area data
	if (!read_ITM(IT1_PRIO,1)) {
		_f_ALL();
		return (IT_R_ERR);
	}
	G_PAR[IT1_PRIO].act_prio = Use_TRAF;

	if (C_DIST && (Max_ATCell > 1)) {
		put_SCL_CR(CCl_PRIO,C_DIST,IT_MAX);
		G_PAR[CCl_PRIO].act_prio = 1;
	}

	if (S_DIST && (Max_ATSCl > 1)) {
		put_CCL_CR(SCl_PRIO,S_DIST,IT_MAX);
		G_PAR[SCl_PRIO].act_prio = 1;
	}

	if (N_DIST_REQ) {
		if (!read_NEIGHBOURS(N2_PRIO,N_DIST_REQ,N_IT_Min)) {
			_f_ALL();
			return (IT_R_ERR);
		}
		G_PAR[N2_PRIO].act_prio = 1;
	}

	if (Use_SON) {
		put_SON_CR(N2_PRIO,SON_PRIO,1,-1,SON_IT_Min);
		G_PAR[SON_PRIO].act_prio = 1;
	}

	_i_X_AFP_();

	_f_ALL();
	return (NO_ERR);

}

int write_last_plan(int p_model) {
	if (!Assignment_Found)
		return (0);
	return (1);
}
