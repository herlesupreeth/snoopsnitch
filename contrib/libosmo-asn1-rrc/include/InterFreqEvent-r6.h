/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "InformationElements"
 * 	found in "../asn/InformationElements.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_InterFreqEvent_r6_H_
#define	_InterFreqEvent_r6_H_


#include <asn_application.h>

/* Including external dependencies */
#include "Event2a-r6.h"
#include "Event2b-r6.h"
#include "Event2c-r6.h"
#include "Event2d-r6.h"
#include "Event2e-r6.h"
#include "Event2f-r6.h"
#include <constr_CHOICE.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Dependencies */
typedef enum InterFreqEvent_r6_PR {
	InterFreqEvent_r6_PR_NOTHING,	/* No components present */
	InterFreqEvent_r6_PR_event2a,
	InterFreqEvent_r6_PR_event2b,
	InterFreqEvent_r6_PR_event2c,
	InterFreqEvent_r6_PR_event2d,
	InterFreqEvent_r6_PR_event2e,
	InterFreqEvent_r6_PR_event2f
} InterFreqEvent_r6_PR;

/* InterFreqEvent-r6 */
typedef struct InterFreqEvent_r6 {
	InterFreqEvent_r6_PR present;
	union InterFreqEvent_r6_u {
		Event2a_r6_t	 event2a;
		Event2b_r6_t	 event2b;
		Event2c_r6_t	 event2c;
		Event2d_r6_t	 event2d;
		Event2e_r6_t	 event2e;
		Event2f_r6_t	 event2f;
	} choice;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} InterFreqEvent_r6_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_InterFreqEvent_r6;

#ifdef __cplusplus
}
#endif

#endif	/* _InterFreqEvent_r6_H_ */
#include <asn_internal.h>