@Service
public class AdlsScannerService {

    private static final Logger logger = LoggerFactory.getLogger(AdlsScannerService.class);

    @Autowired
    private DataLakeServiceClient dataLakeServiceClient;

    @Autowired
    private FileMetadataRepository metadataRepository;

    @Autowired
    private ScanTargetRepository scanTargetRepository;

    @Scheduled(cron = "0 0 * * * *") // hourly
    public void scan() {
        logger.info("ADLS scan started...");
        try {
            List<ScanTarget> targets = scanTargetRepository.findByIsActiveTrue();
            for (ScanTarget target : targets) {
                String container = target.getContainerName();
                String dir = target.getDirectoryPath();

                DataLakeFileSystemClient fsClient = dataLakeServiceClient.getFileSystemClient(container);
                DataLakeDirectoryClient dirClient = fsClient.getDirectoryClient(dir);

                for (PathItem item : dirClient.listPaths(true)) {
                    if (Boolean.FALSE.equals(item.isDirectory())) {
                        String path = item.getName();
                        Long size = item.getContentLength();
                        Timestamp lastModified = Timestamp.from(item.getLastModified().toInstant());

                        if (!metadataRepository.existsByFilePathAndLastModified(path, lastModified)) {
                            FileMetadata meta = new FileMetadata();
                            meta.setFilePath(path);
                            meta.setFileSize(size);
                            meta.setLastModified(lastModified);
                            metadataRepository.save(meta);
                            logger.info("New file added: {}", path);
                        } else {
                            logger.info("Already scanned: {}", path);
                        }
                    }
                }
            }
            logger.info("ADLS scan completed.");
        } catch (Exception e) {
            logger.error("Error while scanning ADLS: ", e);
        }
    }
}
