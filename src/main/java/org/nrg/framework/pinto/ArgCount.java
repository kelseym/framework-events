/**
 * AcceptedArguments
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/16/12 by rherri01
 */
package org.nrg.framework.pinto;

/**
 * Provides enums for specifying the expected arguments for {@link Parameter command-line parameters}.
 * This sets the expectations for the data that comes after the parameter specifier, if any.
 */
public enum ArgCount {
    /**
     * Indicates that the parameter has no accompanying arguments. This can be something like verbose or help
     * parameters, e.g. --help.
     */
    StandAlone,
    /**
     * Indicates that the parameter has a single accompany argument.
     */
    OneArgument,
    /**
     * Indicates that the parameter has possibly zero to a maximum of N arguments. The Nth argument is determined
     * by the termination of the argument list by one of the following:
     *
     * <ul>
     *     <li>Another parameter specified via short or long parameter option</li>
     *     <li>The end of the command line</li>
     * </ul>
     */
    ZeroToN,
    /**
     * Indicates that the parameter has at least one to a maximum of N arguments. The Nth argument is determined
     * by the termination of the argument list by one of the following:
     *
     * <ul>
     *     <li>Another parameter specified via short or long parameter option</li>
     *     <li>The end of the command line</li>
     * </ul>
     */
    OneToN,
    /**
     * Indicates that there is a specific number of arguments to this parameter. The number of arguments should be
     * specified with the {@link Parameter#argCount()} attribute.
     */
    SpecificCount
}
