/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "InformationElements"
 * 	found in "../asn/InformationElements.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_DL_AddReconfTransChInfoList_r9_H_
#define	_DL_AddReconfTransChInfoList_r9_H_


#include <asn_application.h>

/* Including external dependencies */
#include <asn_SEQUENCE_OF.h>
#include <constr_SEQUENCE_OF.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Forward declarations */
struct DL_AddReconfTransChInformation_r9;

/* DL-AddReconfTransChInfoList-r9 */
typedef struct DL_AddReconfTransChInfoList_r9 {
	A_SEQUENCE_OF(struct DL_AddReconfTransChInformation_r9) list;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} DL_AddReconfTransChInfoList_r9_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_DL_AddReconfTransChInfoList_r9;

#ifdef __cplusplus
}
#endif

/* Referred external types */
#include "DL-AddReconfTransChInformation-r9.h"

#endif	/* _DL_AddReconfTransChInfoList_r9_H_ */
#include <asn_internal.h>