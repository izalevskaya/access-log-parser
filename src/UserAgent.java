public class UserAgent {

    private final String userAgent;

    public UserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getOperatingSystem() {
        String ua = userAgent.toLowerCase();

        if (ua.contains("windows")) {
            return "Windows";
        } else if (ua.contains("mac")) {
            return "macOS";
        } else if (ua.contains("linux")) {
            return "Linux";
        } else if (ua.contains("android")) {
            return "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            return "iOS";
        }

        return "Unknown";
    }

    public String getBrowser() {
        String ua = userAgent.toLowerCase();

        if (ua.contains("edge") || ua.contains("edg")) {
            return "Edge";
        } else if (ua.contains("opr") || ua.contains("opera")) {
            return "Opera";
        } else if (ua.contains("chrome") && !ua.contains("edge") && !ua.contains("edg")) {
            return "Chrome";
        } else if (ua.contains("firefox")) {
            return "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "Safari";
        } else if (ua.contains("msie") || ua.contains("trident")) {
            return "Internet Explorer";
        }

        return "Unknown";
    }

    // НОВЫЙ МЕТОД для определения бота
    public boolean isBot() {
        String ua = userAgent.toLowerCase();

        // Список ключевых слов, указывающих на бота
        String[] botKeywords = {
                "bot", "crawler", "spider", "scanner", "checker", "monitor",
                "yahoo! slurp", "googlebot", "yandexbot", "bingbot", "baiduspider",
                "facebookexternalhit", "twitterbot", "slackbot", "telegrambot",
                "ahrefs", "semrush", "mj12bot", "dotbot", "rogerbot", "exabot",
                "curl", "wget", "python-requests", "java", "httpclient", "scrapy"
        };

        for (String keyword : botKeywords) {
            if (ua.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}