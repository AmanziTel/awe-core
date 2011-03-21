/********************************************************************************************************************************
 //  Author: Jan Paul Bernert.
 //  The intellectual property of the software is limited to the author only.
 //  This version is a non-exclusive licence for AMANZITel only.
 //  It is not allowed to alter, modify, copy, or adapt the software as well 
 /// as any part of it as well as the documentation.
 //  Date: March, 3rd, 2011
 //
 *********************************************************************************************************************************/
#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <string.h>
#include <math.h>
#define P_2_(t) t*
#define L_N_M_x 80
#define L_L_M_x 30000
#define _XE_c 13
#define L_FN_M_x 256
#define J_PI_ 3.1415926
#define Bool_sin(was) (((was)!=0) ? 1 : 0)
#define Bool_cos(was) (((was)!=0) ? 0 : 1)
#define Bool_IFELSE(x,Was,B) (Bool_sin(x) ? Was : B);
#define Error_Msg_ALLOC(X,msg,act) if(X==NULL){printf("\n%s\n",msg);act;}
#define _MAX_(was,poa) (((was)<(poa)) ? (poa) : (was))
#define _MIN_(was,poa) (((was)<(poa)) ? (was) : (poa))
#define Bool_sum(x,was,poa) (Bool_sin(x)*(was) + Bool_cos(x)*(poa))
#define PP(x) (x++)
#define MM(x) (x--)
#define NEXT_B(x,was) Bool_IFELSE(x,((was) += 1),((was) -= 1))
#define O_Sign_(was) ((0 < (was)) ? 1 : -1)
#define O_N_N_Sign_(was) ((0 < (was)) ? 1 : 0)
#define Free_SET(M) if(M!=NULL){free(M->V__k_e_t__);free(M);M=NULL;}
#define __S_I_A_T__ S_I_A_T__
#define CALY __G_N_z_b___
#define _MOC_s_i_a_t_ __S_I_A_T__->V__ILE
#define Atom_(i,_UP) _UP->V__k_e_t__[i]
#define _ABS_i_l_e_(_UP) _UP->V__ILE
#define _CALY_(i) Atom_(i,CALY)
#define _I_W_E_Z_E_L_(i) Atom_(i,CALY)
#define __W_E_Z_E_L_(i) __S_I_A_T__->V__w_e_z[i]
#define _W_E_Z_E_L_I_(i) __S_I_A_T__->V__w_e_z[i]->V__n_i_d_
#define _W_E_Z_S_F(i) __S_I_A_T__->V__w_e_z[i]->V__n_c_f_r_
#define _T_W_E_S_F(i) __S_I_A_T__->V__w_e_z[i]->___s_f_cz_
#define _C_T_n_F(i) __S_I_A_T__->V__w_e_z[i]->pyta__n_n_f___
#define _S_T_O_S(i,j) __S_I_A_T__->V__r_e____[i][j]
#define _D_o_p_u(i) __S_I_A_T__->V__p_o_z__[i]
#define _W_E__(i) __W_E_Z_E_L_(_CALY_(i))
#define _W_E_I__(i) _W_E_Z_E_L_I_(_CALY_(i))
#define _C_T_n__F(i) _C_T_n_F(_CALY_(i))
#define _S_T_O__S(i,j) _S_T_O_S(_CALY_(i),_CALY_(j))
#define _I_W_E_Z_E__L_(i,_UP) _I_W_E_Z_E_L_(Atom_(i,_UP))
#define _U_N_I_(i,_UP) _CALY_(Atom_(i,_UP))
#define _W__E__(i,_UP) _W_E__(Atom_(i,_UP))
#define _C_T_n__F_(i,_UP) _C_T_n__F(Atom_(i,_UP))
#define _S_T_O__S_(i,j,_UP) _S_T_O__S(Atom_(i,_UP),Atom_(j,_UP))
#define _D_o_p__u(i,_UP) _D_o_p_u(Atom_(i,_UP))
#define S_T_O_S(R,i,j) R[_W_E_I__(i)][_W_E_I__(j)]
#define _B_S_n_(i) __W_E_Z_E_L_(i)->V__s_t_r_n__
#define _B_S_c_n_(i) __W_E_Z_E_L_(i)->V__c_e_n__
#define pytaIndex(i) L__r_t_[i].V__N_I_x
#define _S_r_n_o(i) L__r_t_[i].V__r_n_o__
#define _G_F_(i) L__r_t_[i].V__r_f_n_o__
#define P__I_L_E_ 10
#define L_a 2
#define L_c 3
#define L_d 1
#define L_p 1
#define xDATA_L 0
#define yDATA_L 0
#define zDATA_L 0
#define _I_V_R "00 "
#define __L__C_M__ (L_p+L_d+L_c+L_a+xDATA_L+yDATA_L+zDATA_L+2)
#define _P_x 9
#define _P_t 8
#define _P_c 7
#define _P_s 6
#define _P_n 5
#define _P_sn 4
#define _P_ic 3
#define _P_i 2
#define _P_no 1
#define _P_0 0
#define M_I_x 999
#define M_I_n 333
#define M_I_c 331
#define M_I_a 32
#define M_I_0 99
#define NO_ERR 0
#define SCC_E 3
#define IT_R_ERR 4

#include <ctype.h>

__inline void _my_sscanf1(char* lp1, int* lodw) {
	*lodw = *lp1 -'0';
}

#define R__p(R,i,j,lodw) lodw=0;_my_sscanf1(R[i][j],&lodw)
#define R__d(R,i,j,lodw) lodw=0;_my_sscanf1(R[i][j]+L_p,&lodw)
#define R__i(R,i,j,lodw) lodw=0;sscanf(R[i][j]+(L_p+L_d),"%3d",&lodw)
#define W__p(R,i,j,w) f_pr_l(R[i][j],w,L_p)
#define W__d(R,i,j,w) f_pr_l(R[i][j]+L_p,w,L_d)
#define W__i(R,i,j,w) f_pr_l(R[i][j]+(L_p+L_d),w,L_c)
#define R___p(R,i,j,lodw) _my_sscanf1(S_T_O_S(R,i,j),&lodw)
#define R___d(R,i,j,lodw) _my_sscanf1(S_T_O_S(R,i,j)+L_p,&lodw)
typedef char __M_C__[__L__C_M__];
typedef struct cell {
	int V__N_I_x, V__r_n_o__, V__r_f_n_o__;
	short __b_r_f__;
} T__R_A_T_R__;
typedef struct s__f_o {
	int frno, frdis;
} T__F_O__;
typedef struct set {
	int * V__k_e_t__;
	int V__ILE;
} T__Z_B_I__;
typedef struct s__w_e_z {
	long CO_Int, AD_Int, V__c_c__;
	char V__s_t_r_n__[L_N_M_x], V__c_e_n__;
	int V__c_n__, _x_s_s_, V__n_o_i_, CO_Int_Ave, V__n_i_d_;
	int *V__n_c_f_l__, V__n_c_f_r_, pyta__n_n_f___, ___s_f_cz_, ___s_f_a,
			__s_f__x__;
} T__w_e_z__;
typedef struct netstruct {
	int V__f_B_l__, __l_b___, __u_b__, V__n_n_f_x__, V__ILE, __l_b_lewo,
			_l_b_t_r_zy[3];

	T__w_e_z__ ** V__w_e_z;
	__M_C__ ** V__r_e____;
	short ** V__p_o_z_;
	unsigned * V__p_o_z__;
} T__S_I_A__;
typedef struct priopar {
	int __a__p, __d__r, __d__m, __c_l__b__, pamax, palb, __c_it_;
} T__p__p__;
T__p__p__ V__p_p_s_i_e_[P__I_L_E_];
typedef struct pair {
	int Left, Right;
} PAIR_T;
typedef struct intvset {
	int V__ILE;
	PAIR_T * IChain;
} INVSET_T;

T__F_O__ FREQ_O_L, FREQ_O_R;
T__S_I_A__ *S_I_A_T__;
T__R_A_T_R__ * L__r_t_, * L__R_t_;
T__Z_B_I__ *__G_N_z_b___, *__G_q_z_x, *__G_c_z_x, *_G_q_lew, *_G_x_n__C;
long _G__2n_=0, _C_cC_=0, _A_nc=0, _G_Co_i=0, _G_co_d_o_ro_b=0, _G__cl_n___=0;
int _K_=0, _a_ss_B_=0, _K_l_a_M = 0, _G_NaF=0, __N_o_c_o_l_=0, V__f_B_l__l=0,
		_X__o_d_ = 0, _G_i_l_C=0, _U_m_ = 0, _W_ma_l_=0, _W__L=0, _G_u_pa=0,
		_x_l_n=0, _Z_a_k_Z=0, _I_2n_, _I_n_1, NS_N0=0, NS_N1=0, NS_SON0=0,
		NS_ICO=0, N2_L=0, SET_0_1=0, N2LLIM=0, _O_s_f_ = -1, _G_Ff = -1,
		_G_k_r_o_k_min = 20, _G_k_r_o_k_max = 200, _G_o_s_t_qc__=0, _Y_fc1=0,
		_Y_c2=0, _Z_zl, _Z_zr, A_i_1=0, A__i2=0, _F_cc=0;
int __G__R_DI__=1, DDo=1, __G_p_u_s_t__y=0, __G_p_o_p=0;
short __G_f_b_d=1;
double TvA_S0 = 1.0, TvA_S1=1.0, TvAI0=10e10, TvAI1=0.0, T2Ai = 10e10, IT_LOW=
		0.0, TAx =0.0, TAi=10e10;
int _G__D__c=0, _G__D__s=0, _G__D__nr=0, _G__D__sn=0, _X__r_c=0, _G_R_s_x__=0,
		_G__D__nm=0, _G__Ca_a=0, _P_o_d_z_j=0, _G__H_t, _G__U_g, _G__G_c,
		_G__U_t=0, _B_i_n2=0, _G__Q__=50, _G__P__=1, _G__S_c, _Z_L_int,
		_G__N_c, _W_e_g_it=0, *__G__C_l;
char __F_g[L_FN_M_x+1], __F_n[L_FN_M_x+1], __F_i[L_FN_M_x+1],
		__F_p[L_FN_M_x+1], __F_l[L_FN_M_x+1], __F_c[L_FN_M_x+1],
		__F_f[L_FN_M_x+1], __F_e[L_FN_M_x+1];
#define P_2(was) pow(was,2)
#define TOL_ 0.000001
#define SPA_(ux,uy,vx,vy,was) (V_l(ux,uy)*V_l(vx,vy)*cos(was))
#define T_60_l_x(px,py,x,y) (R_60_l_x((x-px),(y-py))+px)
#define T_60_l_y(px,py,x,y) (R_60_l_y((x-px),(y-py))+py)
#define T_60_r_x(px,py,x,y) (R_60_r_x((x-px),(y-py))+px)
#define T_60_r_y(px,py,x,y) (R_60_r_y((x-px),(y-py))+py)
#define ISCUT(was,poa,_up_2,l_d) (DET(was,poa,_up_2,l_d) != 0.0)
#define QEQ(was,poa) (fabs(was-poa) < TOL_)
#define QSUM(was,poa,_up_2) QEQ((was+poa),_up_2)
#define DIS_(p1x,p1y,p2x,p2y) V_l((p2x-p1x),(p2y-p1y))
#define BETV_(ax,ay,ux,uy,vx,vy) (QSUM(AV_(ux,uy,ax,ay),AV_(ax,ay,vx,vy),AV_(ux,uy,vx,vy)))
#define BETP_(x,y,px,py,p1x,p1y,p2x,p2y) \
(BETV_((x-px),(y-py),(p1x-px),(p1y-py),(p2x-px),(p2y-py)))
#define IsCut_L(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y) \
ISCUT(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),\
A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y))
#define Cut_x(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y) \
LCut_x(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),C_L(p1x,p1y,q1x,q1y),\
A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y),C_L(p2x,p2y,q2x,q2y))
#define Cut_y(p1x,p1y,q1x,q1y,p2x,p2y,q2x,q2y) \
LCut_y(A_L(p1x,p1y,q1x,q1y),B_L(p1x,p1y,q1x,q1y),C_L(p1x,p1y,q1x,q1y),\
A_L(p2x,p2y,q2x,q2y),B_L(p2x,p2y,q2x,q2y),C_L(p2x,p2y,q2x,q2y))
int jpb_q(int i) {
	return (i*(i<6) + (5<i)*(5 + 2*(i-5))*(i<12)+ (11<i)*(22+3*(i-11))*(i<19)
			+ (18<i)*(60+4*(i-18))*(i<27)+ (26<i)*(130+5*(i-26))*(i<36)+ (35<i)
			*(245+10*(i-35))*(i<46)+ (45<i)*(460+15*(i-45))*(i<57)+ (56<i)*(840
			+20*(i-56))*(i<69)+ (68<i)*(1460+25*(i-68))*(i<82)+ (81<i)*(2405+50
			*(i-81))*(i<96) +(95<i)*(3905+100*(i-95))*(i<101));
}

void __f_c_sst__(char * _tuke_, char * _s_2_, char * _s_1_) {
	char *_LP_1, *_LP_2;
	long ol=0, nl = 2*(strlen(_tuke_)+strlen(_s_1_));
	_LP_2 = (char *) calloc(nl, sizeof(char));
	ol = strlen(_s_2_);
	nl = strlen(_s_1_);
	strcpy(_LP_2, _s_1_);
	_LP_1 = _tuke_;
	while ((_LP_1 = strstr(_tuke_, _s_2_)) != NULL) {
		strcpy(_LP_2+nl, _LP_1+ol);
		strcpy(_LP_1, _LP_2);
	}
	free(_LP_2);
}
void f_cp_l(char * _we_cia, char * _UP_2) {
	char * _LP_2=(char*)calloc(strlen(_we_cia)+1, sizeof(char));
	strcpy(_LP_2, _we_cia+strlen(_UP_2));
	strcpy(_we_cia, _UP_2);
	strcat(_we_cia, _LP_2);
	free(_LP_2);
}
void f_pr_l(char * _we_cia, int val_, int _UP_1) {
	char * _LP_2=(char*)calloc(_UP_1+1, sizeof(char));
	switch (_UP_1) {
	case 1:
		sprintf(_LP_2, "%1d", val_);
		break;
	case 2:
		sprintf(_LP_2, "%2d", val_);
		break;
	case 3:
		sprintf(_LP_2, "%3d", val_);
		break;
	default:
		break;
	}
	f_cp_l(_we_cia, _LP_2);
	free(_LP_2);
}
T__S_I_A__ * __f_a_sia__(int _UP_1_, int _UP_2_) {
	int i;
	T__S_I_A__ *__S_I_A_T__;
	__S_I_A_T__ = (T__S_I_A__ *) calloc(1,sizeof(T__S_I_A__));
	Error_Msg_ALLOC(__S_I_A_T__,"AllocErr by alloc NETWORK",exit(0);)
	__S_I_A_T__->V__w_e_z = ( P_2_(T__w_e_z__) *) calloc(_UP_1_,sizeof(P_2_(T__w_e_z__)));
	Error_Msg_ALLOC(__S_I_A_T__->V__w_e_z,"AllocErr by alloc __S_I_A_T__->Node",exit(0);)
	for (i=0; i<_UP_1_; i++) {
		__S_I_A_T__->V__w_e_z[i]= (T__w_e_z__ *) calloc(1,sizeof(T__w_e_z__));
		Error_Msg_ALLOC(__S_I_A_T__->V__w_e_z[i],"AllocErr by alloc __S_I_A_T__->V__w_e_z[]",exit(0);)
		__S_I_A_T__->V__w_e_z[i]->V__n_c_f_l__ = (int*) calloc(_UP_2_,sizeof(int));
	}
	__S_I_A_T__->V__r_e____ = (P_2_(__M_C__) *) calloc(_UP_1_,sizeof(P_2_(char)));
	Error_Msg_ALLOC(__S_I_A_T__->V__r_e____,"AllocErr by alloc __S_I_A_T__->Edge",exit(0);)
	for (i=0; i<_UP_1_; i++) {
		__S_I_A_T__->V__r_e____[i]= (__M_C__ *) calloc(_UP_1_,sizeof(__M_C__));
		Error_Msg_ALLOC(__S_I_A_T__->V__r_e____[i],"AllocErr by alloc __S_I_A_T__->V__r_e____[]",exit(0);)
	}
	__S_I_A_T__->V__p_o_z_ = NULL;
	__S_I_A_T__->V__p_o_z__ = (P_2_(unsigned)) calloc(_UP_1_,sizeof(unsigned));
	Error_Msg_ALLOC(__S_I_A_T__->V__p_o_z__,"AllocErr by alloc __S_I_A_T__->Perm",exit(0);)
	return (__S_I_A_T__);
}
void __fa__(void) {
	int i;
	if (__S_I_A_T__) {
		for (i=0; i<__S_I_A_T__->V__ILE; i++) {
			if (__S_I_A_T__->V__w_e_z[i]) {
				free(__S_I_A_T__->V__w_e_z[i]->V__n_c_f_l__);
				free(__S_I_A_T__->V__w_e_z[i]);
			}
		}
		free(__S_I_A_T__->V__w_e_z);
		for (i=0; i<__S_I_A_T__->V__ILE; i++)
			free(__S_I_A_T__->V__r_e____[i]);
		free(__S_I_A_T__->V__r_e____);
		free(__S_I_A_T__->V__p_o_z__);
		free(__S_I_A_T__->V__p_o_z_);
		free(__S_I_A_T__);
	}
	if (L__r_t_)
		free(L__r_t_);
	if (L__R_t_)
		free(L__R_t_);
	if (__G_N_z_b___) {
		free(__G_N_z_b___->V__k_e_t__);
		free(__G_N_z_b___);
	}
	if (__G_q_z_x) {
		free(__G_q_z_x->V__k_e_t__);
		free(__G_q_z_x);
	}
	if (__G_c_z_x) {
		free(__G_c_z_x->V__k_e_t__);
		free(__G_c_z_x);
	}
	if (_G_q_lew) {
		free(_G_q_lew->V__k_e_t__);
		free(_G_q_lew);
	}
}
T__R_A_T_R__ * __f_a_rtl__(int _D_1) {
	int i;
	T__R_A_T_R__ *x_rtliist;
	x_rtliist = (T__R_A_T_R__ *) calloc(_D_1, sizeof(T__R_A_T_R__));
	L__R_t_ = (T__R_A_T_R__ *) calloc(_D_1, sizeof(T__R_A_T_R__));
	Error_Msg_ALLOC(x_rtliist,"AllocErr by alloc x_rtliist",exit(0);)
	Error_Msg_ALLOC(L__R_t_,"AllocErr by alloc RTPl_Tmp",exit(0);)
	for (i=0; i<_D_1; i++) {
		x_rtliist[i].__b_r_f__ = 0;
		L__R_t_[i].__b_r_f__ = 0;
	}
	return (x_rtliist);
}
T__Z_B_I__ * __f_a_zb__(int _U_1) {
	T__Z_B_I__ * M;
	M=(T__Z_B_I__ *) calloc(1, sizeof(T__Z_B_I__));
	Error_Msg_ALLOC(M,"AllocErr in alloc_SET",return(NULL);)
	M->V__k_e_t__ = (int *)calloc(_U_1, sizeof(int));
	Error_Msg_ALLOC(M->V__k_e_t__,"AllocErr in __f_a_zb__ by M->Chain",return(NULL);)
	M->V__ILE = 0;
	return (M);
}
int f_w_zb(int e, T__Z_B_I__ * _up_1) {
	int i=0, was=0;
	while (!(was) && (i<_up_1->V__ILE)) {
		was = ((was||(_up_1->V__k_e_t__[i]==e)));
		i++;
	}
	return (i-Bool_sin(was));
}
void f_skok_el(int xix, T__Z_B_I__ * Zb_x) {
	int i=0;
	if (xix == (Zb_x->V__ILE - 1)) {
		Zb_x->V__ILE--;
		return;
	}
	i=xix;
	while (i < Zb_x->V__ILE - 1) {
		Zb_x->V__k_e_t__[i]=Zb_x->V__k_e_t__[i+1];
		i++;
	}
	Zb_x->V__ILE--;
}
void f_zb_cp(T__Z_B_I__ * Zb_A, T__Z_B_I__ * Zb_B) {
	Zb_A->V__ILE=0;
	while (Zb_A->V__ILE<Zb_B->V__ILE) {
		Zb_A->V__k_e_t__[Zb_A->V__ILE]= Zb_B->V__k_e_t__[Zb_A->V__ILE];
		Zb_A->V__ILE++;
	}
}
void f_zb_roz(T__Z_B_I__ * Zb_A, T__Z_B_I__ * Zb_B) {
	int i, xix;
	for (i=0; i<Zb_B->V__ILE; i++) {
		xix = f_w_zb(Zb_B->V__k_e_t__[i], Zb_A);
		if (xix<Zb_A->V__ILE)
			f_skok_el(xix, Zb_A);
	}
}
void IDform_Set(int _U_1, T__Z_B_I__ * Zb_B) {
	int i;
	for (i=0; i<_U_1; i++)
		Zb_B->V__k_e_t__[i]= i;
	Zb_B->V__ILE = _U_1;
}
void _o_f_cp__(char * pf_name, T__R_A_T_R__ * Zb_xN) {
	int x, i, j=0;
	FILE * _LF_1;
	if ((_LF_1 =fopen(pf_name, "w")) == NULL) {
		printf("ERROR by open filr %s", pf_name);;
		return;
	}
	while (j<_ABS_i_l_e_(__S_I_A_T__)) {
		fprintf(_LF_1, "%s %d %d %d %d ",
				__S_I_A_T__->V__w_e_z[j]->V__s_t_r_n__,
				__S_I_A_T__->V__w_e_z[j]->V__c_n__,
				__S_I_A_T__->V__w_e_z[j]->_x_s_s_,
				__S_I_A_T__->V__w_e_z[j]->V__n_c_f_r_,
				__S_I_A_T__->V__w_e_z[j]->___s_f_a);
		for (i=0; i<__S_I_A_T__->V__w_e_z[j]->___s_f_a; i++) {
			x = __S_I_A_T__->V__w_e_z[j]->V__n_c_f_l__[i]+__G_p_o_p;
			fprintf(_LF_1, "%d ", x);
		}
		fprintf(_LF_1, "\n");
		j++;
	}
	fclose(_LF_1);
}
short _ifx__(int ni_v, int fi_v) {
	int rodw;
	if (!__S_I_A_T__->V__w_e_z[ni_v]->__s_f__x__)
		return (0);
	for (rodw=0; rodw<__S_I_A_T__->V__w_e_z[ni_v]->__s_f__x__; rodw++)
		if (__S_I_A_T__->V__w_e_z[ni_v]->V__n_c_f_l__[rodw]== fi_v)
			return (1);
	return (0);
}
void _o_f_pfa__(T__R_A_T_R__ *Zb_xN) {
	int i, j, lpk;
	for (i=0; i<__S_I_A_T__->V__ILE; i++) {
		__S_I_A_T__->V__w_e_z[i]->___s_f_a = __S_I_A_T__->V__w_e_z[i]->__s_f__x__;
	}
	for (i= -1; i<__S_I_A_T__->__u_b__+1; i++) {
		lpk=0;
		for (j=0; j<__N_o_c_o_l_; j++) {
			if (i==Zb_xN[j].V__r_f_n_o__) {
				if (!_ifx__(Zb_xN[j].V__N_I_x, i)) {
					if (__S_I_A_T__->V__w_e_z[Zb_xN[j].V__N_I_x]->___s_f_a < __S_I_A_T__->V__w_e_z[Zb_xN[j].V__N_I_x]->V__n_c_f_r_) {
						__S_I_A_T__->V__w_e_z[Zb_xN[j].V__N_I_x]->V__n_c_f_l__[__S_I_A_T__->V__w_e_z[Zb_xN[j].V__N_I_x]->___s_f_a]= i;
						__S_I_A_T__->V__w_e_z[Zb_xN[j].V__N_I_x]->___s_f_a++;
					}
				}
				lpk++;
			}
		}
	}
	if (j<__N_o_c_o_l_) {
		for (j=0; j<__G_c_z_x->V__ILE; j++) {
		}
	}
}
int _o_act_p__(int _UP_1) {
	if (!_a_ss_B_)
		return (0);
	_o_f_pfa__(L__R_t_);
	if (_UP_1)
		_o_f_cp__(__F_l, L__R_t_);
	return (1);
}
__inline P_s_i_a_(int _UP_1, int _UP_2, __M_C__ ** _UP_3) {
	int i, lodw=0;
	if (!(_UP_1<0) && (_D_o_p_u(_UP_1) != 2))
		_D_o_p_u(_UP_1) = 1;
	for (i=_UP_1+1; i<_ABS_i_l_e_(__S_I_A_T__); i++) {
		R___d(_UP_3,_UP_1,i,lodw);
		if (_D_o_p_u(i) != 2)
			_D_o_p_u(i) = Bool_cos(lodw<_UP_2) * _D_o_p_u(i);
	}
}
__inline void U__P_(int _UP_1) {
	int i;
	if (!(_UP_1<0))
		_D_o_p_u(_UP_1) = _D_o_p_u(_UP_1)*(_D_o_p_u(_UP_1) - 1);
	for (i=_UP_1+1; i<_ABS_i_l_e_(__S_I_A_T__); i++)
		_D_o_p_u(i) = _MAX_(1,_D_o_p_u(i));
}
void IS__(T__Z_B_I__ * Q0p) {
	int i;
	for (i=0; i<_ABS_i_l_e_(Q0p); i++) {
		_D_o_p__u(i,Q0p) = 2;
		__G_p_u_s_t__y--;
	}
}
__inline int C_c_lb_(int _UP_3, T__Z_B_I__ * _UP_2) {
	int i, _j=0;
	for (i=0; i<_ABS_i_l_e_(_UP_2); i++)
		_j += _C_T_n__F_(i,_UP_2);
	_j = 1 + (_UP_3*(_j-1));
	return (_j);
}
__inline int C_c_lbs_(int _UP_3, int skad, T__Z_B_I__ * _UP_2) {
	int i, lx=0, _j=0;
	lx = C_c_lb_(_UP_3, _UP_2);
	lx = lx + _UP_3 - 1;
	for (i=skad; i<_ABS_i_l_e_(CALY); i++)
		_j += _C_T_n__F(i);
	_j = 1 + (_UP_3*(_j-1));
	_j += lx;
	return (_j);
}
__inline int wozb_(T__Z_B_I__ * Zb_x) {
	return (_ABS_i_l_e_(Zb_x));
}
int LBfilter(int _UP_1, int _UP_3, int skad, int * _UP_2, T__Z_B_I__ * starZb,
		T__Z_B_I__ * nowZb) {
	int nlb, xlb = *_UP_2;
	nlb=C_c_lbs_(_UP_3, skad, nowZb);
	if (_UP_1 != _UP_3)
		return (xlb < nlb );
	xlb=wozb_(starZb);
	nlb=wozb_(nowZb) + (_ABS_i_l_e_(CALY) - skad);
	return (xlb < nlb );
}
__inline int _dop_dlazb_(int wag_a, int xix, int _UP_2, __M_C__ ** _UP_3,
		T__Z_B_I__ * _UP) {
	int i=0, lodw=0;
	while (i < _ABS_i_l_e_(_UP)) {
		R___p(_UP_3,xix,Atom_(i,_UP),lodw);
		if (lodw<wag_a)
			return (0);
		R___d(_UP_3,xix,Atom_(i,_UP),lodw);
		if (lodw<_UP_2)
			return (0);

		i++;
	}
	return (1);
}
void _roz_klizb_(short * _znala_p, int _UP_2, int _UP_4, int * _rzad_p,
		int * _UP_1, int _ile_wxcl, int _UP_8, __M_C__ ** _p_stos,
		T__Z_B_I__ * _UP_, T__Z_B_I__ * _zb_q_x0) {
	int lp1, q=_UP_8, nofmax=0, _lp_1 = C_c_lb_(_UP_8, _UP_);
	unsigned _zrob_=0, _lp_=0;
	lp1=_ile_wxcl;
	while ((lp1 < _ABS_i_l_e_(CALY) ) && (_lp_ || (!*_znala_p && (_lp_1 < (_UP_4+1)) && LBfilter(
			*_rzad_p, _UP_8, lp1, _UP_1, _zb_q_x0, _UP_)))) {
		if ( (_D_o_p_u(lp1) != 1) || !(_dop_dlazb_(_UP_2, lp1, q, _p_stos, _UP_))) {
			lp1++;
			_lp_=1;
			continue;
		}
		Atom_(_ABS_i_l_e_(_UP_)++,_UP_) = lp1;
		P_s_i_a_(lp1, q, _p_stos);
		_roz_klizb_(_znala_p, _UP_2, _UP_4, _rzad_p, _UP_1, lp1+1, _UP_8,
				_p_stos, _UP_, _zb_q_x0);
		_zrob_=1;
		_lp_=0;
		lp1++;
	}
	if (*_znala_p)
		return;
	if (!_zrob_) {
		*_UP_1 = C_c_lb_(_UP_8, _UP_);
		if (__S_I_A_T__->__l_b_lewo < *_UP_1) {
			__S_I_A_T__->_l_b_t_r_zy[_UP_8-1]= *_UP_1;
			f_zb_cp(_zb_q_x0, _UP_);
			*_rzad_p = _UP_8;
			__S_I_A_T__->__l_b_lewo = *_UP_1;
			if (_UP_4 < *_UP_1)
				*_znala_p = 1;
		}
	}
	if (_ABS_i_l_e_(_UP_)> 0) {
		U__P_(_UP_->V__k_e_t__[_UP_->V__ILE-1]);
		_ABS_i_l_e_(_UP_)--;
	}
}
void _daj_k_(int wag_a, int _UP_2, int * rzad, int _UP_3, __M_C__ ** _p_stos,
		T__Z_B_I__ * xZbp, T__Z_B_I__ * _zb_q_x0) {
	short *masz= (short*) calloc(1, sizeof(short));
	int qold= *rzad, *xlb;
	*masz=0;
	xlb = (int*) calloc(1, sizeof(int));
	*xlb = 0;
	_roz_klizb_(masz, wag_a, _UP_2, rzad, xlb, _ABS_i_l_e_(xZbp), _UP_3, _p_stos, xZbp, _zb_q_x0);
	if (qold != *rzad)
		__S_I_A_T__->_l_b_t_r_zy[_UP_3-1]= *xlb;
	free(xlb);
	free(masz);
}
int _daj_mi_l_b_(int malo_waz, int siat_ilo, int mala_odl, int dol_odl,
		__M_C__ ** stosu, T__Z_B_I__ * xZbp, T__Z_B_I__ * _zb_q_x0) {
	int lp_1=1, i;
	_ABS_i_l_e_(xZbp) = 0;
	_ABS_i_l_e_(_zb_q_x0) = 0;
	__S_I_A_T__->__l_b_lewo = 0;
	if ((mala_odl<2)&&(0<dol_odl)) {
		__S_I_A_T__->_l_b_t_r_zy[0]=0;
		U__P_(xZbp->V__ILE-1);
		_daj_k_(malo_waz, siat_ilo, &lp_1, 1, stosu, xZbp, _zb_q_x0);
	}
	if ((mala_odl<3)&&(1<dol_odl)) {
		_ABS_i_l_e_(__G_q_z_x) = 0;
		__S_I_A_T__->_l_b_t_r_zy[1]=0;
		U__P_(xZbp->V__ILE-1);
		_daj_k_(malo_waz, siat_ilo, &lp_1, 2, stosu, xZbp, _zb_q_x0);
	}
	if ((mala_odl<4)&&(2<dol_odl)) {
		_ABS_i_l_e_(xZbp) = 0;
		__S_I_A_T__->_l_b_t_r_zy[2]=0;
		U__P_(xZbp->V__ILE-1);
		_daj_k_(malo_waz, siat_ilo, &lp_1, 3, stosu, xZbp, _zb_q_x0);
	}
	_G__cl_n___++;

	for (i=0; i<_ABS_i_l_e_(_zb_q_x0); i++) {
		_W__E__(i,_zb_q_x0)->V__c_c__ = _G__cl_n___;
	}
	return (__S_I_A_T__->__l_b_lewo);
}
void _dop_siat_(void) {
	int i;
	for (i=0; i<_MOC_s_i_a_t_; i++)
		_D_o_p_u(i) = 1;
}
void _wlew_polc_uni_(void) {
	int i=0, j;
	for (j=0; j<_MOC_s_i_a_t_; j++)
		if (_C_T_n_F(j)) {
			_CALY_(i) = j;
			i++;
		}
	_ABS_i_l_e_(CALY) = i;
}
void _sekc_czb_(T__Z_B_I__ *_UP_0) {
	int i=0, j=0, lpk=0, rodw=0;
	T__Z_B_I__ * _LP_2;
	_LP_2 = __f_a_zb__(_ABS_i_l_e_(_UP_0));
	_ABS_i_l_e_(_LP_2) = _ABS_i_l_e_(_UP_0);
	for (i=0; i<_ABS_i_l_e_(_LP_2); i++) {
		Atom_(i,_LP_2) = _C_T_n__F_(i,_UP_0);
		lpk += _C_T_n__F_(i,_UP_0);
	}
	j += __N_o_c_o_l_;
	__N_o_c_o_l_ += lpk;
	while (j<__N_o_c_o_l_) {
		for (i=0; i<_ABS_i_l_e_(_LP_2); i++) {
			lpk = __S_I_A_T__->V__w_e_z[_CALY_(_UP_0->V__k_e_t__[i])]->__s_f__x__;
			if (Atom_(i,_LP_2)) {
				L__r_t_[j].V__N_I_x = _I_W_E_Z_E__L_(i,_UP_0);
				L__r_t_[j].V__r_n_o__ = _C_T_n__F_(i,_UP_0) - Atom_(i,_LP_2);
				if (rodw<lpk) {
					L__r_t_[j].V__r_f_n_o__ = __S_I_A_T__->V__w_e_z[_CALY_(_UP_0->V__k_e_t__[i])]->V__n_c_f_l__[rodw];
					L__r_t_[j].__b_r_f__ = 1;
				} else {
					L__r_t_[j].V__r_f_n_o__ = -1;
					L__r_t_[j].__b_r_f__ = 0;
				}
				Atom_(i,_LP_2)--;
				j++;
			}
		}
		rodw++;
	}
	Free_SET(_LP_2);
}
void _SLC__(int _UP_11) {
	int i;
	for (i=0; i<_MOC_s_i_a_t_; i++)
		_C_T_n_F(i) = _MIN_(_UP_11,_W_E_Z_S_F(i));
}
void _rozb_siat(int malo_waz, int ile_nos, int mala_odl, int odl_gran, int XCD) {
	int _lp_2=0;
	__N_o_c_o_l_=0;
	_dop_siat_();
	_SLC__(XCD);
	_wlew_polc_uni_();
	__G_p_u_s_t__y = _ABS_i_l_e_(CALY);
	while (__G_p_u_s_t__y) {
		_daj_mi_l_b_(malo_waz, ile_nos, mala_odl, odl_gran,
				__S_I_A_T__->V__r_e____, __G_q_z_x, _G_q_lew);
		IS__(_G_q_lew);
		_sekc_czb_(_G_q_lew);
	}
}
__inline int _daj_odl(int i, int j) {
	int lp1=0, was=0;
	R__p(__S_I_A_T__->V__r_e____,pytaIndex(i),pytaIndex(j),lp1)
	;
	if (lp1<_W_ma_l_)
		return (0);
	R__d(__S_I_A_T__->V__r_e____,pytaIndex(i),pytaIndex(j),was)
	;
	return (was);
}
void _przes_bduz(T__R_A_T_R__ * Zb_xN) {
	int i;
	for (i=0; i<__N_o_c_o_l_; i++)
		if (Zb_xN[i].V__r_f_n_o__ > 0)
			Zb_xN[i].V__r_f_n_o__ -= __S_I_A_T__->__l_b___;
	__S_I_A_T__->__u_b__ -= __S_I_A_T__->__l_b___;
	__S_I_A_T__->__l_b___ = 0;
}
__inline void _dop_siat(T__S_I_A__ *Zb_xN, int xix) {
	int lpk;
	for (lpk=1; lpk<Zb_xN->V__f_B_l__+1; lpk++)
		if (!Zb_xN->V__p_o_z_[pytaIndex(xix)][lpk])
			Zb_xN->V__p_o_z_[pytaIndex(xix)][lpk]=1;
}
__inline int _djamif_zbn(int was, int poa, int xix, int ul_) {
	int i=0, i1=0, i2=0, j_1= -1, j_2= -1, d_1=0, d_2=0;
	_G_Ff = -1;
	if (was == ul_) {
		for (i= ul_; (i < poa + 1)&&!d_1; i++) {
			switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
			case -1:
				i1++;
				break;
			case 1:
				j_1 = i;
				d_1=1;
				break;
			default:
				i1++;
				break;
			}
		}
		if (!d_1)
			return (-1);
		_G_Ff = j_1;
		return (i1);
	}
	if (poa == ul_) {
		for (i= ul_; (was-1 < i)&&!d_1; i--) {
			switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
			case -1:
				i1++;
				break;
			case 1:
				j_1 = i;
				d_1=1;
				break;
			default:
				i1++;
				break;
			}
		}
		if (!d_1)
			return (-1);
		_G_Ff = j_1;
		return (i1);
	}
	for (i= ul_; (i != (1-__G__R_DI__)*was + __G__R_DI__*poa + 2*__G__R_DI__
			- 1)&&!d_1; i = i+ 2*__G__R_DI__ - 1) {
		switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
		case -1:
			i1++;
			break;
		case 1:
			j_1 = i;
			d_1=1;
			break;
		default:
			i1++;
			break;
		}
	}
	if (!d_1)
		i1= poa-was+1;
	else if (!i1) {
		_G_Ff = j_1;
		return (0);
	}
	__G__R_DI__ = Bool_cos(__G__R_DI__);
	d_2 = 0;
	for (i= ul_; (i != (1-__G__R_DI__)*was + __G__R_DI__*poa + 2*__G__R_DI__
			- 1)&& !d_2; i = i+ 2*__G__R_DI__ - 1) {
		switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
		case -1:
			i2++;
			break;
		case 1:
			j_2 = i;
			d_2=1;
			break;
		default:
			i2++;
			break;
		}
	}
	__G__R_DI__ = Bool_cos(__G__R_DI__);
	if (!d_1&&!d_2)
		return (-1);
	if (!d_2)
		i2= poa-was+1;
	else if (!i2) {
		_G_Ff = j_2;
		return (0);
	}
	if (i1 < i2) {
		_G_Ff = j_1;
		return (i1);
	}
	_G_Ff = j_2;
	return (i2);
}
__inline void _dajmif_zb(int was, int poa, int xix) {
	int i;
	FREQ_O_L.frno = -1;
	FREQ_O_L.frdis = 0;
	for (i= __G__R_DI__*was + (1-__G__R_DI__)*poa; i != (1-__G__R_DI__)*was
			+ __G__R_DI__*poa + 2*__G__R_DI__ - 1; i = i+ 2*__G__R_DI__ - 1)
		switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
		case -1:
			FREQ_O_L.frdis++;
			break;
		case 1:
			FREQ_O_L.frno = i;
			__G__R_DI__ = Bool_cos(__G__R_DI__);
			return;
			break;
			break;
		default:
			break;
		}
}
__inline void _dajmif_zlew(int was, int xix) {
	int i;
	FREQ_O_L.frno = __S_I_A_T__->V__f_B_l__;
	FREQ_O_L.frdis = 0;
	for (i=was; _MAX_(0,__S_I_A_T__->__u_b__-V__f_B_l__l+1)<=i; i--)
		switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
		case -1:
			FREQ_O_L.frdis++;
			break;
		case 1:
			FREQ_O_L.frno = i;
			return;
			break;
		default:
			break;
		}
}
__inline void _dajmif_zu(int was, int xix) {
	int i;
	FREQ_O_R.frno = -1;
	FREQ_O_R.frdis = 0;
	for (i=was; i<_MIN_(__S_I_A_T__->V__f_B_l__,__S_I_A_T__->__l_b___+V__f_B_l__l); i++)
		switch (__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) {
		case -1:
			FREQ_O_R.frdis++;
			break;
		case 1:
			FREQ_O_R.frno = i;
			return;
			break;
		default:
			break;
		}
}
__inline int _d_zle(int xix, int n) {
	int was = _daj_odl(xix, n);
	was = _G_F_(n) - was;
	return (was);
}
__inline int _d_zpra(int xix, int n) {
	int was = _daj_odl(xix, n);
	was = _G_F_(n) + was;
	return (was);
}
__inline void _polu_(int was, int poa, int xix) {
	int i;
	for (i=was+1; i<poa; i++) {
		if ((i<0)||(__S_I_A_T__->V__f_B_l__ - 1 < i))
			continue;
		__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]=
		__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]* (1 - __S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) / 2;
		if (__S_I_A_T__->V__f_B_l__> i+_Z_L_int)
			__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1+_Z_L_int]=
			__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1+_Z_L_int]* (1 - __S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1+_Z_L_int]) / 2;
		if (i-_Z_L_int > 0)
			__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1-_Z_L_int]=
			__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1-_Z_L_int]* (1 - __S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1-_Z_L_int]) / 2;
	}
}
__inline void _polu(int was, int poa, int xix) {
	int i;
	for (i=was+1; i<poa; i++) {
		if ((i<0)||(__S_I_A_T__->V__f_B_l__ - 1 < i))
			continue;
		__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]=
		__S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]* (1 - __S_I_A_T__->V__p_o_z_[pytaIndex(xix)][i+1]) / 2;
	}
}
__inline void _w_(int xix, T__Z_B_I__ * Zb_x) {
	int i=0, rodw, lodw, przyp=_G_u_pa;
	while (i<Zb_x->V__ILE) {
		rodw = _d_zle(xix, Zb_x->V__k_e_t__[i]);
		lodw = _d_zpra(xix, Zb_x->V__k_e_t__[i]);
		if (przyp && ((rodw < __S_I_A_T__->__l_b___)||(lodw
				> __S_I_A_T__->__u_b__)))
			_polu_(rodw, lodw, xix);
		else
			_polu(rodw, lodw, xix);
		i++;
	}
}
int _oto_zc(int xix, T__Z_B_I__ * Zb_x) {
	int was, poa, x;
	_dop_siat(__S_I_A_T__,xix);
	_w_(xix, Zb_x);
	_dajmif_zb(__S_I_A_T__->__l_b___,__S_I_A_T__->__u_b__,xix);
	if (FREQ_O_L.frno >= 0)
		return (0);
	_dajmif_zlew(__S_I_A_T__->__l_b___,xix);
	was=FREQ_O_L.frno;
	_dajmif_zu(__S_I_A_T__->__u_b__,xix);
	poa=FREQ_O_R.frno;
	if (was != __S_I_A_T__->V__f_B_l__) {
		if (poa != -1) {
			x = _MIN_((__S_I_A_T__->__l_b___ - was - FREQ_O_L.frdis),(poa - __S_I_A_T__->__u_b__ - FREQ_O_R.frdis));
			if ((__S_I_A_T__->__l_b___ - was - FREQ_O_L.frdis) < (poa
					- __S_I_A_T__->__u_b__ - FREQ_O_R.frdis))
				poa = __S_I_A_T__->__u_b__ + 1 - was;
			else
				poa = poa + 1 - __S_I_A_T__->__l_b___;
			if (poa>V__f_B_l__l)
				return (0);
			return (x);
		} else {
			x = __S_I_A_T__->__l_b___ - was - FREQ_O_L.frdis;
			poa = __S_I_A_T__->__u_b__ + 1 - was;
			if (poa>V__f_B_l__l)
				return (0);
			return (x);
		}
	} else {
		if (poa != -1) {
			x = poa - __S_I_A_T__->__u_b__ - FREQ_O_R.frdis;
			poa = poa + 1 - __S_I_A_T__->__l_b___;
			if (poa>V__f_B_l__l)
				return (0);
			return (x);
		} else {
			return (0);
		}
	}
}
int _oto_zlew(int xix, T__Z_B_I__ * Zb_x) {
	int x;
	_G_F_(xix) = -1;
	x = _G_F_(Zb_x->V__k_e_t__[Zb_x->V__ILE-1]);
	if (x < 0)
		return (-1);
	_dop_siat(__S_I_A_T__,xix);
	_w_(xix, Zb_x);
	x = _djamif_zbn(__S_I_A_T__->__l_b___,__S_I_A_T__->__u_b__,xix,x);
	return (x);
}
int _r_trz_zx(int ni_v, T__Z_B_I__ * Zb_x) {
	_dop_siat(__S_I_A_T__,ni_v);
	_G_F_(ni_v) = -1;
	_w_(ni_v, Zb_x);
	_dajmif_zb(__S_I_A_T__->__l_b___,__S_I_A_T__->__u_b__,ni_v);
	if (FREQ_O_L.frno < 0)
		return (0);
	_G_F_(ni_v) = FREQ_O_L.frno;
	_T_W_E_S_F(pytaIndex(ni_v))--;
	return (1);
}
void _r_dw_a(int ni_v, T__Z_B_I__ * Zb_x) {
	_dop_siat(__S_I_A_T__,ni_v);
	_G_F_(ni_v) = -1;
	_w_(ni_v, Zb_x);
	_dajmif_zlew(__S_I_A_T__->__l_b___,ni_v);
	_dajmif_zu(__S_I_A_T__->__u_b__,ni_v);
	if (FREQ_O_L.frno < __S_I_A_T__->V__f_B_l__) {
		if (FREQ_O_R.frno > -1) {
			if ((__S_I_A_T__->__l_b___ - FREQ_O_L.frno - FREQ_O_L.frdis)
					< (FREQ_O_R.frno - __S_I_A_T__->__u_b__ - FREQ_O_R.frdis)) {
				_G_F_(ni_v) = FREQ_O_L.frno;
				__S_I_A_T__->__l_b___ = _MIN_(__S_I_A_T__->__l_b___,FREQ_O_L.frno);
				_T_W_E_S_F(pytaIndex(ni_v))--;
				__G_f_b_d = 1 && ((__S_I_A_T__->__u_b__ - FREQ_O_L.frno + 1)
						< V__f_B_l__l);
				return;
			}
			_G_F_(ni_v) = FREQ_O_R.frno;
			__S_I_A_T__->__u_b__ = _MAX_(__S_I_A_T__->__u_b__,FREQ_O_R.frno);
			_T_W_E_S_F(pytaIndex(ni_v))--;
			__G_f_b_d = 1 && ((FREQ_O_R.frno - __S_I_A_T__->__l_b___)
					< V__f_B_l__l);
			return;
		} else {
			_G_F_(ni_v) = FREQ_O_L.frno;
			__S_I_A_T__->__l_b___ = _MIN_(__S_I_A_T__->__l_b___,FREQ_O_L.frno);
			_T_W_E_S_F(pytaIndex(ni_v))--;
			__G_f_b_d = 1 && ((__S_I_A_T__->__u_b__ - FREQ_O_L.frno + 1)
					< V__f_B_l__l);
			return;
		}
	}
	_G_F_(ni_v) = FREQ_O_R.frno;
	__S_I_A_T__->__u_b__ = _MAX_(__S_I_A_T__->__u_b__,FREQ_O_R.frno);
	_T_W_E_S_F(pytaIndex(ni_v))--;
	__G_f_b_d = 1
			&& ((FREQ_O_R.frno - __S_I_A_T__->__l_b___ + 1) < V__f_B_l__l);
}
unsigned _r__G__(char *_UP_) {
	int i, _lP_1=0;
	char *lx, *pj, _LP_2[L_N_M_x+1];
	FILE *fp;
	_G_NaF = 0;
	lx = (char*) calloc(L_L_M_x+1,sizeof(char));
	if ((fp =fopen(_UP_, "r")) == NULL) {
		printf("ERROR by open filr %s", _UP_);;
		return (0);
	}
	while (fgets(lx, L_L_M_x, fp) != NULL) {
		if (lx[0]!= '!')
			_lP_1++;
	}
	rewind(fp);
	__S_I_A_T__ = __f_a_sia__(_lP_1,_X__r_c);
	__S_I_A_T__->V__ILE = 0;
	__S_I_A_T__->V__n_n_f_x__ = 0;
	while (fgets(lx, L_L_M_x, fp) != NULL) {
		if (lx[0]!= '!') {
			__f_c_sst__(lx, "  ", " ");
			__f_c_sst__(lx, "\t\t", "\t");
			__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__s_t_r_n__[0]= '\0';
			__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__c_e_n__ = '0';
			sscanf(lx, "%s", __W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__s_t_r_n__);
			pj = lx;
			pj = pj + strlen(__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__s_t_r_n__)+1;
			sscanf(pj, "%s", _LP_2);
			pj = pj + strlen(_LP_2)+1;
			sscanf(_LP_2, "%d", &__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__c_n__);
			sscanf(pj, "%s", _LP_2);
			pj = pj + strlen(_LP_2)+1;
			sscanf(_LP_2, "%d", &__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->_x_s_s_);
			sscanf(pj, "%s", _LP_2);
			pj = pj + strlen(_LP_2)+1;
			sscanf(_LP_2, "%d", &_W_E_Z_S_F(__S_I_A_T__->V__ILE));
			__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->__s_f__x__=0;
			__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->___s_f_a=0;
			if (!(_W_E_Z_S_F(__S_I_A_T__->V__ILE)> 0)) {
				continue;
			}
			_G_NaF += _W_E_Z_S_F(__S_I_A_T__->V__ILE);
			if (1 == sscanf(pj, "%s", _LP_2)) {
				pj = pj + strlen(_LP_2)+1;
				if (1 == sscanf(_LP_2, "%d", &__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->__s_f__x__)) {
					if (__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->__s_f__x__) {
						__S_I_A_T__->V__n_n_f_x__ += __W_E_Z_E_L_(__S_I_A_T__->V__ILE)->__s_f__x__;
						for (i=0; i<__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->__s_f__x__; i++) {
							if (1 != sscanf(pj, "%s", _LP_2)) {
								return (0);
							}
							pj = pj + strlen(_LP_2)+1;
							sscanf(_LP_2, "%d", &__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__n_c_f_l__[i]);
							__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__n_c_f_l__[i]-= __G_p_o_p;
						}
					}
				}
			}
			_W_E_Z_E_L_I_(__S_I_A_T__->V__ILE) = __S_I_A_T__->V__ILE;
			__W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__c_e_n__ = (char) ((int)'A' + __W_E_Z_E_L_(__S_I_A_T__->V__ILE)->V__c_n__ - 1);
			_T_W_E_S_F(__S_I_A_T__->V__ILE) = _W_E_Z_S_F(__S_I_A_T__->V__ILE);
			__S_I_A_T__->V__ILE++;
		}
	}
	free(lx);
	fclose(fp);
	return (1);
}
void _a_cI__(T__S_I_A__ * _Up_1) {
	int i;

	_Up_1->V__f_B_l__ = 2*(V__f_B_l__l+_MAX_(_X__o_d_,_G__D__s)) + 5;
	_Up_1->V__p_o_z_ = (P_2_(short) *) calloc(_Up_1->V__ILE, sizeof(P_2_(short)));
	Error_Msg_ALLOC(_Up_1->V__p_o_z_,"AllocErr by alloc __S_I_A_T__->ENAB",exit(0);)
	for (i=0; i<_Up_1->V__ILE; i++) {
		_Up_1->V__p_o_z_[i]= (short *) calloc(_Up_1->V__f_B_l__+1,
				sizeof(short));
		Error_Msg_ALLOC(_Up_1->V__p_o_z_[i],"AllocErr by alloc __S_I_A_T__->V__p_o_z_[]",exit(0);)
	}
}
void init_comp_matrix(T__S_I_A__ *Zb_xN, __M_C__ **_up_3) {
	int ni_v = -1, lp_4 = -1;
	for (ni_v=0; ni_v<Zb_xN->V__ILE; ni_v++)
		for (lp_4=ni_v; lp_4<__S_I_A_T__->V__ILE; lp_4++) {
			strcpy(_up_3[ni_v][lp_4], _I_V_R);
			strcpy(_up_3[lp_4][ni_v], _I_V_R);
		}
}
void _iGD__(T__S_I_A__ *Zb_xN, __M_C__ **_up_3) {
	int ni_v = -1, lpk;
	_G__cl_n___=0;
	for (ni_v=0; ni_v<Zb_xN->V__ILE; ni_v++) {
		Zb_xN->V__p_o_z__[ni_v]= 1;
		Zb_xN->V__w_e_z[ni_v]->CO_Int = 0;
		Zb_xN->V__w_e_z[ni_v]->AD_Int = 0;
		Zb_xN->V__w_e_z[ni_v]->V__c_c__ = 0;
		Zb_xN->V__w_e_z[ni_v]->CO_Int_Ave = 0;
	}
	init_comp_matrix(Zb_xN, _up_3);
	for (ni_v=0; ni_v<Zb_xN->V__ILE; ni_v++) {
		Zb_xN->V__p_o_z_[ni_v][0]= Zb_xN->V__f_B_l__;
		for (lpk=0; lpk<Zb_xN->V__f_B_l__; lpk++)
			Zb_xN->V__p_o_z_[ni_v][lpk+1]= 0;
	}
}
void _b_c_o__(void) {
	int lp_3, lp_;
	for (lp_3=0; lp_3<__S_I_A_T__->V__ILE; lp_3++)
		for (lp_=0; lp_<_G__N_c; lp_++)
			__S_I_A_T__->V__p_o_z_[lp_3][__G__C_l[lp_]-__G_p_o_p+1]= 1;
	for (lp_3=0; lp_3<__S_I_A_T__->V__ILE; lp_3++)
		for (lp_=0; lp_<__S_I_A_T__->V__f_B_l__; lp_++)
			__S_I_A_T__->V__p_o_z_[lp_3][lp_+1]-= 1;
}
void OrderNodesbyReqColors(void) {
	int i, j;
	T__w_e_z__ * x0;
	for (i=0; i<__S_I_A_T__->V__ILE; i++) {
		for (j=i+1; j<__S_I_A_T__->V__ILE; j++) {
			if (_W_E_Z_S_F(i) < _W_E_Z_S_F(j)) {
				x0 = __W_E_Z_E_L_(j);
				__W_E_Z_E_L_(j) = __W_E_Z_E_L_(i);
				__W_E_Z_E_L_(i) = x0;
			}
		}
		_W_E_Z_E_L_I_(i) = i;
	}
}
void _n_zb_o__(int _up_2, T__Z_B_I__ *_up_1) {
	int i=0;
	_up_1->V__ILE=_up_2;
	while (i<_up_1->V__ILE) {
		_up_1->V__k_e_t__[i]=i;
		i++;
	}
}
int __G_n__i__(char *Up_s, int Up_c) {
	int i;
	for (i=0; i<__S_I_A_T__->V__ILE; i++)
		if ( (__S_I_A_T__->V__w_e_z[i]->V__c_n__ != Up_c) || strcmp(Up_s,
				__S_I_A_T__->V__w_e_z[i]->V__s_t_r_n__) )
			continue;
		else

			return (__S_I_A_T__->V__w_e_z[i]->V__n_i_d_);
	return (-1);
}
unsigned __R_FC__(char *UP_F) {
	int i, j=0, _l_nf=0, lp_3= -1, _l_fb = -1;
	char *lx, *pj, _LP_2[L_N_M_x+1], _l_cs[L_N_M_x+1];
	FILE *fp;
	lx = (char*) calloc(L_L_M_x+1,sizeof(char));
	if ((fp =fopen(UP_F, "r")) == NULL) {
		printf("ERROR by open filr %s", UP_F);;
		return (0);
	}
	while (fgets(lx, L_L_M_x, fp) != NULL) {
		if (lx[0]!= '!') {
			__f_c_sst__(lx, "  ", " ");
			__f_c_sst__(lx, "\t\t", "\t");
			sscanf(lx, "%s", _l_cs);
			pj = lx;
			pj = pj + strlen(_l_cs)+1;
			sscanf(pj, "%s", _LP_2);
			pj = pj + strlen(_LP_2)+1;
			sscanf(_LP_2, "%d", &j);
			lp_3 = __G_n__i__(_l_cs, j);
			if (lp_3<0) {
				printf("!!ERROR in file %s: cell %s %d not found\n", UP_F,
						_l_cs, j);
				free(lx);
				fclose(fp);
				return (0);
			}
			sscanf(pj, "%s", _LP_2);
			pj = pj + strlen(_LP_2)+1;
			sscanf(_LP_2, "%d", &_l_nf);
			if (!_l_nf)
				continue;
			__S_I_A_T__->V__p_o_z_[lp_3][0]-= _l_nf;
			if (__S_I_A_T__->V__p_o_z_[lp_3][0]< __S_I_A_T__->V__w_e_z[lp_3]->V__n_c_f_r_) {
				printf(
						"!!!ERROR number of frequencies allowed=%d < %d=required for the cell %s %d\n",
						__S_I_A_T__->V__p_o_z_[lp_3][0],
						__S_I_A_T__->V__w_e_z[lp_3]->V__n_c_f_r_, _l_cs, j);
				free(lx);
				fclose(fp);
				return (0);
			}
			for (i=0; i<_l_nf; i++) {
				if (1 != sscanf(pj, "%s", _LP_2)) {
					printf("!!!ERROR reading file %s in lp_l %s\n", UP_F, lx);
					free(lx);
					fclose(fp);
					return (0);
				}
				pj = pj + strlen(_LP_2)+1;
				sscanf(_LP_2, "%d", &_l_fb);
				if (!(_l_fb-__G_p_o_p<0))
					__S_I_A_T__->V__p_o_z_[lp_3][_l_fb-__G_p_o_p+1]= -1;
			}
		}
	}
	free(lx);
	fclose(fp);
	return (1);
}
unsigned _czy_wyja(char *UP_F, int UP_E, __M_C__ ** _up_3) {
	int lp_2=0, lp_1=0, lp__2= -1, lp__1= -1, __lp1 = 0;
	char *lx, _ls_1[L_N_M_x+1], _ls_2[L_N_M_x+1];
	FILE *fp;
	lx = (char*) calloc(L_N_M_x+1,sizeof(char));
	if ((fp =fopen(UP_F, "r")) == NULL) {
		printf("ERROR by open filr %s", UP_F);;
		return (0);
	}
	while (fgets(lx, L_L_M_x, fp) != NULL) {
		if (lx[0]== '!')
			continue;
		__f_c_sst__(lx, "  ", " ");
		__f_c_sst__(lx, "\t\t", "\t");
		sscanf(lx, "%s %d %s %d %d", _ls_1, &lp_2, _ls_2, &lp_1, &__lp1);
		lp__2 = __G_n__i__(_ls_1, lp_2);
		lp__1 = __G_n__i__(_ls_2, lp_1);
		if ((lp__2<0)||(lp__1<0)) {
			printf("!!ERROR in file %s: on of cells %s %d,%s %d not found",
					UP_F, _ls_1, lp_2, _ls_2, lp_1);
			return (0);
		}
		if (!(__lp1<0)) {
			W__p(_up_3,lp__2,lp__1,UP_E);
			W__p(_up_3,lp__1,lp__2,UP_E);
			W__d(_up_3,lp__2,lp__1,__lp1);
			W__d(_up_3,lp__1,lp__2,__lp1);
		}
	}
	free(lx);
	fclose(fp);
	return (1);
}

void _domk_mat_nas(int _UP_5, int _UP_1, int _UP_3, int _l_t_s, int _UP_2,
		__M_C__ ** stosu) {
	int i, j, lpk, lp_2_=100, _lp_1=100, lp_c=0, _lp_c=0;
	long pj=0;
	for (i=0; i<__S_I_A_T__->V__ILE; i++) {
		for (j=0; j<__S_I_A_T__->V__ILE; j++) {
			if (!(j != i))
				continue;
			R__p(stosu,i,j,lp_2_)
			;

			if (lp_2_ < _UP_5)
				continue;
			for (lpk=0; lpk<__S_I_A_T__->V__ILE; lpk++) {
				if (!((lpk != i)&&(lpk != j)))
					continue;
				R__p(stosu,i,lpk,lp_2_)
				;
				R__p(stosu,lpk,i,_lp_1)
				;
				lp_2_ = _MAX_(lp_2_,_lp_1);
				if (lp_2_ < _UP_5)
					continue;

				R__p(stosu,j,lpk,lp_2_)
				;
				R__p(stosu,lpk,j,_lp_1)
				;
				lp_2_ = _MAX_(lp_2_,_lp_1);
				if (!(lp_2_ < _UP_1))
					continue;
				R__i(stosu,j,lpk,lp_c)
				;
				R__i(stosu,lpk,j,_lp_c)
				;
				lp_c =_MAX_(lp_c,_lp_c);
				if (!(_l_t_s < lp_c))
					continue;
				lp_c = _MIN_(M_I_x,lp_c+_UP_2);
				pj++;
				W__i(stosu,j,lpk,lp_c);
				W__i(stosu,lpk,j,lp_c);
				W__d(stosu,j,lpk,_UP_3);
				W__d(stosu,lpk,j,_UP_3);
				W__p(stosu,j,lpk,_UP_1);
				W__p(stosu,lpk,j,_UP_1);
				if (lp_c)
					V__p_p_s_i_e_[_UP_5].__c_it_++;
			}
		}
	}
}

void _pol_mat_cit(int up_cp, int _UP_3, int _up_ct, __M_C__ ** _up_3) {
	int i1=0, ni_v = -1;
	for (ni_v=0; ni_v<__S_I_A_T__->V__ILE; ni_v++) {
		R__p(_up_3,ni_v,ni_v,i1)
		;
		if (!(i1 < up_cp))
			continue;
		W__p(_up_3,ni_v,ni_v,up_cp);
		W__d(_up_3,ni_v,ni_v,_UP_3);
		W__i(_up_3,ni_v,ni_v,_up_ct);
		if (_up_ct)
			V__p_p_s_i_e_[_P_c].__c_it_++;
	}
}
void _pol_mat_sit(int _UP_p, int _UP_3, int _up_ct, __M_C__ ** _up_3) {
	int i1=0, i2=0, ni_v = -1, lp_4 = -1;
	for (ni_v=0; ni_v<__S_I_A_T__->V__ILE; ni_v++)
		for (lp_4=ni_v+1; lp_4<__S_I_A_T__->V__ILE; lp_4++) {
			R__p(_up_3,ni_v,lp_4,i1)
			;
			if (!(i1 < _UP_p))
				continue;
			if ( !strcmp(__S_I_A_T__->V__w_e_z[ni_v]->V__s_t_r_n__,__S_I_A_T__->V__w_e_z[lp_4]->V__s_t_r_n__) ) {
				W__p(_up_3,ni_v,lp_4,_UP_p);
				W__p(_up_3,lp_4,ni_v,_UP_p);
				W__d(_up_3,ni_v,lp_4,_UP_3);
				W__d(_up_3,lp_4,ni_v,_UP_3);
				W__i(_up_3,ni_v,lp_4,_up_ct);
				W__i(_up_3,lp_4,ni_v,_up_ct);
				V__p_p_s_i_e_[_P_s].__c_it_++;
			}
		}
}
unsigned _czy_sasia(char *pf_name, int UP_p_, int UP_o_, int UP_i,
		__M_C__ ** _up_3) {
	char lp_l[L_L_M_x+1], lp_cs[5], lp_ss[L_N_M_x+1], lp_s[L_N_M_x+1];
	FILE *fp;
	int pj=0, l_p=0, lp_c_= 0, lp_c=0, ni_v = -1, lp_4 = -1, i1, i2;
	lp_ss[0]='\0';

	if ((fp =fopen(pf_name, "r")) == NULL) {
		printf("ERROR by open filr %s", pf_name);;
		return (0);
	}
	while (fgets(lp_l, L_L_M_x, fp) != NULL) {
		if (lp_l[0]== '!')
			continue;
		sscanf(lp_l, "%s %s %d", lp_cs, lp_s, &lp_c);
		if ((lp_cs[0]== 'N')) {
			lp_4 = -1;
			lp_4 = __G_n__i__(lp_s, lp_c);
			if (lp_4<0) {
				printf("!!ERROR in file %s: cell %s %d not found\n", pf_name,
						lp_s, lp_c);
				return (0);
			}
			if (ni_v != lp_4) {
				R__p(_up_3,ni_v,lp_4,l_p)
				;
				if (!(l_p < UP_p_))
					continue;
				W__p(_up_3,ni_v,lp_4,UP_p_);
				W__p(_up_3,lp_4,ni_v,UP_p_);
				W__d(_up_3,ni_v,lp_4,UP_o_);
				W__d(_up_3,lp_4,ni_v,UP_o_);
				R__i(_up_3,ni_v,lp_4,i1)
				;
				R__i(_up_3,lp_4,ni_v,i2)
				;
				i1 = _MAX_(i1,i2);
				i1 += UP_i;
				i1 = _MIN_(i1,M_I_x);
				W__i(_up_3,ni_v,lp_4,i1);
				W__i(_up_3,lp_4,ni_v,i1);
				V__p_p_s_i_e_[_P_n].__c_it_++;
			} else {
			}
		} else {
			if (lp_cs[0]!= 'C') {
				printf("!!ERROR in file %s\n", pf_name);
				return (0);
			} else {
				if ( !strcmp(lp_ss, lp_s) && (lp_c_==lp_c))
					continue;
				strcpy(lp_ss, lp_s);
				lp_c_ = lp_c;
				ni_v = -1;
				ni_v = __G_n__i__(lp_ss, lp_c_);
				if (ni_v<0) {
					printf("!!ERROR in file %s: cell %s %d not found\n",
							pf_name, lp_ss, lp_c_);
					return (0);
				}
			}
		}
	}
	fclose(fp);
	return (1);
}
unsigned _czy_zaklum(int U_P_n, char *ITFile_name, __M_C__ ** _up_3, int U_P_d) {
	char lx[L_L_M_x+1], _lp_si[L_N_M_x+1], _lp_s[L_N_M_x+1], _lp_c[8],
			__lp_c[L_N_M_x+1];
	FILE *fp;
	int ip_s, ip_i=0, ni_v = -1, lp_4 = -1, lk=0, lip_f, Lp_c, Lp_c_=0, Lp__c_=
			0;
	double lp1, lp2, lp4, lp3, Was = 0.0, Tas = 0.0;
	if ((fp =fopen(ITFile_name, "r")) == NULL) {
		printf("ERROR by open filr %s", ITFile_name);;
		return (0);
	}
	while (fgets(lx, 300, fp) != NULL) {
		if ((lx[0]!= 'S') && (lx[0]!= 'I'))
			continue;
		if (lx[0]!= 'S') {
			sscanf(lx, "%s %s %d %lf %lf %lf %lf %s", _lp_c, __lp_c, &lk, &lp1,
					&lp2, &lp4, &lp3, _lp_si);
			ip_i = ( (int)_lp_si[strlen(_lp_si)-1]- (int)'A' ) + 1;
			_lp_si[strlen(_lp_si)-1]= '\0';
			ni_v = -1;
			ni_v = __G_n__i__(_lp_si, ip_i);
			if (ni_v<0) {
				continue;
			}
			R__p(_up_3,lp_4,ni_v,Lp_c)
			;
			if (Lp_c > U_P_n)
				continue;

			if (_G__U_t>1) {
				lp2 = lp1;
				lp3 = lp4;
			}
			if (Tas > 0.0) {
				Lp_c=0;
				lp2 = lp2/Tas;
				Lp_c = _MIN_(M_I_x,_MAX_(0,(int) ceil(lp2 * ((double)M_I_c) )));
			} else {
				Lp_c = 0;
				_W_e_g_it++;
			}
			R__i(_up_3,ni_v,lp_4,Lp_c_)
			;
			Lp_c = _MAX_(Lp_c_,Lp_c);
			W__i(_up_3,ni_v,lp_4,Lp_c);
			W__i(_up_3,lp_4,ni_v,Lp_c);

			W__p(_up_3,lp_4,ni_v,U_P_n);
			W__p(_up_3,ni_v,lp_4,U_P_n);
			W__d(_up_3,lp_4,ni_v,U_P_d);
			W__d(_up_3,ni_v,lp_4,U_P_d);
		} else {
			sscanf(lx, "%s %s %d %lf %lf %d %s", _lp_c, __lp_c, &lk, &Was,
					&Tas, &lip_f, _lp_s);
			if (_G__U_t>1)
				Tas = Was;
			if ((Tas > 0.0) && (lip_f > 0)) {
				ip_s = ( (int)_lp_s[strlen(_lp_s)-1]- (int)'A' ) + 1;
				_lp_s[strlen(_lp_s)-1]= '\0';
				lp_4 = -1;
				lp_4 = __G_n__i__(_lp_s, ip_s);
				if (lp_4<0) {
					fclose(fp);
					return (0);
				}
			} else {
				printf(
						"ERROR: AREA/TRAFFIC val_ %lf or number of interferers not valid in the file %s in lp_l:\n",
						Tas, lip_f, ITFile_name);
				printf("       %s\n", lx);
				fclose(fp);
				return (0);
			}
		}
	}
	fclose(fp);
	return (1);
}
void _robsy_cit_(__M_C__ ** _up_3) {
	int i1=0, i2=0, ni_v = -1, lp_4 = -1;
	for (ni_v=0; ni_v<__S_I_A_T__->V__ILE; ni_v++)
		for (lp_4=ni_v; lp_4<__S_I_A_T__->V__ILE; lp_4++) {
			i1=0;
			i2=0;
			R__i(_up_3,ni_v,lp_4,i1)
			;
			R__i(_up_3,lp_4,ni_v,i2)
			;
			if (i1 < i2)
				W__i(_up_3,ni_v,lp_4,i2);
			if (i2 < i1)
				W__i(_up_3,lp_4,ni_v,i1);
		}
}
short _czy_stos_o(int U_P_n, char * up_1, __M_C__ ** stosu, int up_4) {
	if (!_czy_zaklum(U_P_n, up_1, stosu, up_4))
		return (0);
	_robsy_cit_(stosu);
	return (1);
}
void _podzia_fx(T__R_A_T_R__ * up_2, T__Z_B_I__ * sp_2, T__Z_B_I__ * up_1) {
	int i=0, j=0;
	while ((i<__S_I_A_T__->V__n_n_f_x__)&&(j<__N_o_c_o_l_)) {
		if (up_2[up_1->V__k_e_t__[j]].__b_r_f__) {
			sp_2->V__k_e_t__[sp_2->V__ILE++]=up_1->V__k_e_t__[j];
			f_skok_el(j, up_1);
			i++;
		} else
			j++;
	}
}

void _domk_nacozak(__M_C__ ** stosu) {
	int i, j, lpb=0, lpa=0;
	double pj=0.0;
	_G_co_d_o_ro_b = 0;
	for (i=0; i<_ABS_i_l_e_(__S_I_A_T__); i++)
		for (j=i; j<_ABS_i_l_e_(__S_I_A_T__); j++) {
			R__i(stosu,i,j,lpb)
			;
			R__i(stosu,j,i,lpa)
			;
			if (lpb+lpa) {
				_G_co_d_o_ro_b++;
			}
		}

	pj = (double) _G_co_d_o_ro_b;
	lpb = (int) ((pj-1) / 100);
	lpb = 1 + (100-_G__Q__)*lpb;
	lpb = _MAX_(lpb,1);
	_G_k_r_o_k_min = _MIN_(_G_co_d_o_ro_b,lpb);
}
int _pol_tymcz_zakl(int up_l, int l_p, int _l_t_s, __M_C__ ** stosu) {
	int _U_1=0, i, j, lp_2_=100, _lp_1=100;
	for (i=0; (i<_ABS_i_l_e_(__S_I_A_T__))&&(_U_1<up_l); i++) {
		for (j=i; (j<_ABS_i_l_e_(__S_I_A_T__))&&(_U_1<up_l); j++) {
			R__p(stosu,i,j,lp_2_)
			;
			if (lp_2_ != l_p)
				continue;
			R__i(stosu,i,j,lp_2_)
			;

			if (_l_t_s != lp_2_)
				continue;
			W__p(stosu,i,j,_P_t);
			W__p(stosu,j,i,_P_t);
			_U_1++;
		}
	}
	return (_U_1);
}
int _pol_tymcz_p(int up_l, int l_p, int up_D, int up_d, __M_C__ ** stosu) {
	int i, j, moc=0, lp_2_=100, _lp_1=100;
	for (i=0; (i<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); i++) {
		for (j=i+1; (j<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); j++) {
			R__p(stosu,i,j,lp_2_)
			;
			if (lp_2_ != l_p)
				continue;
			R__d(stosu,i,j,lp_2_)
			;
			R__d(stosu,j,i,_lp_1)
			;
			lp_2_=_MIN_(lp_2_,_lp_1);
			if (lp_2_ != up_D)
				continue;
			W__d(stosu,i,j,up_d);
			W__d(stosu,j,i,up_d);
			W__p(stosu,i,j,_P_t);
			W__p(stosu,j,i,_P_t);
			moc++;
		}
	}
	return (moc);
}
void _pol_p(int up_l, int UP_p_, __M_C__ ** stosu) {
	int i, j, lp_2_=100, moc=0;
	;
	for (i=0; (i<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); i++) {
		for (j=i; (j<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); j++) {
			R__p(stosu,i,j,lp_2_)
			;
			if (lp_2_ != _P_t)
				continue;
			W__p(stosu,i,j,UP_p_);
			W__p(stosu,j,i,UP_p_);
			moc++;
		}
	}
}
int _wycie_oba(int up_l, int UP_p_, int _UP_3, __M_C__ ** stosu) {
	int i, j, lp_2_=100, moc=0;
	;
	for (i=0; (i<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); i++) {
		for (j=i; (j<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); j++) {
			R__p(stosu,i,j,lp_2_)
			;
			if (lp_2_ != _P_t)
				continue;
			W__d(stosu,i,j,_UP_3);
			W__d(stosu,j,i,_UP_3);
			W__p(stosu,i,j,UP_p_);
			W__p(stosu,j,i,UP_p_);
			moc++;
		}
	}
	return (moc);
}
int _ponow_p_(int up_l, int up2, int _UP_3, __M_C__ ** stosu) {
	int i, j, lp_2_=100, moc=0;
	;
	for (i=0; (i<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); i++) {
		for (j=i+1; (j<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); j++) {
			R__p(stosu,i,j,lp_2_)
			;
			if (lp_2_ != _P_t)
				continue;
			W__p(stosu,i,j,up2);
			W__p(stosu,j,i,up2);
			W__d(stosu,i,j,_UP_3);
			W__d(stosu,j,i,_UP_3);
		}
	}
	return (moc);
}
int _ponow_p(int up1, int up3, int up_l, int up2, int UP_p_, int _UP_3,
		int _l_t_s, __M_C__ ** stosu) {
	int moc=0, i, j, lp_2_=100;
	for (i=0; (i<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); i++) {
		for (j=i+1; (j<_ABS_i_l_e_(__S_I_A_T__))&&(moc<up_l); j++) {
			R__p(stosu,i,j,lp_2_)
			;
			if (lp_2_ != _P_t)
				continue;
			W__d(stosu,i,j,_UP_3);
			W__d(stosu,j,i,_UP_3);
			if (!up1) {
				W__p(stosu,i,j,up2);
				W__p(stosu,j,i,up2);
			} else {
				R__i(stosu,i,j,lp_2_)
				;
				if (lp_2_ > _l_t_s) {
					W__p(stosu,i,j,up2);
					W__p(stosu,j,i,up2);
					W__i(stosu,i,j,(lp_2_-_l_t_s));
					W__i(stosu,j,i,(lp_2_-_l_t_s));
				} else {
					W__p(stosu,i,j,UP_p_);
					W__p(stosu,j,i,UP_p_);
				}
			}
			moc++;
		}
	}
	return (moc);
}
double DET(double Was, double B, double was, double poa) {
	return ((Was*poa)-(B*was));
}
double V_l(double ux, double uy) {
	return (sqrt(P_2(ux)+P_2(uy)));
}
double SPV_(double ux, double uy, double vx, double vy) {
	return ((ux*vx) + ((uy)*(vy)));
}
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

double LCut_x(double Was, double B, double _j, double was, double poa,
		double _up_2) {
	return ((-(poa)*(_j) + (B)*(_up_2)) / DET(Was, B, was, poa));
}
double LCut_y(double Was, double B, double _j, double was, double poa,
		double _up_2) {
	return ((-(Was)*(_up_2) + (was)*(_j)) / DET(Was, B, was, poa));
}
double JDIS_(double p0x, double p0y, double p1x, double p1y, double p2x,
		double p2y, double al) {
	double D=0.0;
	D = _MAX_(DIS_(p0x,p0y,p1x,p1y),DIS_(p0x,p0y,p2x,p2y));
	return (D / al );
}
unsigned IsCut_HL(double az1, double az2, double p1x, double p1y, double q1x,
		double q1y, double p2x, double p2y, double q2x, double q2y) {
	double Was=A_L(p1x, p1y, q1x, q1y), B=B_L(p1x, p1y, q1x, q1y), _j=C_L(p1x,
			p1y, q1x, q1y), was=A_L(p2x, p2y, q2x, q2y), poa=B_L(p2x, p2y, q2x,
			q2y), _up_2=C_L(p2x, p2y, q2x, q2y), D=DET(Was, B, was, poa), X0,
			Y0;
	if (D != 0.0) {
		X0=LCut_x(Was, B, _j, was, poa, _up_2), Y0=LCut_y(Was, B, _j, was, poa,
				_up_2);
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
		printf("ERROR\n");
	ALPH = 1.0+AV_((p1x-long1), (p1y-lat1), (p2x-long2), (p2y-lat2));
	if ( BETP_(long1,lat1,long2,lat2,p2_60l_x,p2_60l_y,p2_60r_x,p2_60r_y) ||
	BETP_(long2,lat2,long1,lat1,p1_60l_x,p1_60l_y,p1_60r_x,p1_60r_y)) {
		D = D / ALPH;
		if (D < 0.0)
			printf("ERROR\n");
		return (D);
	}
	if (IsCut_HL((azimuth1-(J_PI_/3.0)), (azimuth2-(J_PI_/3.0)), long1, lat1,
			p1_60l_x, p1_60l_y, long2, lat2, p2_60l_x, p2_60l_y))
		MDIS
				= JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60l_x,p2_60l_y),long1,lat1,long2,lat2,ALPH);

	if (IsCut_HL((azimuth1-(J_PI_/3.0)), (azimuth2+(J_PI_/3.0)), long1, lat1,
			p1_60l_x, p1_60l_y, long2, lat2, p2_60r_x, p2_60r_y))
		MDIS
				= _MIN_(MDIS,JDIS_(Cut_x(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),Cut_y(long1,lat1,p1_60l_x,p1_60l_y,long2,lat2,p2_60r_x,p2_60r_y),long1,lat1,long2,lat2,ALPH));
	if (IsCut_HL((azimuth1+(J_PI_/3.0)), (azimuth2+(J_PI_/3.0)), long1, lat1,
			p1_60r_x, p1_60r_y, long2, lat2, p2_60r_x, p2_60r_y))
		MDIS
				= _MIN_(MDIS,JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60r_x,p2_60r_y),long1,lat1,long2,lat2,ALPH));
	if (IsCut_HL((azimuth1+(J_PI_/3.0)), (azimuth2-(J_PI_/3.0)), long1, lat1,
			p1_60r_x, p1_60r_y, long2, lat2, p2_60l_x, p2_60l_y))
		MDIS
				= _MIN_(MDIS,JDIS_(Cut_x(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),Cut_y(long1,lat1,p1_60r_x,p1_60r_y,long2,lat2,p2_60l_x,p2_60l_y),long1,lat1,long2,lat2,ALPH));

	return (_MIN_(MDIS,5.0*D));
	return (10.0*D);
}
int _blisk_do_ost1_(T__Z_B_I__ * _U2, T__Z_B_I__ * _U1) {
	int i=0, n, P0_, lp_, xix = -1;
	P0_ = __S_I_A_T__->V__f_B_l__+1;
	while (i < _U1->V__ILE) {
		lp_ = _U1->V__k_e_t__[i];
		n = _oto_zlew(lp_, _U2);
		if (n<0) {
			i++;
			continue;
		}
		if (!n) {
			_O_s_f_ = _G_Ff;
			return (i);
		}
		if (n < P0_) {
			_O_s_f_ = _G_Ff;
			xix = i;
			P0_ = n;
			i++;
			continue;
		}
		if (n == P0_) {
			if (_T_W_E_S_F(pytaIndex(lp_))> _T_W_E_S_F(pytaIndex(_U1->V__k_e_t__[xix]))) {
				_O_s_f_ = _G_Ff;
				xix = i;
			}
		}
		i++;
	}
	return (xix);
}
int _blisk_do_zb_(T__Z_B_I__ * _U2, T__Z_B_I__ * _U1) {
	int i=0, n, P0_, lp_, xix = -1;
	P0_ = __S_I_A_T__->V__f_B_l__;
	while (i < _U1->V__ILE) {
		lp_ = _U1->V__k_e_t__[i];
		n = _oto_zc(lp_, _U2);
		if (!n) {
			i++;
			continue;
		}
		if (n < 2)
			return (i);
		else if (n < P0_) {
			xix = i;
			P0_ = n;
		}
		i++;
	}
	return (xix);
}
unsigned _rozsz_wsteg_(T__Z_B_I__ * _UP_, T__Z_B_I__ * cX) {
	int i = -1;
	unsigned _zrob_=1, j=0;
	while (__G_f_b_d && _zrob_ && cX->V__ILE) {
		i = _blisk_do_zb_(_UP_, cX);
		if (-1 < i) {
			_r_dw_a(cX->V__k_e_t__[i], _UP_);
			_UP_->V__k_e_t__[_UP_->V__ILE++]=cX->V__k_e_t__[i];
			f_skok_el(i, cX);
			j = 1;
		} else
			_zrob_=0;
	}
	return (j);
}
unsigned _w_wsteg_(T__Z_B_I__ * _UP_, T__Z_B_I__ * cX) {
	int i = -1;
	unsigned _zrob_=1, j=0;
	while (_zrob_ && cX->V__ILE) {
		i = _blisk_do_ost1_(_UP_, cX);
		if (-1 < i) {
			_G_F_(cX->V__k_e_t__[i]) = _O_s_f_;
			_T_W_E_S_F(pytaIndex(cX->V__k_e_t__[i]))--;
			_UP_->V__k_e_t__[_UP_->V__ILE++]=cX->V__k_e_t__[i];
			f_skok_el(i, cX);
			j = 1;
		} else
			_zrob_=0;
	}
	return (j);
}
unsigned _wsad_w_wsteg_(T__Z_B_I__ * _UP_, T__Z_B_I__ * cX) {
	int xix=0;
	unsigned j=0, _zrob_=1;
	while (_zrob_ && (xix<cX->V__ILE) && cX->V__ILE) {
		if (_r_trz_zx(cX->V__k_e_t__[xix], _UP_)) {
			_UP_->V__k_e_t__[_UP_->V__ILE++]=cX->V__k_e_t__[xix];
			f_skok_el(xix, __G_c_z_x);
			j=1;
		} else
			_zrob_ = 0;
	}
	return (j);
}
int _porz_siat_(T__Z_B_I__ * _UP_, T__Z_B_I__ * up_1) {
	int i=__G__R_DI__;
	unsigned proc=1, p1=1, p2=1;
	while (proc) {
		proc = (__G_f_b_d && _rozsz_wsteg_(_UP_, up_1))
				|| _w_wsteg_(_UP_, up_1) || _wsad_w_wsteg_(_UP_, up_1);
	}
	return (up_1->V__ILE);
}
int _dajmi_ost_dop_(int lp_3) {
	int i;
	for (i=__S_I_A_T__->V__f_B_l__; 0<i; i--)
		if (__S_I_A_T__->V__p_o_z_[lp_3][i]>0)
			return (i-1);
	return (-1);
}
int _poli_fax_(T__R_A_T_R__ * Zb_xN, T__Z_B_I__ * sp_2, T__Z_B_I__ * up_1) {
	int i=0;
	if (!up_1->V__ILE)
		return (0);
	if (!__S_I_A_T__->V__n_n_f_x__) {
		_dop_siat(__S_I_A_T__,up_1->V__k_e_t__[0]);
		if ((i = _dajmi_ost_dop_(Zb_xN[up_1->V__k_e_t__[0]].V__N_I_x)) < 0) {
			return (1);
		}
		Zb_xN[up_1->V__k_e_t__[0]].V__r_f_n_o__ = i;
		sp_2->V__k_e_t__[sp_2->V__ILE++]=up_1->V__k_e_t__[0];
		f_skok_el(0, up_1);
		__G_f_b_d= 1 && ((__S_I_A_T__->__u_b__ - __S_I_A_T__->__l_b___)
				< V__f_B_l__l);
	}
	return (_porz_siat_(sp_2, up_1));
}
int _poli_fa_(T__R_A_T_R__ * Zb_xN, T__Z_B_I__ * sp_2, T__Z_B_I__ * up_1) {
	int i=0;
	sp_2->V__k_e_t__[0]=0;
	sp_2->V__ILE++;
	Zb_xN[0].V__r_f_n_o__ = __S_I_A_T__->V__f_B_l__ / 2;
	__S_I_A_T__->__l_b___ = Zb_xN[0].V__r_f_n_o__;
	__S_I_A_T__->__u_b__ = __S_I_A_T__->__l_b___;
	__G_f_b_d=1;
	f_zb_roz(up_1, sp_2);
	i = _porz_siat_(sp_2, up_1);
	_przes_bduz(Zb_xN);
	return (i);
}
int _prob_fac_(T__R_A_T_R__ * up_2, T__Z_B_I__ * sp_2, T__Z_B_I__ * up_1) {
	int i, j;
	for (i=0; i<__S_I_A_T__->V__ILE; i++)
		_T_W_E_S_F(i) = _W_E_Z_S_F(i);
	sp_2->V__ILE = 0;
	_n_zb_o__(__N_o_c_o_l_, up_1);
	i = _poli_fa_(up_2, sp_2, up_1);
	if (!i) {
		for (j=0; j<__N_o_c_o_l_; j++)
			*(L__R_t_+j) = *(up_2+j);
		_a_ss_B_ = 1;
	}
	return (i);
}
int _polep_powsio_i_(int up_l, int upi, int _UP_p, int up2, int up_D, int up_d,
		int _l_t_s, __M_C__ ** stosu) {
	int i=0, j=_l_t_s, up1=1;
	i = _pol_tymcz_zakl(up_l, upi, _l_t_s, stosu);
	if (!i)
		return (0);
	if (!_prob_fac_(L__r_t_, _G_q_lew, __G_c_z_x)) {
		_G_o_s_t_qc__ = i;
		_pol_p(i, _UP_p, stosu);
		return (1);
	}
	if (i < _G_k_r_o_k_min+1) {
		_wycie_oba(i, up2, _MAX_(1,up_d), stosu);
		return (2);
	} else
		_ponow_p_(i, upi, up_D, stosu);
	_G_o_s_t_qc__ = i;
	return (3);
}
void polu_kro_(int wag_a) {
	int i = _G_k_r_o_k_min, _K_ = 0, _L_, j;
	_W_ma_l_ = wag_a + 1;
	_K_ = M_I_x;
	while (V__p_p_s_i_e_[wag_a].__c_l__b__ < _K_) {
		_W__L=0;
		switch (_polep_powsio_i_(i, wag_a, _P_x, wag_a-1,
				V__p_p_s_i_e_[wag_a].__d__r, V__p_p_s_i_e_[wag_a].__d__r-1,
				_K_, __S_I_A_T__->V__r_e____)) {
		case 0:
			_K_--;
			i = _MAX_(i,_G_k_r_o_k_min);
			break;
		case 1:
			_W__L=1;
			_L_ = _K_;
			i = _MAX_(_G_k_r_o_k_min,2*_G_o_s_t_qc__);
			break;
		case 2:
			_L_ = _K_;
			break;
		default:
			_K_ = _L_;
			i = _MAX_(_G_k_r_o_k_min,_G_o_s_t_qc__/2);
			break;
		}
	}
	j = __G__R_DI__;
	if (_prob_fac_(L__r_t_, _G_q_lew, __G_c_z_x)) {
		__G__R_DI__ = 1-j;
		_prob_fac_(L__r_t_, _G_q_lew, __G_c_z_x);
	}
}
int _polep_powsio_(int up_l, int upi, int _UP_p, int up2, int up_D, int up_d,
		__M_C__ ** stosu) {
	int i;
	if (!up_l)
		return (0);
	i = _pol_tymcz_p(up_l, upi, up_D, up_d, stosu);
	if (!i)
		return (0);
	if (!_prob_fac_(L__r_t_, _G_q_lew, __G_c_z_x)) {
		_G_o_s_t_qc__ = i;
		_pol_p(i, _UP_p, stosu);
		return (1);
	}
	if (i < _G_k_r_o_k_min+1) {
		_ponow_p(1, 0, i, upi, up2, up_D, M_I_x, stosu);
		return (2);
	} else
		_ponow_p(0, 1, i, upi, up2, up_D, M_I_x, stosu);
	return (3);
}
unsigned _polu_wsio_(int wag_a) {
	int j=0;
	_W_ma_l_ = wag_a;
	if ((j=_prob_fac_(L__r_t_, _G_q_lew, __G_c_z_x))) {
		_o_f_pfa__(L__r_t_);
		return (0);
	}
	_o_f_pfa__(L__r_t_);
	return (1);
}

void _rob_wsio_(void) {
	int LOW_PRIO, lp1;
	if (!_G__P__) {
		IDform_Set(__S_I_A_T__->V__ILE,_G_q_lew);
		_sekc_czb_(_G_q_lew);
	} else {
		_rozb_siat(_P_n,_G__N_c,1,1,_X__r_c);
	}
	_domk_nacozak(__S_I_A_T__->V__r_e____);
	LOW_PRIO = _P_no;
	for (lp1=_P_c; (2 < lp1); lp1--) {
		if (!V__p_p_s_i_e_[lp1].__a__p)
			continue;
		if (!_polu_wsio_(lp1))
			polu_kro_(lp1);
	}
	_o_f_pfa__(L__r_t_);
	_o_act_p__(1);
}
short _i__d__(char * cont_fil) {
	int i=0, j=0, lp1;
	char ax[L_L_M_x], lx[L_L_M_x+1], *pj;
	FILE * ConfigFile;
	if ((ConfigFile =fopen(cont_fil, "r")) == NULL) {
		printf("ERROR by open filr %s", cont_fil);;
		return (0);
	}
	while ((i<13 )&& (fgets(lx, L_L_M_x, ConfigFile) != NULL)) {
		switch (i) {
		case 0:
			if ( (pj = strchr(lx, (int)('"'))) != NULL) {
				strcpy(__F_g, pj+1);
				if ( (pj = strchr(__F_g, (int)('"'))) != NULL)
					pj[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR by reading ConfigFile lp_l %d\n", i+1);
			break;
		case 1:
			sscanf(lx, "%s %d", ax, &_G__D__s);
			i++;
			continue;
			break;
		case 2:
			sscanf(lx, "%s %d", ax, &_G__D__c);
			i++;
			continue;
			break;
		case 3:
			sscanf(lx, "%s %d", ax, &_X__r_c);
			i++;
			continue;
			break;
		case 4:
			sscanf(lx, "%s %d", ax, &_G__D__nr);
			i++;
			continue;
			break;
		case 5:
			sscanf(lx, "%s %d", ax, &_G__D__sn);
			i++;
			continue;
			break;
		case 6:
			if ( (pj = strchr(lx, (int)('"'))) != NULL) {
				strcpy(__F_n, pj+1);
				if ( (pj = strchr(__F_n, (int)('"'))) != NULL)
					pj[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR by reading ConfigFile lp_l %d\n", i+1);
			break;
		case 7:
			sscanf(lx, "%s %d", ax, &_G__U_t);
			i++;
			continue;
			break;
		case 8:
			sscanf(lx, "%s %d", ax, &_B_i_n2);
			i++;
			continue;
			break;
		case 9:
			sscanf(lx, "%s %d", ax, &_G__Q__);
			i++;
			continue;
			break;
		case 16:
			if ( (pj = strchr(lx, (int)('"'))) != NULL) {
				strcpy(__F_p, pj+1);
				if ( (pj = strchr(__F_p, (int)('"'))) != NULL)
					pj[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR by reading ConfigFile lp_l %d\n", i+1);
			break;
		case 10:
			if ( (pj = strchr(lx, (int)('"'))) != NULL) {
				strcpy(__F_i, pj+1);
				if ( (pj = strchr(__F_i, (int)('"'))) != NULL)
					pj[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR by reading ConfigFile lp_l %d\n", i+1);
			break;
		case 11:
			if ( (pj = strchr(lx, (int)('"'))) != NULL) {
				strcpy(__F_l, pj+1);
				if ( (pj = strchr(__F_l, (int)('"'))) != NULL)
					pj[0]='\0';
				i++;
				continue;
			} else
				printf("ERROR by reading ConfigFile lp_l %d\n", i+1);
			break;
		case 12:
			if ( ((pj = strchr(lx, (int)(' '))) != NULL)) {
				strcpy(lx, pj+1);
				sscanf(lx, "%d", &_G__N_c);
				__G__C_l = (int *) calloc(_G__N_c, sizeof(int));
				Error_Msg_ALLOC(__G__C_l,"AllocErr by alloc Colors",exit(0);)
				j=0;
				while ( ((pj = strchr(lx, (int)(' '))) != NULL) && (j<_G__N_c )) {
					strcpy(lx, pj+1);
					sscanf(lx, "%d", &__G__C_l[j]);
					j++;
				}
				i++;
				continue;
			} else
				printf("ERROR by reading ConfigFile lp_l %d\n", i+1);
			break;
		default:
			break;
		}
	}
	fclose(ConfigFile);
	__G_p_o_p = __G__C_l[0];
	_Z_L_int = __G__C_l[_G__N_c-1]- __G_p_o_p + 1;

	V__f_B_l__l = _Z_L_int;
	if (!_r__G__(__F_g))
		return (0);
	if (!_G_NaF)
		return (1);
	__G_N_z_b___ = __f_a_zb__(_G_NaF);
	L__r_t_ = __f_a_rtl__(_G_NaF);
	__G_q_z_x = __f_a_zb__(_G_NaF);
	__G_c_z_x = __f_a_zb__(_G_NaF);
	_G_q_lew = __f_a_zb__(_G_NaF);
	_X__o_d_ = _G__D__c;
	_a_cI__(__S_I_A_T__);
	_iGD__(__S_I_A_T__,__S_I_A_T__->V__r_e____);
	if (_Z_L_int != _G__N_c)
		_b_c_o__();
	OrderNodesbyReqColors();
	_n_zb_o__(__S_I_A_T__->V__ILE,__G_N_z_b___);
	_SLC__(_X__r_c);

	_I_2n_ = O_N_N_Sign_(_G__D__nr)*_B_i_n2*M_I_n;
	_I_n_1 = M_I_n + _I_2n_;
	for (lp1=_P_x; _P_0<lp1; lp1--) {
		V__p_p_s_i_e_[lp1].__a__p = 0;
		V__p_p_s_i_e_[lp1].__c_l__b__ = 0;
		V__p_p_s_i_e_[lp1].__c_it_ = 0;
		V__p_p_s_i_e_[lp1].__d__r = 1;
		V__p_p_s_i_e_[lp1].__d__m = 1;
	}
	V__p_p_s_i_e_[_P_c].__d__r = _G__D__c;
	V__p_p_s_i_e_[_P_s].__d__r = _G__D__s;
	V__p_p_s_i_e_[_P_n].__d__r = _G__D__nr;
	V__p_p_s_i_e_[_P_sn].__d__r = _G__D__sn;
	V__p_p_s_i_e_[_P_c].__c_l__b__ = 998;
	V__p_p_s_i_e_[_P_s].__c_l__b__ = 997;
	V__p_p_s_i_e_[_P_n].__c_l__b__ = _I_n_1-1;
	V__p_p_s_i_e_[_P_sn].__c_l__b__ = _I_2n_-1;

	return (1);
}

int awe_afp(char * configfile) {
	short cr_read=0, it_read=0, COIT=0, ADIT=0;
	int i=0, QC2=0, nt=0, pt=1000, _L_=0, U_Z_y_n=1, _U_Z_y_i=0, lp1=0;
	double x=0.0;
	char _up_2='\0';
	_a_ss_B_ = 0;
	fflush(stdin);
	if (!_i__d__(configfile)) {
		__fa__();
		return (1);
	}
	U_Z_y_n = _G__D__nr;
	_B_i_n2 = _B_i_n2 && U_Z_y_n && _G__D__sn;

	if (_G__U_t && !_czy_stos_o(_P_ic,__F_i,__S_I_A_T__->V__r_e____,1)) {
		__fa__();
		return (IT_R_ERR);
	}
	V__p_p_s_i_e_[_P_ic].__a__p = _G__U_t;
	if (_G__D__c && (_X__r_c > 1)) {
		_pol_mat_cit(_P_c,_G__D__c,M_I_x,__S_I_A_T__->V__r_e____);
		V__p_p_s_i_e_[_P_c].__a__p = 1;
	}
	if (_G__D__s && (_G_R_s_x__ > 1)) {
		_pol_mat_sit(_P_s,_G__D__s,M_I_x,__S_I_A_T__->V__r_e____);
		V__p_p_s_i_e_[_P_s].__a__p = 1;
	}
	if (U_Z_y_n) {
		if (!_czy_sasia(__F_n, _P_n, _G__D__nr, _I_n_1, __S_I_A_T__->V__r_e____)) {
			__fa__();
			return (IT_R_ERR);
		}
		V__p_p_s_i_e_[_P_n].__a__p = 1;
	}
	if (_B_i_n2) {
		_domk_mat_nas(_P_n,_P_sn,1,-1,_I_2n_,__S_I_A_T__->V__r_e____);
		V__p_p_s_i_e_[_P_sn].__a__p = 1;
	}
	_rob_wsio_();
	__fa__();
	return (NO_ERR);
}
int write_last_plan(int _UP_1) {
	if (!_a_ss_B_)
		return (0);
	return (1);
}
