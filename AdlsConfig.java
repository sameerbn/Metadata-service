import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure.adls")
public class AzureAdlsProperties {
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String accountName;
    private Proxy proxy = new Proxy();

    @Data
    public static class Proxy {
        private boolean enabled;
        private String host;
        private int port;
    }
}
