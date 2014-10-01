/*
 * Generated by asn1c-0.9.24 (http://lionet.info/asn1c)
 * From ASN.1 module "InformationElements"
 * 	found in "../asn/InformationElements.asn"
 * 	`asn1c -fcompound-names -fnative-types`
 */

#ifndef	_TransmissionWindowSize_H_
#define	_TransmissionWindowSize_H_


#include <asn_application.h>

/* Including external dependencies */
#include <NativeEnumerated.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Dependencies */
typedef enum TransmissionWindowSize {
	TransmissionWindowSize_tw1	= 0,
	TransmissionWindowSize_tw8	= 1,
	TransmissionWindowSize_tw16	= 2,
	TransmissionWindowSize_tw32	= 3,
	TransmissionWindowSize_tw64	= 4,
	TransmissionWindowSize_tw128	= 5,
	TransmissionWindowSize_tw256	= 6,
	TransmissionWindowSize_tw512	= 7,
	TransmissionWindowSize_tw768	= 8,
	TransmissionWindowSize_tw1024	= 9,
	TransmissionWindowSize_tw1536	= 10,
	TransmissionWindowSize_tw2047	= 11,
	TransmissionWindowSize_tw2560	= 12,
	TransmissionWindowSize_tw3072	= 13,
	TransmissionWindowSize_tw3584	= 14,
	TransmissionWindowSize_tw4095	= 15
} e_TransmissionWindowSize;

/* TransmissionWindowSize */
typedef long	 TransmissionWindowSize_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_TransmissionWindowSize;
asn_struct_free_f TransmissionWindowSize_free;
asn_struct_print_f TransmissionWindowSize_print;
asn_constr_check_f TransmissionWindowSize_constraint;
ber_type_decoder_f TransmissionWindowSize_decode_ber;
der_type_encoder_f TransmissionWindowSize_encode_der;
xer_type_decoder_f TransmissionWindowSize_decode_xer;
xer_type_encoder_f TransmissionWindowSize_encode_xer;
per_type_decoder_f TransmissionWindowSize_decode_uper;
per_type_encoder_f TransmissionWindowSize_encode_uper;

#ifdef __cplusplus
}
#endif

#endif	/* _TransmissionWindowSize_H_ */
#include <asn_internal.h>