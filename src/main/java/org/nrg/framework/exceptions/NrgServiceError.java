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
    AlreadyInitialized, 
    ChannelRendererNotFound, 
    ChannelRendererProcessingError,
    ConfigurationError,
    DuplicateCategory,
    DuplicateDefinition,
    DuplicateSubscriber,
    GenericMailError,
    Instantiation,
    InvalidChannelRenderer,
    InvalidDirectParameterizedClassUsage,
    InvalidMailAttachment,
    InvalidRestServiceParameters,
    InvalidScript,
    NoMatchingCategory,
    NoMatchingDefinition,
    NoMatchingRepositoryForService,
    /** attempted access to a resource exceeding caller's permissions */
    PermissionsViolation,
    /** attempted forbidden action **/
    SecurityViolation,
    Uninitialized,
    Unknown,
    UnknownChannelRendererError,
    UnknownEntity,
    UnknownScriptRunner,
    UnsupportedFeature;

    public static final NrgServiceError Default = Unknown;
}
