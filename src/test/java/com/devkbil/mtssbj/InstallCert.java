package com.devkbil.mtssbj;

import javax.net.ssl.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Class used to add the server's certificate to the KeyStore with your trusted
 * certificates.
 */
public class InstallCert {
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    /* 영세하거나 무료 인증서 같은 경우 java 키스토어에 인증서가 없을 수 있습니다.
        그런 경우 SSL 통신을 시도하면 아래와 같은 오류가 발생합니다.
        javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed:
        이런 경우 해당 사이트의 인증서를 받아서 java 키스토어에 추가 해줘야 합니다.
        원래 Sun사에서 제공하던 InstallCert.java 를 좀 개량해서 한번의 실행만으로 모두 해결되도록 수정한 버전이 아래 소스 입니다.
        1. Host 및 Port 를 소스에서 변경하고 컴파일 후 실행합니다.
        [실행 내용]
        1. 해당 사이트의 SSL 인증서를 가지고 있는 지 확인해서 있다면 그냥 끝냅니다.
        2. 없다면 인증서를 내려 받습니다.
        3. 내려받은 인증서에서 여러 keytool을 이용해 각각 export해서 자바 기본 키스토어에 import 합니다.
        - 이 때 자바 기본 키스토어는 자동으로 백업 됩니다.
     */
    public static void main(String[] args) throws Exception {
        String host = "다운받을인증서 사이트 주소";
        int port = 443;// 다운받을인증서 사이트 포트
        char[] passphrase = "changeit".toCharArray(); // 자바 인증서 기본 암호
        char SEP = File.separatorChar;

        File cacerts = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security" + SEP + "cacerts");
        System.out.println("Loading KeyStore " + cacerts.getAbsolutePath() + "...");
        InputStream in = new FileInputStream(cacerts);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[] {tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        System.out.println("Opening connection to " + host + ":" + port + "...");
        SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
        socket.setSoTimeout(10000);

        try {
            System.out.println("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            System.out.println("\nNo errors, certificate is already trusted.");
            return;
        } catch (SSLException e) {
            System.out.println("\nSSL handshake failed:");
            e.printStackTrace(System.out);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain");
            return;
        }

        System.out.println("\nServer sent " + chain.length + " certificate(s):\n");
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];
            System.out.println(" " + (i + 1) + " Subject: " + cert.getSubjectX500Principal());
            System.out.println("   Issuer: " + cert.getIssuerX500Principal());
            sha1.update(cert.getEncoded());
            System.out.println("   SHA1: " + toHexString(sha1.digest()));
            md5.update(cert.getEncoded());
            System.out.println("   MD5: " + toHexString(md5.digest()));
            System.out.println();

            String alias = host + "-" + (i + 1);
            ks.setCertificateEntry(alias, cert);

            try (OutputStream out = new FileOutputStream("jssecacerts")) {
                ks.store(out, passphrase);
            }

            System.out.println("\nAdded certificate to keystore 'jssecacerts' using alias '" + alias + "'");

            // Keytool export command
            ProcessBuilder exportCommand = new ProcessBuilder(
                    "keytool", "-exportcert",
                    "-keystore", new File("jssecacerts").getAbsolutePath(),
                    "-storepass", "changeit",
                    "-file", "output.cert",
                    "-alias", alias
            );
            executeCommand(exportCommand);

            // Keystore backup
            Files.copy(cacerts.toPath(), new File(cacerts.getAbsolutePath() + ".bak." + new Date().getTime()).toPath());

            // Keytool import command
            ProcessBuilder importCommand = new ProcessBuilder(
                    "keytool", "-importcert", "-noprompt",
                    "-keystore", cacerts.getAbsolutePath(),
                    "-storepass", "changeit",
                    "-file", "output.cert",
                    "-alias", alias
            );
            System.out.println(String.join(" ", importCommand.command())); // For debugging
            executeCommand(importCommand);
        }
    }

    private static void executeCommand(ProcessBuilder command) throws IOException, InterruptedException {
        Process process = command.start();
        process.waitFor();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}
