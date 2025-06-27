package com.devkbil.mtssbj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class ShellCommandTest {
    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().forEach(consumer);
        }
    }

    private static void processBuilder(boolean isWindows) throws IOException, InterruptedException {
        System.out.println(":: START :: Use ProcessBuilder");
        String homeDirectory = System.getProperty("user.home");
        System.out.println(":: homeDirectory is " + homeDirectory);

        ProcessBuilder builder = new ProcessBuilder();
        // builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        // builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        builder.directory(new File(homeDirectory));
        String[] commandAndOptionsProcessBuilder;
        if (isWindows) {
            commandAndOptionsProcessBuilder = new String[] {"cmd.exe", "/c", "gradlew dependencies --configuration compileClasspath"};
        } else {
            commandAndOptionsProcessBuilder = new String[] {"sh", "-c", "ls -l | grep P"};
        }
        builder.command(commandAndOptionsProcessBuilder);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);

        Future<?> unused = Executors.newSingleThreadExecutor().submit(streamGobbler);

        int exitCode = process.waitFor();
        assert exitCode == 0;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows");
        System.out.println(":: OS is " + (isWindows ? "window" : "mac"));
        ShellCommandTest.processBuilder(isWindows);
    }
}
