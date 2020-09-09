package com.my.demo;

import java.text.MessageFormat;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;

/**
 * An aspect that adds tracing to all methods. This uses a
 * "pertypewithin" aspect, which associates the aspect state (in this case, the
 * log) with each type.
 * 
 * The pertypewithin causes all classes that don't have the Untraced annotation
 * to be woven. The pointcuts further limit the use of the tracing loggers to
 * avoid infinite recursion, etc...
 */
@Aspect("pertypewithin(!is(InterfaceType) && !is(EnumType) && (!@com.my.demo.Untraced "
        + "(openpackage..* || mypackage..*)))")
@Untraced
public class TracingAspect {

    private Logger conLogger;
    private static final String ENTERING_PREFIX = "Entering {0}";
    private static final String EXITING_PREFIX = "Exiting {0}";
    private static final String ARGUMENTS = ": Arguments =>";
    private static final String ARGUMENTS_NOT_AVAILABLE = ARGUMENTS + " N/A";
    private static final String RETURN = ", Returns =>";
    private static final String RETURN_NOT_AVAILABLE = RETURN + " N/A";
    private static final String THROWING = "Throwing {0}";

    @Pointcut("staticinitialization(*)")
    public void staticInit() {
    }

    /**
     * This is run in a static initializer block for every woven class, and is
     * used to initialize the trace logger for the class.
     */
    @After("staticInit()")
    public void initLogger(JoinPoint.StaticPart jps) {
        conLogger = Logger.getLogger(TracingAspect.class);
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Pointcuts
    //
    ////////////////////////////////////////////////////////////////////////////

    @Pointcut("execution(new(..))")
    void tracedConstructors() {
    }

    // The !execution(String toString()) avoids infinite recursion by not
    // tracing toString() methods.
    // Also, don't trace the compile-time inserted 'access$nnn' methods. That
    // might (in very specific cases) lead to recursion but more generally just
    // confusing.
    @Pointcut("execution(* *(..)) && !execution(String toString()) && !execution(* access$*(..)) "
            + "&& !@annotation(com.my.demo.Untraced)")
    void tracedMethods() {
    }

    // Advice starts below.

    @Before("tracedConstructors()")
    public void traceConstructorEntry(JoinPoint thisJoinPoint) {
        if (null != conLogger){}
           // conLogger.trace(() -> entering(thisJoinPoint.getSignature().getName(), thisJoinPoint.getArgs()));
    }

    @AfterReturning("tracedConstructors()")
    public void traceConstructorExit(JoinPoint thisJoinPoint) {
        if (null != conLogger){}
           // conLogger.trace(() -> exiting(thisJoinPoint.getSignature().getName(), thisJoinPoint.getArgs(), null));
    }

    @AfterThrowing(pointcut = "tracedConstructors()", throwing = "t")
    public void traceConstructorThrow(JoinPoint thisJoinPoint, Throwable t) {
        if (null != conLogger){}
           /* conLogger.trace(() -> MessageFormat.format(THROWING, thisJoinPoint.getSignature().getName()) + " - "
                    + t.toString());*/
    }

    @Before("tracedMethods()")
    public void traceMethodEntry(JoinPoint thisJoinPoint) {
        if (null != conLogger){}
            //conLogger.trace(() -> entering(thisJoinPoint.getSignature().getName(), thisJoinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "tracedMethods()", returning = "r")
    public void traceMethodExit(JoinPoint thisJoinPoint, Object r) {
        // Add sensitive return value to log context

        if (null != conLogger){
            //conLogger.trace(() -> exiting(thisJoinPoint.getSignature().getName(), thisJoinPoint.getArgs(), r));
        }
    }

    @AfterThrowing(pointcut = "tracedMethods()", throwing = "t")
    public void traceMethodThrow(JoinPoint thisJoinPoint, Throwable t) {
        if (null != conLogger){}
           /* conLogger.trace(() -> MessageFormat.format(THROWING, thisJoinPoint.getSignature().getName()) + " - "
                    + t.toString());*/
    }

    // End of Advice

    /**
     * Creates entering message trace statement
     *
     * @param method
     *            method name of the log entry
     * @param args
     *            {@code Object} array representing the message parameters
     */
    private String entering(String method, Object[] args) {
        StringBuffer msgBuffer = new StringBuffer(ENTERING_PREFIX);
        Object[] temp = { method };

        if (args != null && args.length > 0) {
            int index = 1;
            msgBuffer.append(ARGUMENTS);
            for (Object arg : args) {
                if (index > 1) {
                    msgBuffer.append(",");
                }
                msgBuffer.append(" {").append(index).append("}");
                index++;
            }
            temp = ArrayUtils.addAll(temp, args);
        } else {
            msgBuffer.append(ARGUMENTS_NOT_AVAILABLE);
        }

        return MessageFormat.format(msgBuffer.toString(), temp);
    }

    /**
     * Creates exiting message trace statement
     *
     * @param method
     *            method name of the log entry
     * @param args
     *            {@code Object} array representing the message parameters
     * @param returnValue
     *            return value from the method, if any
     */
    private String exiting(String method, Object[] args, Object returnValue) {
        StringBuffer msgBuffer = new StringBuffer(EXITING_PREFIX);
        Object[] temp = { method };

        int index = 1;
        if (args != null && args.length > 0) {
            msgBuffer.append(ARGUMENTS);
            for (Object arg : args) {
                if (index > 1) {
                    msgBuffer.append(",");
                }
                msgBuffer.append(" {").append(index).append("}");
                index++;
            }
            temp = ArrayUtils.addAll(temp, args);
        } else {
            msgBuffer.append(ARGUMENTS_NOT_AVAILABLE);
        }

        if (null != returnValue) {
            msgBuffer.append(RETURN).append(" {").append(index).append("}");
            temp = ArrayUtils.addAll(temp, new Object[] { returnValue });
        } else {
            msgBuffer.append(RETURN_NOT_AVAILABLE);
        }
        temp = ArrayUtils.addAll(temp, args);

        return MessageFormat.format(msgBuffer.toString(), temp);
    }
}
