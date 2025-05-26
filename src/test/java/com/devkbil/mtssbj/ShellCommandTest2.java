package com.devkbil.mtssbj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ShellCommandTest2 {

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows");
        String[] linuxCmd = new String[] {"sh", "-c", "ifconfig"};
        String[] windowCmd = new String[] {"cmd.exe", "/c", "gradlew dependencies --configuration compileClasspath"};
        String output = "";
        String line;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                builder.command(windowCmd);
            } else {
                builder.command(linuxCmd);
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                output += line;
            }
            int exitCode = process.waitFor();
            System.out.println(output);
            System.out.println(exitCode);
        } catch (IOException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
    }
}
