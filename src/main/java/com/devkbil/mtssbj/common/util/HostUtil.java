package com.devkbil.mtssbj.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class HostUtil {

    public HostUtil() {
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(getHostNameRt());
        System.out.println(getHostNameInet());
        System.out.println(hostCheck("DESKTOP-288GLL6"));
    }

    /**
     * Get HostName at Runtime Method
     *
     * @return
     */
    public static String getHostNameRt() {
        String hostName = "";
        String[] cmdArray = {"hostname"}; // @Deprecated(since="18")

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmdArray);
            int inp;
            while ((inp = proc.getInputStream().read()) != -1) {
                hostName += (char) inp;
            }
            proc.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return hostName.trim();
    }

    /**
     * Get HostName at InetAddress Method
     *
     * @return
     * @throws UnknownHostException
     */
    public static String getHostNameInet() throws UnknownHostException {
        String hostName = InetAddress.getLocalHost().getHostName();
        return hostName;
    }

    /**
     * HostName check
     *
     * @return
     * @throws UnknownHostException
     */
    public static boolean hostCheck(String indexingHost) throws UnknownHostException {
        // batch HOSTNAME Check
        if (!"".equals(indexingHost) || indexingHost != null) {
            //String sHostName = InetAddress.getLoopbackAddress().getHostName();
            String hostName = getHostNameInet();
            return indexingHost.equals(hostName);
        }
        return false;
    }
}
