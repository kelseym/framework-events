/**
 * AbstractPintoBean
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/16/12 by rherri01
 */
package org.nrg.framework.pinto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.util.*;

/**
 * Defines the base functionality for a parameter interface object (PIntO) bean. Scan a bean class and extract the
 * command-line parameters from methods annotated with the {@link Parameter} annotation. These parameters and any
 * accompanying arguments are stored in the bean object.
 */
public abstract class AbstractPintoBean {

    /**
     * Processes the incoming arguments, setting the bean's print stream to {@link System#out}.
     * @param arguments    Incoming parameter arguments to process.
     */
    protected AbstractPintoBean(Object parent, String[] arguments) {
        this(parent, arguments, System.out);
    }

    /**
     * Processes the incoming arguments, setting the bean's print stream to the submitted parameter.
     * @param arguments    Incoming parameter arguments to process.
     * @param printStream  Indicates the print stream to be used for printing output.
     */
    protected AbstractPintoBean(Object parent, String[] arguments, PrintStream printStream) {
        assert parent != null : "You must specify the parent for your pinto bean.";

        _arguments = Arrays.asList(Arrays.copyOfRange(arguments, 1, arguments.length));
        _printStream = printStream;
        _parent = parent;

        try {
            scan();
            harvest();
            prune();

            if (getHelp()) {
                throw new PintoException(PintoExceptionType.HelpRequested);
            }

            validate();
        } catch (PintoException exception) {
            if (!StringUtils.isBlank(exception.getMessage())) {
                _log.warn(exception.getMessage());
            }
            displayHelp();
        }
    }

    /**
     * Provides an opportunity for subclasses to validate the processed parameters and their arguments.
     * @throws PintoException
     */
    abstract public void validate() throws PintoException;

    public void setPrintStream(PrintStream printStream) {
        _printStream = printStream;
    }

    public PrintStream getPrintStream() {
        return _printStream;
    }

    /**
     * The setter for the help option.
     *
     * @param help Incoming parameter.
     */
    @Parameter(value = "h", longOption = "help", help = "Displays this help text.", argCount = ArgCount.StandAlone)
    public void setHelp(boolean help) {
        _help = help;
    }

    /**
     * The getter for the help option.
     *
     * @return Gets the value for the help option.
     */
    @Value("h")
    public boolean getHelp() {
        return _help;
    }

    /**
     * Returns any arguments that are posted on the end of the list of arguments but aren't associated with a parameter.
     * @return Any arguments on the end of the list of arguments not associated with a parameter.
     */
    public List<String> getTrailingArguments() {
        return _trailing;
    }

    /**
     * Display the help for each of the available command-line parameters supported by this bean. The help is printed to
     * the stream specified by the {@link #setPrintStream(java.io.PrintStream)} property or passed in through the
     * {@link AbstractPintoBean(String[], PrintStream)} constructor.
     */
    public void displayHelp() {
        if (_parametersByShortOption == null || _parametersByShortOption.size() == 0) {
            getPrintStream().println("No parameters found for this application!");
        } else {
            // TODO: Add an annotation to put application name, copyright info, and introductory help text on the class level.
            String appName, copyright, introduction;
            PintoApplication application = _parent.getClass().getAnnotation(PintoApplication.class);
            if (application == null) {
                appName = _parent.getClass().getSimpleName();
                copyright = introduction = null;
            } else {
                appName = application.value();
                copyright = application.copyright();
                introduction = application.introduction();
            }

            getPrintStream().println(appName);
            if (!StringUtils.isBlank(copyright)) {
                getPrintStream().println(copyright);
            }
            if (!StringUtils.isBlank(introduction)) {
                getPrintStream().println(introduction);
            }
            getPrintStream().println();

            for(ParameterData parameter : _parametersByShortOption.values()) {
                StringBuilder parameterText = new StringBuilder(PREFIX);
                parameterText.append(SHORT_OPTION_DELIMITER).append(parameter.getShortOption());
                if (parameter.hasLongOption()) {
                    parameterText.append(", ").append(LONG_OPTION_DELIMITER).append(parameter.getLongOption());
                }

                // If our parameter text is so long that it will either run into the hanging indent text or directly up
                // to the hanging indent text (i.e., no space left between them)...
                final int length = parameterText.length();
                if (length > HANGING_INDENT - 1) {
                    // Then add a new line
                    parameterText.append(INDENT_FILLER);
                } else {
                    parameterText.append(CharBuffer.allocate(HANGING_INDENT - length).toString().replace('\0', ' '));
                }

                parameterText.append(WordUtils.wrap(parameter.getHelp(), WIDTH - HANGING_INDENT, INDENT_FILLER, true));
                getPrintStream().println(parameterText.toString());
            }
        }
    }

    /**
     * Converts a string to the type indicated by the <b>type</b> parameter. The ability of a pinto bean to convert
     * strings to any arbitrary type can be extended by overriding and extending this method.
     * @param type        Indicates the type to which the argument should be converted.
     * @param argument    The argument to be converted.
     * @return An object of the indicated type from the given value.
     * @throws PintoException
     */
    protected Object convertStringToType(final Class<?> type, final String argument) throws PintoException {
        Object object;
        if (type == String.class) {
            object = argument;
        } else if (type == Integer.class || type == int.class) {
            object = Integer.parseInt(argument);
        } else if (type == Long.class || type == long.class) {
            object = Long.parseLong(argument);
        } else if (type == Float.class || type == float.class) {
            object = Float.parseFloat(argument);
        } else if (type == Double.class || type == double.class) {
            object = Double.parseDouble(argument);
        } else if (type == Character.class || type == char.class) {
            object = argument.toCharArray()[0];
        } else if (type == Byte.class || type == byte.class) {
            object = Byte.parseByte(argument);
        } else if (type == Short.class || type == short.class) {
            object = Short.parseShort(argument);
        } else if (type == Boolean.class || type == boolean.class) {
            object = Boolean.parseBoolean(argument);
        } else if (type == File.class) {
            object = new File(argument);
        } else if (type == URI.class) {
            try {
                object = new URI(argument);
            } catch (URISyntaxException exception) {
                throw new PintoException(PintoExceptionType.SyntaxFormat, "The value " + argument + " is not a valid URI.", exception);
            }
        } else {
            throw new PintoException(PintoExceptionType.UnknownParameterTypes, "I don't know how to convert to the type " + type.getName());
        }

        return object;
    }

    /**
     * Scans the bean class for configured parameters. Parameters are configured by adding the {@link Parameter}
     * annotation to the setter method. Associated getter methods are marked with the {@link Value} annotation.
     */
    private void scan() throws PintoException {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            Parameter annotation = method.getAnnotation(Parameter.class);
            if (annotation != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Found command-line parameter annotation " + annotation.value() + " on " + getClass().getName() + "." + method.getName() + "() method");
                }
                final ParameterData parameter = new ParameterData(method, annotation);
                if (_parametersByShortOption.containsKey(parameter.getShortOption())) {
                    throw new PintoException(PintoExceptionType.DuplicateParameter, "Your application has multiple declarations of the short option " + parameter.getShortOption());
                }
                _parametersByShortOption.put(parameter.getShortOption(), parameter);
                final String longOption = parameter.getLongOption();
                if (!StringUtils.isBlank(longOption)) {
                    if (_parametersByLongOption.containsKey(parameter.getLongOption())) {
                        throw new PintoException(PintoExceptionType.DuplicateParameter, "Your application has multiple declarations of the long option " + parameter.getLongOption());
                    }
                    _parametersByLongOption.put(longOption, parameter);
                }
            }
        }
    }

    /**
     * Harvests the command-line parameters and sorts them along with their arguments. Any trailing
     * arguments are assigned to the last found parameter. Trailing arguments that occur without
     * parameters are stashed in the {@link #getTrailingArguments()} property.
     * @throws PintoException
     */
    private void harvest() throws PintoException {
        ParameterData parameter = null;
        for (String argument : _arguments) {
            // Is this argument a parameter?
            if (isParameter(argument)) {
                // If there are trailing arguments, then something is wrong.
                if (_trailing.size() > 0) {
                    throw new PintoException(PintoExceptionType.SyntaxFormat, argument, "Trailing arguments were found prior to the parameter " + argument + ". Check that you've supplied only the expected number of arguments to each parameter.");
                }

                // Try to get the data for the indicated parameter.
                parameter = getParameterData(argument);

                // Now store the parameter option in the map of parameter data and initialize the argument cache.
                _parameters.put(parameter.getShortOption(), new ArrayList<String>());
            } else if (parameter == null) {
                // This is a fail-safe catch for situations with no parameters, e.g., ls file1 file2 file3
                _trailing.add(argument);
            } else {
                // Add the argument to the current parameter. The last parameter will harvest all trailing data.
                // We'll handle that situation when we prune the parameter list.
                _parameters.get(parameter.getShortOption()).add(argument);
            }
        }
    }

    /**
     * Prunes the parameters and arguments. This includes validating the arguments passed in against
     * the accepted arguments for each parameter, as well as removing trailing arguments.
     */
    private void prune() throws PintoException {
        for (String parameterId : _parameters.keySet()) {
            ParameterData parameter = _parametersByShortOption.get(parameterId);
            List<String> arguments = _parameters.get(parameterId);

            validateArgCount(parameter, arguments);

            final Method method = parameter.getMethod();
            try {
                if (parameter.getArgCount() == ArgCount.StandAlone) {
                    method.invoke(this, true);
                } else {
                    try {
                        Object[] coercedArguments = coerceArguments(method, arguments);
                        method.invoke(this, coercedArguments);
                    } catch (PintoException exception) {
                        if (exception.getType() == PintoExceptionType.UnknownParameterTypes && StringUtils.isBlank(exception.getMessage())) {
                            throw new PintoException(PintoExceptionType.UnknownParameterTypes, "The parameter " + parameterId + " has unknown parameter types. Check your set method for compatible parameter types.");
                        }
                        if (exception.getType() == PintoExceptionType.SyntaxFormat) {
                            final StringBuilder message = new StringBuilder("The parameter " + parameterId + " has a syntax error. Check that your arguments match the parameter requirements.");
                            if (!StringUtils.isBlank(exception.getMessage())) {
                                message.append(" The specific error message is:\n\n").append(exception.getMessage());
                            }
                            throw new PintoException(PintoExceptionType.SyntaxFormat, parameterId, message.toString());
                        }
                        throw exception;
                    }
                }
            } catch (IllegalAccessException exception) {
                throw new PintoException(PintoExceptionType.Configuration, parameter.getShortOption(), "Unable to call the " + method.getName() + " method configured for handling parameter", exception);
            } catch (InvocationTargetException exception) {
                throw new PintoException(PintoExceptionType.Configuration, parameter.getShortOption(), "Unable to call the " + method.getName() + " method configured for handling parameter", exception);
            }
        }
    }

    private Object[] coerceArguments(final Method method, final List<String> arguments) throws PintoException {
        Class<?>[] types = method.getParameterTypes();
        if (types == null || types.length == 0) {
            throw new PintoException(PintoExceptionType.UnknownParameterTypes);
        }
        final boolean isArrayParameter = types.length == 1 && types[0].isArray();
        if (types.length != arguments.size() && !isArrayParameter) {
            throw new PintoException(PintoExceptionType.SyntaxFormat);
        }
        Class<?> type = isArrayParameter ? types[0].getComponentType() : null;
        final List<Object> coercedArguments = new ArrayList<Object>(types.length);
        for (int index = 0; index < arguments.size(); index++) {
            if (!isArrayParameter) {
                type = types[index];
            }
            coercedArguments.add(convertStringToType(type, arguments.get(index)));
        }

        return isArrayParameter ? new Object[] { coercedArguments.toArray((Object[]) Array.newInstance(types[0].getComponentType(), coercedArguments.size())) } : coercedArguments.toArray();
    }

    private void validateArgCount(final ParameterData parameter, final List<String> arguments) throws PintoException {
        final String parameterId = parameter.getShortOption();
        final int argCount = arguments.size();
        final ArgCount ArgCount = parameter.getArgCount();

        switch (ArgCount) {
            case StandAlone:
                if (_log.isDebugEnabled()) {
                    _log.debug("Found parameter " + parameterId + " specified as StandAlone parameter, comes with " + argCount + " arguments");
                }
                if (argCount > 0) {
                    throw new PintoException(PintoExceptionType.SyntaxFormat, "The parameter " + parameterId + " does not accept any arguments.");
                }
                break;
            case OneArgument:
                if (_log.isDebugEnabled()) {
                    _log.debug("Found parameter " + parameterId + " specified as OneArgument parameter, comes with " + argCount + " arguments");
                }
                if (argCount != 1) {
                    throw new PintoException(PintoExceptionType.SyntaxFormat, "The parameter " + parameterId + " only accepts a single argument.");
                }
                break;
            case OneToN:
                if (_log.isDebugEnabled()) {
                    _log.debug("Found parameter " + parameterId + " specified as OneToN parameter, comes with " + argCount + " arguments");
                }
                if (argCount == 0) {
                    throw new PintoException(PintoExceptionType.SyntaxFormat, "The parameter " + parameterId + " requires one or more arguments.");
                }
                break;
            case SpecificCount:
                int acceptedArgCount = parameter.getExactArgCount();
                if (_log.isDebugEnabled()) {
                    _log.debug("Found parameter " + parameterId + " specified as SpecificCount parameter with " + acceptedArgCount + " arguments required, comes with " + argCount + " arguments");
                }
                if (argCount != acceptedArgCount) {
                    throw new PintoException(PintoExceptionType.SyntaxFormat, "The parameter " + parameterId + " requires exactly " + acceptedArgCount + " arguments.");
                }
                break;
            case ZeroToN:
                if (_log.isDebugEnabled()) {
                    _log.debug("Found parameter " + parameterId + " specified as ZeroToN parameter, comes  with " + argCount + " arguments");
                }
        }
    }

    private ParameterData getParameterData(String parameter) throws PintoException {
        if (parameter.startsWith(LONG_OPTION_DELIMITER)) {
            parameter = parameter.substring(LONG_OPTION_DELIMITER.length());
            if (_parametersByLongOption.containsKey(parameter)) {
                return _parametersByLongOption.get(parameter);
            }
        }
        if (parameter.startsWith(SHORT_OPTION_DELIMITER)) {
            parameter = parameter.substring(SHORT_OPTION_DELIMITER.length());
            if (_parametersByShortOption.containsKey(parameter)) {
                return _parametersByShortOption.get(parameter);
            }
        }
        // We didn't find it.
        throw new PintoException(PintoExceptionType.UnknownParameter, parameter, "The parameter " + parameter + " is not a valid parameter.");
    }

    private boolean isParameter(final String argument) {
        return StringUtils.startsWithAny(argument, OPTION_DELIMITERS);
    }

    private static final Log _log = LogFactory.getLog(AbstractPintoBean.class);
    /**
     * This is the text to place before each option in the help text.
     */
    private static final String PREFIX = " ";
    /**
     * This is the width of the hanging indent in the help text. Illustration ('|' is the left margin):
     *
     * <div style='font-family: "Courier New", Courier, monospace'>
     * | -h, --help         Show this help text.
     * |12345678901234567890
     * </div>
     *
     * Above shows a 20-character hanging indent.
     */
    private static final int HANGING_INDENT = 20;
    /**
     * Provides the indent filler to format the hanging indent space.
     */
    private static final String INDENT_FILLER = "\n" + CharBuffer.allocate(HANGING_INDENT).toString().replace('\0', ' ');
    /**
     * The overall format width for the help text.
     */
    private static final int WIDTH = 80;

    private static final String SHORT_OPTION_DELIMITER = "-";
    private static final String LONG_OPTION_DELIMITER = "--";
    private static final String[] OPTION_DELIMITERS = new String[] { LONG_OPTION_DELIMITER, SHORT_OPTION_DELIMITER };

    /**
     * The print stream for help text and user messages.
     */
    private PrintStream _printStream = System.out;
    private final Object _parent;
    private boolean _help;
    private List<String> _arguments;
    private Map<String, List<String>> _parameters = new LinkedHashMap<String, List<String>>();
    private List<String> _trailing = new ArrayList<String>();
    private Map<String, ParameterData> _parametersByShortOption = new HashMap<String, ParameterData>();
    private Map<String, ParameterData> _parametersByLongOption = new HashMap<String, ParameterData>();
}