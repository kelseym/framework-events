/**
 * NrgServiceError
 * (C) 2011 Washington University School of Medicine
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
	DuplicateDefinition,
	DuplicateSubscriber,
	GenericMailError,
	InvalidChannelRenderer,
	InvalidDirectParameterizedClassUsage,
	InvalidMailAttachment,
	NoMatchingCategory,
	NoMatchingDefinition,
	UnknownChannelRendererError;
	
	public static NrgServiceError Default = Unknown;
}
