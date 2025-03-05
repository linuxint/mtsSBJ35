package com.devkbil.mtssbj.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class SftpUtil {

    @Autowired
    private SftpService sftpService;

    public void sftpInit(String ip, int port, String id, String pw) {
        sftpService.configure(ip, port, id, pw);
        System.out.println("SFTP initialization success.");
    }

    public void disconnect() {
        sftpService.disconnect();
        System.out.println("SFTP disconnected.");
    }

    public void sftpFileUpload(String remoteDirectory, String localPath, String uploadFileNm)
        throws IOException {
        File file = new File(localPath + uploadFileNm);
        sftpService.uploadFile(remoteDirectory, file);
        System.out.println("SFTP file upload success.");
    }

    public void sftpMultiFileUpload(
        String remoteDirectory, String localPath, List<String> uploadFiles) throws IOException {
        for (String fileName : uploadFiles) {
            sftpFileUpload(remoteDirectory, localPath, fileName);
        }
    }

    public void sftpFileDownload(String remoteDirectory, String remoteFileName, String localFilePath)
        throws IOException {
        sftpService.downloadFile(remoteDirectory, remoteFileName, localFilePath);
        System.out.println("SFTP file download success.");
    }

    public String sftpSearchFile(String remoteDirectory, String fileNamePattern) {
        String foundFileName = sftpService.sftpSearchFile(remoteDirectory, fileNamePattern);
        System.out.println(foundFileName != null ? "File found: " + foundFileName : "File not found.");
        return foundFileName;
    }
}
