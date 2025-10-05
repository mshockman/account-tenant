package dev.shockman.tenant.shared

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.spi.LoggingEventBuilder

private inline fun logWith(
    enabled: Boolean,
    builder: () -> LoggingEventBuilder,
    context: Map<String, Any?>,
    crossinline msg: () -> String,
    cause: Throwable? = null
) {
    if (!enabled) return
    val eb = builder()
    context.forEach { (k, v) -> eb.addKeyValue(k, v) }
    if (cause != null) eb.setCause(cause)
    eb.log { msg() }
}

fun Logger.logInfo(context: Map<String, Any?> = emptyMap(), message: () -> String) {
    logWith(isInfoEnabled, ::atInfo, context, message)
}

fun Logger.logDebug(context: Map<String, Any?> = emptyMap(), message: () -> String) {
    logWith(isDebugEnabled, ::atDebug, context, message)
}

fun Logger.logTrace(context: Map<String, Any?> = emptyMap(), message: () -> String) {
    logWith(isTraceEnabled, ::atTrace, context, message)
}

fun Logger.logWarn(cause: Throwable? = null, context: Map<String, Any?> = emptyMap(), message: () -> String) {
    logWith(isWarnEnabled, ::atWarn, context, message, cause)
}

fun Logger.logError(cause: Throwable? = null, context: Map<String, Any?> = emptyMap(), message: () -> String) {
    logWith(isErrorEnabled, ::atError, context, message, cause)
}

fun Any.logger(): Logger = LoggerFactory.getLogger(this.javaClass)

inline fun <reified T : Any> T.logInfo(context: Map<String, Any?> = emptyMap(), noinline message: () -> String) {
    this.logger().logInfo(context, message)
}

inline fun <reified T : Any> T.logDebug(context: Map<String, Any?> = emptyMap(), noinline message: () -> String) {
    this.logger().logDebug(context, message)
}

inline fun <reified T : Any> T.logTrace(context: Map<String, Any?> = emptyMap(), noinline message: () -> String) {
    this.logger().logTrace(context, message)
}

inline fun <reified T : Any> T.logWarn(cause: Throwable? = null, context: Map<String, Any?> = emptyMap(), noinline message: () -> String) {
    this.logger().logWarn(cause, context, message)
}

inline fun <reified T : Any> T.logError(cause: Throwable? = null, context: Map<String, Any?> = emptyMap(), noinline message: () -> String) {
    this.logger().logError(cause, context, message)
}