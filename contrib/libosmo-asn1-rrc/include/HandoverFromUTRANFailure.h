/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "PDU-definitions"
 * 	found in "../asn/PDU-definitions.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_HandoverFromUTRANFailure_H_
#define	_HandoverFromUTRANFailure_H_


#include <asn_application.h>

/* Including external dependencies */
#include "RRC-TransactionIdentifier.h"
#include "GSM-MessageList.h"
#include <constr_SEQUENCE.h>
#include "CDMA2000-MessageList.h"
#include <constr_CHOICE.h>
#include <BIT_STRING.h>
#include "HandoverFromUtranFailure-v590ext-IEs.h"
#include "HandoverFromUtranFailure-v860ext-IEs.h"

#ifdef __cplusplus
extern "C" {
#endif

/* Dependencies */
typedef enum HandoverFromUTRANFailure__interRATMessage_PR {
	HandoverFromUTRANFailure__interRATMessage_PR_NOTHING,	/* No components present */
	HandoverFromUTRANFailure__interRATMessage_PR_gsm,
	HandoverFromUTRANFailure__interRATMessage_PR_cdma2000
} HandoverFromUTRANFailure__interRATMessage_PR;

/* Forward declarations */
struct InterRAT_HO_FailureCause;

/* HandoverFromUTRANFailure */
typedef struct HandoverFromUTRANFailure {
	RRC_TransactionIdentifier_t	 rrc_TransactionIdentifier;
	struct InterRAT_HO_FailureCause	*interRAT_HO_FailureCause	/* OPTIONAL */;
	struct HandoverFromUTRANFailure__interRATMessage {
		HandoverFromUTRANFailure__interRATMessage_PR present;
		union HandoverFromUTRANFailure__interRATMessage_u {
			struct HandoverFromUTRANFailure__interRATMessage__gsm {
				GSM_MessageList_t	 gsm_MessageList;
				
				/* Context for parsing across buffer boundaries */
				asn_struct_ctx_t _asn_ctx;
			} gsm;
			struct HandoverFromUTRANFailure__interRATMessage__cdma2000 {
				CDMA2000_MessageList_t	 cdma2000_MessageList;
				
				/* Context for parsing across buffer boundaries */
				asn_struct_ctx_t _asn_ctx;
			} cdma2000;
		} choice;
		
		/* Context for parsing across buffer boundaries */
		asn_struct_ctx_t _asn_ctx;
	} *interRATMessage;
	struct HandoverFromUTRANFailure__laterNonCriticalExtensions {
		BIT_STRING_t	*handoverFromUTRANFailure_r3_add_ext	/* OPTIONAL */;
		struct HandoverFromUTRANFailure__laterNonCriticalExtensions__v590NonCriticalExtensions {
			HandoverFromUtranFailure_v590ext_IEs_t	 handoverFromUTRANFailure_v590ext;
			struct HandoverFromUTRANFailure__laterNonCriticalExtensions__v590NonCriticalExtensions__v860NonCriticalExtensions {
				HandoverFromUtranFailure_v860ext_IEs_t	 handoverFromUTRANFailure_v860ext;
				struct HandoverFromUTRANFailure__laterNonCriticalExtensions__v590NonCriticalExtensions__v860NonCriticalExtensions__nonCriticalExtensions {
					
					/* Context for parsing across buffer boundaries */
					asn_struct_ctx_t _asn_ctx;
				} *nonCriticalExtensions;
				
				/* Context for parsing across buffer boundaries */
				asn_struct_ctx_t _asn_ctx;
			} *v860NonCriticalExtensions;
			
			/* Context for parsing across buffer boundaries */
			asn_struct_ctx_t _asn_ctx;
		} *v590NonCriticalExtensions;
		
		/* Context for parsing across buffer boundaries */
		asn_struct_ctx_t _asn_ctx;
	} *laterNonCriticalExtensions;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} HandoverFromUTRANFailure_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_HandoverFromUTRANFailure;

#ifdef __cplusplus
}
#endif

/* Referred external types */
#include "InterRAT-HO-FailureCause.h"

#endif	/* _HandoverFromUTRANFailure_H_ */
#include <asn_internal.h>