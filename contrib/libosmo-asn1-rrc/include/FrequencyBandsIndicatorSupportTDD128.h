/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "InformationElements"
 * 	found in "../asn/InformationElements.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_FrequencyBandsIndicatorSupportTDD128_H_
#define	_FrequencyBandsIndicatorSupportTDD128_H_


#include <asn_application.h>

/* Including external dependencies */
#include "RadioFrequencyBandTDD-r10.h"
#include <constr_SEQUENCE.h>

#ifdef __cplusplus
extern "C" {
#endif

/* FrequencyBandsIndicatorSupportTDD128 */
typedef struct FrequencyBandsIndicatorSupportTDD128 {
	RadioFrequencyBandTDD_r10_t	 frequencyBandsIndicatorTDD;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} FrequencyBandsIndicatorSupportTDD128_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_FrequencyBandsIndicatorSupportTDD128;

#ifdef __cplusplus
}
#endif

#endif	/* _FrequencyBandsIndicatorSupportTDD128_H_ */
#include <asn_internal.h>