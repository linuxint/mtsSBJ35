package com.devkbil.mtssbj;

import java.io.*;
import java.util.concurrent.Executors;
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
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
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
        String[] commandAndOptions_ProcessBuilder;
        if (isWindows) {
            commandAndOptions_ProcessBuilder = new String[] {"cmd.exe", "/c", "gradlew dependencies --configuration compileClasspath"};
        } else {
            commandAndOptions_ProcessBuilder = new String[] {"sh", "-c", "ls -l | grep P"};
        }
        builder.command(commandAndOptions_ProcessBuilder);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);

        Executors.newSingleThreadExecutor().submit(streamGobbler);

        int exitCode = process.waitFor();
        assert exitCode == 0;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        System.out.println(":: OS is " + (isWindows ? "window" : "mac"));
        ShellCommandTest.processBuilder(isWindows);
    }
}
