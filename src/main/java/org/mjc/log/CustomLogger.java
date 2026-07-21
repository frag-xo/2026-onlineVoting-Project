package org.mjc.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 自定义日志工具类（基于 SLF4J）
 * 使用 SLF4J API，底层实现可以是 Logback 或 Log4j2
 */
public class CustomLogger {

    private static final CustomLogger instance = new CustomLogger();

    // 自定义 Marker（用于标记特殊日志）
    private static final Marker CUSTOMER_MARKER = MarkerFactory.getMarker("CUSTOMER");

    // 自定义日志级别名称
    public static final String CUSTOMER_LEVEL = "CUSTOMER";

    private CustomLogger() {
    }

    public static CustomLogger getInstance() {
        return instance;
    }

    /**
     * 创建自定义日志记录器
     * 注意：SLF4J 本身不负责文件输出，由底层实现（Logback/Log4j2）配置决定
     * 这里返回一个带有特定名称的 Logger，方便在配置文件中单独配置
     *
     * @param filePath          日志输出路径
     * @param fileName          日志文件名
     * @param conversionPattern 日志格式
     * @param flag              true:追加 false:覆盖
     * @return Logger 对象
     */
    public Logger createLogger(String filePath, String fileName,
                               String conversionPattern, boolean flag) {
        // 创建日志目录
        createLogDirectory(filePath);

        // 使用 fileName 作为 Logger 名称，方便在配置文件中配置
        String loggerName = fileName + "." + System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(loggerName);

        // 记录 Logger 创建信息（可用于调试）
        logger.debug("Created logger: {}, path: {}, pattern: {}, append: {}",
                loggerName, filePath, conversionPattern, flag);

        return logger;
    }

    /**
     * 创建日志目录
     */
    private void createLogDirectory(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.err.println("Failed to create log directory: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * 使用自定义标记记录日志（INFO 级别）
     *
     * @param logger  日志对象
     * @param logInfo 日志信息
     */
    public void customLog(Logger logger, Object logInfo) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(CUSTOMER_MARKER, "{}", logInfo);
        }
    }

    /**
     * 使用自定义标记记录不同级别的日志
     *
     * @param logger  日志对象
     * @param logInfo 日志信息
     * @param level   日志级别 (DEBUG, INFO, WARN, ERROR, TRACE)
     */
    public void customLog(Logger logger, Object logInfo, String level) {
        if (logger == null) {
            return;
        }

        String message = logInfo != null ? logInfo.toString() : "null";

        switch (level.toUpperCase()) {
            case "DEBUG":
                if (logger.isDebugEnabled()) {
                    logger.debug(CUSTOMER_MARKER, "{}", message);
                }
                break;
            case "WARN":
                if (logger.isWarnEnabled()) {
                    logger.warn(CUSTOMER_MARKER, "{}", message);
                }
                break;
            case "ERROR":
                if (logger.isErrorEnabled()) {
                    logger.error(CUSTOMER_MARKER, "{}", message);
                }
                break;
            case "TRACE":
                if (logger.isTraceEnabled()) {
                    logger.trace(CUSTOMER_MARKER, "{}", message);
                }
                break;
            case "INFO":
            default:
                if (logger.isInfoEnabled()) {
                    logger.info(CUSTOMER_MARKER, "{}", message);
                }
                break;
        }
    }

    /**
     * 使用自定义标记记录日志（带异常）
     *
     * @param logger  日志对象
     * @param logInfo 日志信息
     * @param throwable 异常
     */
    public void customLog(Logger logger, Object logInfo, Throwable throwable) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(CUSTOMER_MARKER, logInfo != null ? logInfo.toString() : "null", throwable);
        }
    }

    /**
     * 检查是否启用了自定义日志
     *
     * @param logger 日志对象
     * @param level  日志级别
     * @return true 如果启用了该级别
     */
    public boolean isCustomLogEnabled(Logger logger, String level) {
        if (logger == null) {
            return false;
        }

        switch (level.toUpperCase()) {
            case "DEBUG":
                return logger.isDebugEnabled();
            case "WARN":
                return logger.isWarnEnabled();
            case "ERROR":
                return logger.isErrorEnabled();
            case "TRACE":
                return logger.isTraceEnabled();
            case "INFO":
            default:
                return logger.isInfoEnabled();
        }
    }

    /**
     * 获取自定义 Marker
     *
     * @return CUSTOMER_MARKER
     */
    public Marker getCustomerMarker() {
        return CUSTOMER_MARKER;
    }

    /**
     * 获取日志记录器（基于类名）
     *
     * @param clazz 类
     * @return Logger 对象
     */
    public Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取日志记录器（基于名称）
     *
     * @param name 日志名称
     * @return Logger 对象
     */
    public Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    /**
     * 检查指定名称的 Logger 是否存在
     *
     * @param loggerName Logger 名称
     * @return true 如果存在
     */
    public boolean isLoggerExists(String loggerName) {
        // SLF4J 不直接提供检查 Logger 是否存在的方法
        // 可以尝试获取 Logger 并检查其是否被配置
        Logger logger = LoggerFactory.getLogger(loggerName);
        return logger != null;
    }

    /**
     * 关闭自定义日志
     * 注意：SLF4J 不直接提供关闭 Logger 的方法
     * 实际关闭操作需要由底层实现提供
     *
     * @param logger 日志对象
     */
    public void closeCustomLogger(Logger logger) {
        if (logger == null) {
            return;
        }

        try {
            // 检查底层是否支持关闭
            String loggerName = logger.getName();
            LoggerFactory.getLogger(loggerName);

            // 由于 SLF4J 本身不支持关闭，这里记录一条关闭信息
            // 实际关闭需要在 Logback 或 Log4j2 层面实现
            logger.info("Closing logger: {}", loggerName);

            // 如果是 Logback，可以尝试转换为 ch.qos.logback.classic.Logger
            if (logger instanceof ch.qos.logback.classic.Logger) {
                ch.qos.logback.classic.Logger logbackLogger =
                        (ch.qos.logback.classic.Logger) logger;
                logbackLogger.detachAndStopAllAppenders();
            }
        } catch (Exception e) {
            System.err.println("Failed to close logger: " + e.getMessage());
        }
    }
}