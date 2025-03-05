package com.devkbil.mtssbj;

import java.io.*;

public class ShellCommandTest2 {

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String[] linuxCmd = new String[] {"sh", "-c", "ifconfig"};
        String[] windowCmd = new String[] {"cmd.exe", "/c", "gradlew dependencies --configuration compileClasspath"};
        String OS = "Mac";
        String output = "";
        String s = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            // builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            // builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            if (isWindows) {
                builder.command(windowCmd);
            } else {
                builder.command(linuxCmd);
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader sI = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((s = sI.readLine()) != null) {
                output += s;
            }
            int exitCode = process.waitFor();
            System.out.println(output);
            System.out.println(exitCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
