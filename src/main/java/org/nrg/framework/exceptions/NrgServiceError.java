/**
 * NrgServiceError
 * (C) 2011,2013 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.exceptions;

public enum NrgServiceError {
	Unknown,
	AlreadyInitialized, 
	ChannelRendererNotFound, 
	ChannelRendererProcessingError,
    DuplicateCategory,
    DuplicateDefinition,
	DuplicateSubscriber,
	GenericMailError,
	InvalidChannelRenderer,
	InvalidDirectParameterizedClassUsage,
	InvalidMailAttachment,
	InvalidRestServiceParameters,
	NoMatchingCategory,
	NoMatchingDefinition,
	/** attempted access to a resource exceeding caller's permissions */
    PermissionsViolation,
    /** attempted forbidden action **/
    SecurityViolation,
	UnknownChannelRendererError,
    NoMatchingRepositoryForService,
    Instantiation,
	UnsupportedFeature, UnknownScriptRunner, UnknownEntity;
	
	public static final NrgServiceError Default = Unknown;
}
