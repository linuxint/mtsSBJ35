package com.devkbil.mtssbj.mail;

import org.springframework.util.StringUtils;
import java.util.Locale;

/**
 * Simple resolver to derive IMAP/SMTP hosts and ports from an email address domain.
 * This is intentionally minimal and covers popular providers with sensible defaults.
 */
public final class MailServerResolver {

    public static class MailServers {
        public final String imapHost;
        public final String imapPort;
        public final String smtpHost;
        public final String smtpPort;
        public MailServers(String imapHost, String imapPort, String smtpHost, String smtpPort) {
            this.imapHost = imapHost;
            this.imapPort = imapPort;
            this.smtpHost = smtpHost;
            this.smtpPort = smtpPort;
        }
    }

    private MailServerResolver() {}

    public static MailServers resolve(String email) {
        String domain = extractDomain(email);
        if (!StringUtils.hasText(domain)) {
            // Fallback generic guess
            return new MailServers("imap." + "example.com", "993", "smtp." + "example.com", "465");
        }
        String d = domain.toLowerCase(Locale.ROOT);
        switch (d) {
            case "gmail.com":
                return new MailServers("imap.gmail.com", "993", "smtp.gmail.com", "465");
            case "naver.com":
                return new MailServers("imap.naver.com", "993", "smtp.naver.com", "465");
            case "daum.net":
            case "hanmail.net":
                return new MailServers("imap.daum.net", "993", "smtp.kakao.com", "465");
            case "kakao.com":
                return new MailServers("imap.kakao.com", "993", "smtp.daum.net", "465");
            case "nate.com":
                return new MailServers("imap.nate.com", "993", "smtp.nate.com", "465");
            case "outlook.com":
            case "hotmail.com":
            case "live.com":
            case "msn.com":
                return new MailServers("outlook.office365.com", "993", "smtp.office365.com", "587");
            case "yahoo.com":
                return new MailServers("imap.mail.yahoo.com", "993", "smtp.mail.yahoo.com", "465");
            case "icloud.com":
            case "me.com":
                return new MailServers("imap.mail.me.com", "993", "smtp.mail.me.com", "587");
            case "aol.com":
                return new MailServers("imap.aol.com", "993", "smtp.aol.com", "465");
            default:
                // Common convention: imap.<domain>, smtp.<domain>
                return new MailServers("imap." + d, "993", "smtp." + d, "465");
        }
    }

    private static String extractDomain(String email) {
        if (!StringUtils.hasText(email)) return null;
        int at = email.indexOf('@');
        if (at < 0 || at == email.length() - 1) return null;
        return email.substring(at + 1);
    }
}
