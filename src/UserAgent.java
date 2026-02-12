public class UserAgent {

    private final String operatingSystem;
    private final String browser;

    public UserAgent(String userAgentString) {

        if (userAgentString.contains("Windows"))
            operatingSystem = "Windows";
        else if (userAgentString.contains("Mac"))
            operatingSystem = "macOS";
        else if (userAgentString.contains("Linux"))
            operatingSystem = "Linux";
        else
            operatingSystem = "Other";

        if (userAgentString.contains("Edge"))
            browser = "Edge";
        else if (userAgentString.contains("Firefox"))
            browser = "Firefox";
        else if (userAgentString.contains("Chrome"))
            browser = "Chrome";
        else if (userAgentString.contains("Opera"))
            browser = "Opera";
        else
            browser = "Other";
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }
}