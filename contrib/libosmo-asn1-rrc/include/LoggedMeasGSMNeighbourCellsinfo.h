/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "InformationElements"
 * 	found in "../asn/InformationElements.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_LoggedMeasGSMNeighbourCellsinfo_H_
#define	_LoggedMeasGSMNeighbourCellsinfo_H_


#include <asn_application.h>

/* Including external dependencies */
#include "BSIC.h"
#include "Frequency-Band.h"
#include "BCCH-ARFCN.h"
#include "GSM-CarrierRSSI.h"
#include <constr_SEQUENCE.h>

#ifdef __cplusplus
extern "C" {
#endif

/* LoggedMeasGSMNeighbourCellsinfo */
typedef struct LoggedMeasGSMNeighbourCellsinfo {
	BSIC_t	 bsic;
	Frequency_Band_t	 frequency_band;
	BCCH_ARFCN_t	 bcch_ARFCN;
	GSM_CarrierRSSI_t	 gsm_CarrierRSSI;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} LoggedMeasGSMNeighbourCellsinfo_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_LoggedMeasGSMNeighbourCellsinfo;

#ifdef __cplusplus
}
#endif

#endif	/* _LoggedMeasGSMNeighbourCellsinfo_H_ */
#include <asn_internal.h>
