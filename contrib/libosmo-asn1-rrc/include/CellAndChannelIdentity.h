/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "InformationElements"
 * 	found in "../asn/InformationElements.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_CellAndChannelIdentity_H_
#define	_CellAndChannelIdentity_H_


#include <asn_application.h>

/* Including external dependencies */
#include "BurstType.h"
#include "MidambleShiftLong.h"
#include "TimeslotNumber.h"
#include "CellParametersID.h"
#include <constr_SEQUENCE.h>

#ifdef __cplusplus
extern "C" {
#endif

/* CellAndChannelIdentity */
typedef struct CellAndChannelIdentity {
	BurstType_t	 burstType;
	MidambleShiftLong_t	 midambleShift;
	TimeslotNumber_t	 timeslot;
	CellParametersID_t	 cellParametersID;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} CellAndChannelIdentity_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_CellAndChannelIdentity;

#ifdef __cplusplus
}
#endif

#endif	/* _CellAndChannelIdentity_H_ */
#include <asn_internal.h>