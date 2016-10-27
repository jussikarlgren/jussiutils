package com.gavagai.jussiutil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Class implementing a client for logging metrics to a StatsD
 * server. StatsD (https://github.com/etsy/statsd) is "A network
 * daemon that runs on the Node.js platform and listens for statistics,
 * like counters and timers, sent over UDP and sends aggregates to one
 * or more pluggable backend services (e.g., Graphite)."
 *
 * NOTE that the caller of this class is responsible for using the
 * appropriate isLogXX-method for checking whether the logger is
 * supposed to log metrics for that particular task, e.g., call
 * isLogTooHighSpamlevel() prior to actually invoking the logger. This
 * is analoguous to the approach taken by SLF4J. NOTE also that if you
 * intend to use StatsdLogger for logging metrics *not* covered by
 * the current set of isLogXX-methods, you should add such methods
 * accordingly.
 */
public class StatsdLogger {

    private static final Log logger = LogFactory.getLog(StatsdLogger.class);

    public static final String SERVER_NAME_KEY = "com.gavagai.rabbit.utils.StatsdLogger.serverName";
    public static final String SERVER_PORT_KEY = "com.gavagai.rabbit.utils.StatsdLogger.serverPort";
    public static final String ENABLE_LOGGER_KEY = "com.gavagai.rabbit.utils.StatsdLogger.enableLogger";
    public static final String LOG_DOCUMENT_AGE_KEY = "com.gavagai.rabbit.utils.StatsdLogger.logDocumentAge";
    public static final String LOG_LANGUAGE_CHANGE_KEY = "com.gavagai.rabbit.utils.StatsdLogger.logLanguageChange";
    public static final String LOG_NUM_DOCS_SENT = "com.gavagai.rabbit.utils.StatsdLogger.logNumberOfDocumentsSent";
    public static final String LOG_NUM_TOO_OLD_DOCS = "com.gavagai.rabbit.utils.StatsdLogger.logNumberOfTooOldDocuments";
    public static final String LOG_NUM_DUPLICATE_DOCS = "com.gavagai.rabbit.utils.StatsdLogger.logNumberOfDuplicateDocuments";
    public static final String LOG_TOO_HIGH_SPAMLEVEL = "com.gavagai.rabbit.utils.StatsdLogger.logTooHighSpamlevel";

    private boolean loggerEnabled;
    private boolean logDocumentAge;
    private boolean logLanguageChange;
    private boolean logNumDocsSent;
    private boolean logNumTooOldDocs;
    private boolean logNumDuplicateDocs;
    private boolean logTooHighSpamLevel;
    private String statsdServer;
    private int statsdServerPort;
    private InetAddress statsdServerAddress;
    private String clientNodeIdentifier;

    /**
     * Method for creating a StatsD logger according to the settings specified
     * by the system properties described by SERVER_NAME_KEY, SERVER_PORT_KEY,
     * and LOGGING_ENABLE_KEY.
     *
     * @return A default StatsdLogger.
     */
    public static StatsdLogger getDefaultLogger() {
        String host = System.getProperty(SERVER_NAME_KEY, "localhost");
        Integer port = Integer.getInteger(SERVER_PORT_KEY, 33444);
        Boolean loggerEnabled = Boolean.getBoolean(ENABLE_LOGGER_KEY);

        Boolean logDocumentAge = Boolean.getBoolean(LOG_DOCUMENT_AGE_KEY);
        Boolean logLanguageChange = Boolean.getBoolean(LOG_LANGUAGE_CHANGE_KEY);
        Boolean logNumDocsSent = Boolean.getBoolean(LOG_NUM_DOCS_SENT);
        Boolean logNumTooOldDocs = Boolean.getBoolean(LOG_NUM_TOO_OLD_DOCS);
        Boolean logNumDuplicateDocs = Boolean.getBoolean(LOG_NUM_DUPLICATE_DOCS);
        Boolean logTooHighSpamlevel = Boolean.getBoolean(LOG_TOO_HIGH_SPAMLEVEL);

        StatsdLogger statsdLogger = new StatsdLogger(host, port, loggerEnabled);

        // The use of the following flags are up to the caller to decide, hence their
        // setting via explicit setters, and not in the constructor. Also, the number of
        // such flags is subject to change, and since we don't want to re-factor the
        // use of the constructor for each new flag, setters/getters are more appropriate.

        statsdLogger.setLogDocumentAge(logDocumentAge);
        statsdLogger.setLogLanguageChange(logLanguageChange);
        statsdLogger.setLogNumDocsSent(logNumDocsSent);
        statsdLogger.setLogNumTooOldDocs(logNumTooOldDocs);
        statsdLogger.setLogNumDuplicateDocs(logNumDuplicateDocs);
        statsdLogger.setLogTooHighSpamlevel(logTooHighSpamlevel);

        if (logger.isInfoEnabled()) {
            logger.info("StatsD logger created: " + statsdLogger.toString());
        }
        return statsdLogger;
    }

    /**
     * Constructor for obtaining a StatsD logger. Defaults to logging all
     * metrics: use setters for invoking appropriate "modules", e.g.,
     * setLogLanguageChange for enabling/disabling the logging of the amount
     * of re-assigned language. By default, logging for all "modules" is
     * turned off. Enable by using the appropriate setXX methods, or
     * system properties.
     *
     * @param statsdServer The name of the server, e.g., metrics.gavagai.se
     * @param statsdServerPort, The port on which the StatsD server listens to UDP traffic.
     * @param loggerEnabled Set to true if the logger should be capable of logging, false otherwise.
     */
    public StatsdLogger(String statsdServer, int statsdServerPort, boolean loggerEnabled) {
        this.statsdServer = statsdServer;
        this.statsdServerPort = statsdServerPort;
        this.loggerEnabled = loggerEnabled;

        this.logDocumentAge = false;
        this.logLanguageChange = false;
        this.logNumDocsSent = false;
        this.logNumTooOldDocs = false;
        this.logNumDuplicateDocs = false;
        this.logTooHighSpamLevel = false;

        try {
            this.statsdServerAddress = InetAddress.getByName(statsdServer);
        } catch (UnknownHostException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Unknown StatsD server: " + statsdServer, e);
            }
        }
        try {
            this.clientNodeIdentifier = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to determine host name for StatsD logger", e);
            }
        }

        if (!isOkString(this.statsdServer)
                || !isOkString(this.clientNodeIdentifier)
                || this.statsdServerPort < 0) {

            if (logger.isWarnEnabled()) {
                logger.warn("Failed to create StatsD logger since server, " +
                        "server port or client node identifier could not be " +
                        "identified properly: ");
            }
            this.loggerEnabled = false;
        }
    }

    /**
     * @param key Treat key as a counter and increase it by 1.
     * @return True if sending the counter was successful, false otherwise. (Note
     * that the return value *is not* indicative of whether the value was
     * actually received by the StatsD server).
     */
    public boolean logCounter(String key) {
        return logCounter(key, 1);
    }

    /**
     * @param key Treat key as a counter and increase it by value.
     * @param value The value by which key should be increased.
     * @return True if sending the counter was successful, false otherwise. (Note
     * that the return value *is not* indicative of whether the value was
     * actually received by the StatsD server).
     */
    public boolean logCounter(String key, int value) {
        return logCounter(key, value, 1d);
    }

    /**
     * @param key Treat key as a counter and increase it by value.
     * @param value The value by which key should be increased.
     * @param sampleRate A value between 0 and 1 indicating the rate by which
     * the counter is sampled from the underlying source.
     * @return True if sending the counter was successful, false otherwise. (Note
     * that the return value *is not* indicative of whether the value was
     * actually received by the StatsD server).
     */
    public boolean logCounter(String key, int value, double sampleRate) {
        return logMetric(MetricType.COUNTER, key, value, sampleRate);
    }

    /**
     * @param key Treat key as a timer.
     * @param timeInMillis The number of milliseconds associated with the timer.
     * @return True if sending the timer was successful, false otherwise. (Note
     * that the return value *is not* indicative of whether the value was
     * actually received by the StatsD server).
     */
    public boolean logTimer(String key, long timeInMillis) {
        return logTimer(key, timeInMillis, 1d);
    }

    /**
     * @param key Treat key as a timer.
     * @param timeInMillis The number of milliseconds associated with the timer.
     * @param sampleRate A value between 0 and 1 indicating the rate by which
     * the timer is sampled from the underlying source.
     * @return True if sending the timer was successful, false otherwise. (Note
     * that the return value *is not* indicative of whether the value was
     * actually received by the StatsD server).
     */
    public boolean logTimer(String key, long timeInMillis, double sampleRate) {
        return logMetric(MetricType.TIMER, key, timeInMillis, sampleRate);
    }

    /**
     * @param key Treat key as a gauge.
     * @param value The value of the gauge.
     * @return True if sending the timer was successful, false otherwise. (Note
     * that the return value *is not* indicative of whether the value was
     * actually received by the StatsD server).
     */
    public boolean logGauge(String key, int value) {
        return logMetric(MetricType.GAUGE, key, value, 1d);
    }

    /**
     * Statsd original documentation says: "StatsD supports counting
     * unique occurences of events between flushes, using a Set to
     * store all occuring events." An event appears to be defined as
     * the combination of key and value. For instance "key" combined
     * with "10" is a different event than "key" combined with "11",
     * in the same flushinterval (defaulting to 10 seconds; set in the
     * statsd server).
     *
     * @param key The key of the statistics to log.
     * @param value The value associated with the key.
     * @return true if sending the statistics to the server was
     * successful, false otherwise.
     */
    public boolean logUniqueEventCounter(String key, int value) {
        return logMetric(MetricType.UNIQUE, key, value, 1d);
    }

    public boolean isLogDocumentAgeEnabled() {
        return this.logDocumentAge && this.loggerEnabled;
    }

    public void setLogDocumentAge(boolean logDocumentAge) {
        this.logDocumentAge = logDocumentAge;
    }

    public boolean isLogLanguageChangeEnabled() {
        return this.logLanguageChange && this.loggerEnabled;
    }

    public void setLogLanguageChange(boolean logLanguageChange) {
        this.logLanguageChange = logLanguageChange;
    }

    public boolean isLogNumDocsSentEnabled() {
        return this.logNumDocsSent && this.loggerEnabled;
    }

    public void setLogNumDocsSent(boolean logNumDocsSent) {
        this.logNumDocsSent = logNumDocsSent;
    }

    public boolean isLogNumTooOldDocsEnabled() {
        return this.logNumTooOldDocs && this.loggerEnabled;
    }

    public void setLogNumTooOldDocs(boolean logNumTooOldDocs) {
        this.logNumTooOldDocs = logNumTooOldDocs;
    }

    public boolean isLogNumDuplicateDocsEnabled() {
        return this.logNumDuplicateDocs && this.loggerEnabled;
    }

    public void setLogNumDuplicateDocs(boolean logNumDuplicateDocs) {
        this.logNumDuplicateDocs = logNumDuplicateDocs;
    }

    public boolean isLogTooHighSpamLevelEnabled() {
        return this.logTooHighSpamLevel && this.loggerEnabled;
    }

    public void setLogTooHighSpamlevel(boolean logTooHighSpamLevel) {
        this.logTooHighSpamLevel = logTooHighSpamLevel;
    }

    protected boolean logMetric(MetricType type, String key, long value, double sampleRate) {
        return logToStatsd(
                getStatsAsString(type, this.clientNodeIdentifier,
                        key, value, sampleRate));
    }

    protected String getStatsAsString(MetricType type, String clientNodeIdentifier,
                                      String key, long value, double sampleRate) {
        String result = null;

        if (isOkString(clientNodeIdentifier) && isOkString(key) && isOkSampleRate(sampleRate)) {
            String intermediate =
                    String.format(type.getFormat(clientNodeIdentifier), key, value);
            if (sampleRate < 1d) {
                // Use Locale.US to get a .-separated sample rate, regardless of which
                // locale is used by the system at the time of formatting.
                result = String.format(Locale.US, type.getSampleRateFormat(),
                        intermediate, sampleRate);
            } else {
                result = intermediate;
            }
        } else {
            if(logger.isWarnEnabled()) {
                StringBuilder s = new StringBuilder("Failed to get statistics as string. ");
                s.append("Configuration out of bounds: ");
                s.append("clientNodeIdentifier=").append(clientNodeIdentifier);
                s.append(", key=").append(key);
                s.append(", sampleRate=").append(sampleRate);
                logger.warn(s.toString());
            }
        }
        return result;
    }

    protected boolean isOkString(String key) {
        return key != null && key.trim().length() > 0;
    }

    protected boolean isOkSampleRate(double sampleRate) {
        return sampleRate >= 0d && sampleRate <= 1d;
    }

    protected boolean logToStatsd(String statistics) {
        boolean isSuccess = true;
        try {
            logToStatsdUDP(statistics);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot log to StatsD server. Statistics key to log: "
                        + statistics + ", configuration: "
                        + toString() ,e);
            }
            isSuccess = false;
        }
        return isSuccess;
    }

    protected void logToStatsdUDP(String statistics) throws Exception {
        if (statistics == null || statistics.length() == 0) {
            throw new Exception("Attempting to log empty statistics!");
        }
        if (this.loggerEnabled) {
            byte[] outBuffer = statistics.getBytes();
            DatagramSocket socket = new DatagramSocket();
            try {
                socket.connect(statsdServerAddress, statsdServerPort);
                DatagramPacket udpPacket =
                        new DatagramPacket(outBuffer, outBuffer.length,
                                statsdServerAddress, statsdServerPort);
                if (logger.isTraceEnabled()) {
                    logger.trace("sending UDP packet: "
                            + getDatagramPacketAsString(udpPacket));
                }
                socket.send(udpPacket);
            } finally {
                socket.close();
            }
        }
    }

    protected String getDatagramPacketAsString(DatagramPacket packet) {
        StringBuilder s = new StringBuilder("DatagramPacket[");
        s.append("inetAddress=").append(packet.getAddress().toString());
        s.append(", port=").append(packet.getPort());
        s.append(", socketAddress=").append(packet.getSocketAddress());
        s.append(", data=\'").append(new String(packet.getData())).append("\'");
        s.append("]");
        return s.toString();
    }

    public boolean isEnabled() {
        return this.loggerEnabled;
    }

    public String toString() {
        StringBuilder s = new StringBuilder("StatsdLogger[");
        s.append("statsdServer=").append(this.statsdServer);
        s.append(", statsdServerPort=").append(this.statsdServerPort);
        s.append(", loggerEnabled=").append(this.loggerEnabled);
        s.append(", logDocumentAge=").append(this.isLogDocumentAgeEnabled());
        s.append(", logLanguageChange=").append(this.isLogDocumentAgeEnabled());
        s.append(", logNumDocsSent=").append(this.isLogDocumentAgeEnabled());
        s.append(", logNumTooOldDocs=").append(this.isLogDocumentAgeEnabled());
        s.append(", logNumDuplicateDocs=").append(this.isLogDocumentAgeEnabled());
        s.append(", logTooHighSpamlevel=").append(this.isLogTooHighSpamLevelEnabled());
        s.append(", clientNodeIdentifier=").append(this.clientNodeIdentifier);
        s.append("]");
        return s.toString();
    }

    protected enum MetricType {
        COUNTER("%s:%d|c"),
        TIMER("%s:%d|ms"),
        GAUGE("%s:%d|g"),
        UNIQUE("%s:%d|s");

        private final String sampleRateFormat = "%s|@%f";
        private String format;

        MetricType(String format) {
            this.format = format;
        }

        public String getFormat(String clientNodeIdentifier) {
            return clientNodeIdentifier + "." + this.format;
        }

        public String getSampleRateFormat() {
            return this.sampleRateFormat;
        }
    }

}
