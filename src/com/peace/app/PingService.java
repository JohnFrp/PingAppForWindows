package com.peace.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingService {
    private Process pingProcess;
    private Thread pingThread;
    private boolean isPinging;
    private final Consumer<String> outputConsumer;

    private static final String IPV4_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
    private static final String DOMAIN_PATTERN = "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern PING_RESPONSE_PATTERN = Pattern.compile("time=(\\d+ms)");
    
    // Timeout and unreachable messages
    private static final String TIMEOUT_MESSAGE = "Request timed out.";
    private static final String UNREACHABLE_MESSAGE = "Destination host unreachable";

    public PingService(Consumer<String> outputConsumer) {
        this.outputConsumer = outputConsumer;
    }

    public void startPing(String host) {
        isPinging = true;

        pingThread = new Thread(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", host, "/t");
            try {
                pingProcess = processBuilder.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pingProcess.getInputStream()))) {
                    String line;
                    while (isPinging && (line = reader.readLine()) != null) {
                        if (line.contains(TIMEOUT_MESSAGE)) {
                            outputConsumer.accept("Error");
                        } else if (line.contains(UNREACHABLE_MESSAGE)) {
                            outputConsumer.accept("ERROR");
                        } else {
                            Matcher matcher = PING_RESPONSE_PATTERN.matcher(line);
                            if (matcher.find()) {
                                String ms = matcher.group(1).replace("ms", "").trim();
                                int milliseconds = Integer.parseInt(ms);
                                outputConsumer.accept(formatAsClock(milliseconds) + "MS");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                outputConsumer.accept("Error occurred during ping execution.");
                ex.printStackTrace();
            } finally {
                stopPing();
            }
        });

        pingThread.start();
    }

    public void stopPing() {
        isPinging = false;
        if (pingProcess != null) {
            pingProcess.destroy();
        }
        if (pingThread != null && pingThread.isAlive()) {
            pingThread.interrupt();
        }
    }

    private String formatAsClock(int milliseconds) {
        int ms = milliseconds % 1000;
        return String.format("%03d", ms);
    }

    public static boolean isValidIP(String ip) {
        return Pattern.matches(IPV4_PATTERN, ip);
    }

    public static boolean isValidDomain(String domain) {
        return Pattern.matches(DOMAIN_PATTERN, domain);
    }
}
