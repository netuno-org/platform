package org.netuno.psamata.ftp;

import org.junit.jupiter.api.Test;
import org.netuno.psamata.ConfigTest;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;

public class FTPClientTest {
    
    @Test
    public void test() throws Exception {
        Values config = ConfigTest.get().getValues("ftp").getValues("default");
        FTPConfig ftpConfig = new FTPConfig()
            .setHost(config.getString("host"))
            .setUsername(config.getString("username"))
            .setPassword(config.getString("password"));
        
        FTPClient ftpClient = new FTPClient(ftpConfig)
            .connect();
        
        System.out.println("Listing /");
        for (FTPFile ftpFile : ftpClient.list("/")) {
            System.out.printf("[%d]\n", System.currentTimeMillis());
            System.out.printf("[%d] Get name : %s \n", System.currentTimeMillis(), ftpFile.getName());
            System.out.printf("[%d] Get timestamp : %s \n", System.currentTimeMillis(), ftpFile.getTimestamp().getTimeInMillis());
            System.out.printf("[%d] Get group : %s \n", System.currentTimeMillis(), ftpFile.getGroup());
            System.out.printf("[%d] Get link : %s \n", System.currentTimeMillis(), ftpFile.getLink());
            System.out.printf("[%d] Get user : %s \n", System.currentTimeMillis(), ftpFile.getUser());
            System.out.printf("[%d] Is file : %s \n", System.currentTimeMillis(), ftpFile.isFile());
            System.out.printf("[%d] Is directory : %s \n", System.currentTimeMillis(), ftpFile.isDirectory());
            System.out.printf("[%d] Formatted string : %s \n", System.currentTimeMillis(), ftpFile.toFormattedString());
            System.out.println();
        }

        ftpClient.upload("/docs/coder.json", new File("/home/edu-main/coder.json"));
        System.out.println(new String(ftpClient.downloadText("/docs/coder.json")));
        /*ftpClient.createDirectory(directoryPath);
        ftpClient.uploadFile("C:\\test.jpg", path);
        ftpClient.renameFile(path, newPath);
        byte[] downloadFile = ftpClient.downloadFile(newPath);
        System.out.println("Downloaded file : " + new String(downloadFile));
        ftpClient.deleteFile(newPath);
        ftpClient.deleteDirectory(directoryPath);*/
        ftpClient.disconnect();
    }

}
