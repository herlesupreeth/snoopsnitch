/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "PDU-definitions"
 * 	found in "../asn/PDU-definitions.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#include "MeasurementReport-v770ext-IEs.h"

static asn_TYPE_member_t asn_MBR_MeasurementReport_v770ext_IEs_1[] = {
	{ ATF_POINTER, 4, offsetof(struct MeasurementReport_v770ext_IEs, measuredResults),
		(ASN_TAG_CLASS_CONTEXT | (0 << 2)),
		+1,	/* EXPLICIT tag at current level */
		&asn_DEF_MeasuredResults_v770ext,
		0,	/* Defer constraints checking to the member type */
		0,	/* No PER visible constraints */
		0,
		"measuredResults"
		},
	{ ATF_POINTER, 3, offsetof(struct MeasurementReport_v770ext_IEs, additionalMeasuredResults),
		(ASN_TAG_CLASS_CONTEXT | (1 << 2)),
		-1,	/* IMPLICIT tag at current level */
		&asn_DEF_MeasuredResultsList_v770xet,
		0,	/* Defer constraints checking to the member type */
		0,	/* No PER visible constraints */
		0,
		"additionalMeasuredResults"
		},
	{ ATF_POINTER, 2, offsetof(struct MeasurementReport_v770ext_IEs, eventResults),
		(ASN_TAG_CLASS_CONTEXT | (2 << 2)),
		+1,	/* EXPLICIT tag at current level */
		&asn_DEF_EventResults_v770ext,
		0,	/* Defer constraints checking to the member type */
		0,	/* No PER visible constraints */
		0,
		"eventResults"
		},
	{ ATF_POINTER, 1, offsetof(struct MeasurementReport_v770ext_IEs, ue_Positioning_OTDOA_MeasuredResults),
		(ASN_TAG_CLASS_CONTEXT | (3 << 2)),
		-1,	/* IMPLICIT tag at current level */
		&asn_DEF_UE_Positioning_OTDOA_MeasuredResultsTDD_ext,
		0,	/* Defer constraints checking to the member type */
		0,	/* No PER visible constraints */
		0,
		"ue-Positioning-OTDOA-MeasuredResults"
		},
};
static int asn_MAP_MeasurementReport_v770ext_IEs_oms_1[] = { 0, 1, 2, 3 };
static ber_tlv_tag_t asn_DEF_MeasurementReport_v770ext_IEs_tags_1[] = {
	(ASN_TAG_CLASS_UNIVERSAL | (16 << 2))
};
static asn_TYPE_tag2member_t asn_MAP_MeasurementReport_v770ext_IEs_tag2el_1[] = {
    { (ASN_TAG_CLASS_CONTEXT | (0 << 2)), 0, 0, 0 }, /* measuredResults at 4188 */
    { (ASN_TAG_CLASS_CONTEXT | (1 << 2)), 1, 0, 0 }, /* additionalMeasuredResults at 4189 */
    { (ASN_TAG_CLASS_CONTEXT | (2 << 2)), 2, 0, 0 }, /* eventResults at 4190 */
    { (ASN_TAG_CLASS_CONTEXT | (3 << 2)), 3, 0, 0 } /* ue-Positioning-OTDOA-MeasuredResults at 4192 */
};
static asn_SEQUENCE_specifics_t asn_SPC_MeasurementReport_v770ext_IEs_specs_1 = {
	sizeof(struct MeasurementReport_v770ext_IEs),
	offsetof(struct MeasurementReport_v770ext_IEs, _asn_ctx),
	asn_MAP_MeasurementReport_v770ext_IEs_tag2el_1,
	4,	/* Count of tags in the map */
	asn_MAP_MeasurementReport_v770ext_IEs_oms_1,	/* Optional members */
	4, 0,	/* Root/Additions */
	-1,	/* Start extensions */
	-1	/* Stop extensions */
};
asn_TYPE_descriptor_t asn_DEF_MeasurementReport_v770ext_IEs = {
	"MeasurementReport-v770ext-IEs",
	"MeasurementReport-v770ext-IEs",
	SEQUENCE_free,
	SEQUENCE_print,
	SEQUENCE_constraint,
	SEQUENCE_decode_ber,
	SEQUENCE_encode_der,
	SEQUENCE_decode_xer,
	SEQUENCE_encode_xer,
	SEQUENCE_decode_uper,
	SEQUENCE_encode_uper,
	0,	/* Use generic outmost tag fetcher */
	asn_DEF_MeasurementReport_v770ext_IEs_tags_1,
	sizeof(asn_DEF_MeasurementReport_v770ext_IEs_tags_1)
		/sizeof(asn_DEF_MeasurementReport_v770ext_IEs_tags_1[0]), /* 1 */
	asn_DEF_MeasurementReport_v770ext_IEs_tags_1,	/* Same as above */
	sizeof(asn_DEF_MeasurementReport_v770ext_IEs_tags_1)
		/sizeof(asn_DEF_MeasurementReport_v770ext_IEs_tags_1[0]), /* 1 */
	0,	/* No PER visible constraints */
	asn_MBR_MeasurementReport_v770ext_IEs_1,
	4,	/* Elements count */
	&asn_SPC_MeasurementReport_v770ext_IEs_specs_1	/* Additional specs */
};
