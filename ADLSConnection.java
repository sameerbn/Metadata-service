import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;
import com.azure.core.http.ProxyOptions;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class ADLSConnection {

    private final AzureAdlsProperties properties;

    public ADLSConnection(AzureAdlsProperties properties) {
        this.properties = properties;
    }

    public DataLakeServiceClient getClient() {
        ClientSecretCredentialBuilder credentialBuilder = new ClientSecretCredentialBuilder()
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .tenantId(properties.getTenantId());

        if (properties.getProxy().isEnabled()) {
            ProxyOptions proxyOptions = new ProxyOptions(
                    ProxyOptions.Type.HTTP,
                    new InetSocketAddress(properties.getProxy().getHost(), properties.getProxy().getPort())
            );
            credentialBuilder.proxyOptions(proxyOptions);
        }

        TokenCredential credential = credentialBuilder.build();

        String endpoint = "https://" + properties.getAccountName() + ".dfs.core.windows.net";

        return new DataLakeServiceClientBuilder()
                .credential(credential)
                .endpoint(endpoint)
                .buildClient();
    }
}
