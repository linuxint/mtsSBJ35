package com.devkbil.common.util;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class SftpService {

    private SftpRemoteFileTemplate remoteFileTemplate;

    @Autowired
    public SftpService(SftpRemoteFileTemplate remoteFileTemplate) {
        this.remoteFileTemplate = remoteFileTemplate;
    }

    public void configure(String host, int port, String user, String password) {
        DefaultSftpSessionFactory sftpSessionFactory = new DefaultSftpSessionFactory();
        sftpSessionFactory.setHost(host);
        sftpSessionFactory.setPort(port);
        sftpSessionFactory.setUser(user);
        sftpSessionFactory.setPassword(password);
        sftpSessionFactory.setAllowUnknownKeys(true);
        this.remoteFileTemplate = new SftpRemoteFileTemplate(sftpSessionFactory);
    }

    public void uploadFile(String remoteDirectory, File file) throws IOException {
        if (remoteFileTemplate == null) {
            throw new IllegalStateException("SFTP Service is not configured. Call configure() first.");
        }

        remoteFileTemplate.execute(
            session -> {
                try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                    session.write(inputStream, remoteDirectory + "/" + file.getName());
                }
                return null;
            });
    }

    public void downloadFile(String remoteDirectory, String remoteFileName, String localFilePath)
        throws IOException {
        if (remoteFileTemplate == null) {
            throw new IllegalStateException("SFTP Service is not configured. Call configure() first.");
        }

        Path localPath = Path.of(localFilePath);
        Files.createDirectories(localPath.getParent());

        remoteFileTemplate.execute(
            session -> {
                try (InputStream inputStream = session.readRaw(remoteDirectory + "/" + remoteFileName); FileOutputStream outputStream = new FileOutputStream(localPath.toFile())) {
                    IOUtils.copy(inputStream, outputStream);
                }
                return null;
            });
    }

    public String sftpSearchFile(String remoteDirectory, String fileNamePattern) {
        if (remoteFileTemplate == null) {
            throw new IllegalStateException("SFTP Service is not configured. Call configure() first.");
        }

        return remoteFileTemplate.execute(
            session -> Arrays.stream(session.list(remoteDirectory))
                .map(file -> file.getFilename())
                .filter(filename -> filename.matches(fileNamePattern))
                .findFirst()
                .orElse(null));
    }

    public void disconnect() {
        if (remoteFileTemplate != null) {
            remoteFileTemplate.execute(
                session -> {
                    session.close();
                    return null;
                });
        }
    }
}