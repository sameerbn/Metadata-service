import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdlsScannerService {

    private static final Logger logger = LoggerFactory.getLogger(AdlsScannerService.class);

    @Autowired
    private DataLakeServiceClient dataLakeServiceClient;

    @Autowired
    private FileMetadataRepository metadataRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void scan() {
        logger.info("ADLS scan started...");

        try {
            DataLakeFileSystemClient fileSystemClient = dataLakeServiceClient.getFileSystemClient("your-container");
            DataLakeDirectoryClient dirClient = fileSystemClient.getDirectoryClient("your-dir");

            for (PathItem item : dirClient.listPaths(true)) {
                if (Boolean.FALSE.equals(item.isDirectory())) {
                    String path = item.getName();
                    Long size = item.getContentLength();
                    Timestamp lastModified = Timestamp.from(item.getLastModified().toInstant());

                    if (!metadataRepository.existsByFilePathAndLastModified(path, lastModified)) {
                        FileMetadata metadata = new FileMetadata();
                        metadata.setFilePath(path);
                        metadata.setFileSize(size);
                        metadata.setLastModified(lastModified);
                        metadataRepository.save(metadata);
                        logger.info("New file added: {}", path);
                    } else {
                        logger.info("File already scanned, skipping: {}", path);
                    }
                }
            }

            logger.info("ADLS scan completed.");
        } catch (Exception e) {
            logger.error("Error while scanning ADLS: ", e);
        }
    }
}
