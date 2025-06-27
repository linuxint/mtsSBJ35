package com.devkbil.mtssbj;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FIleDownloadTest {
    public static void main(String[] args) {
        String urlStr = "http://localhost:9090/fileDownload?filename=%EA%B1%B7%EA%B8%B0%EC%95%88%EB%82%B4.txt&downname=202211160953101070";
        String fileName = "download.txt";
        URL url;

        try {
            url = new URI(urlStr).toURL();

            ReadableByteChannel inputChannel = Channels.newChannel(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            FileChannel outputChannel = fileOutputStream.getChannel();

            outputChannel.transferFrom(inputChannel, 0, Long.MAX_VALUE);

        } catch (MalformedURLException ex) {
            // handle exception if needed
        } catch (IOException | URISyntaxException ex) {
            // handle exception if needed
        }
    }
}
