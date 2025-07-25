@Configuration
public class AdlsConfig {

    @Value("${azure.adls.account-name}")
    private String accountName;

    @Value("${azure.adls.account-key}")
    private String accountKey;

    @Bean
    public DataLakeServiceClient dataLakeServiceClient() {
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
        return new DataLakeServiceClientBuilder()
                .credential(credential)
                .endpoint("https://" + accountName + ".dfs.core.windows.net")
                .buildClient();
    }
}
