@Slf4j
@Service
@RequiredArgsConstructor
public class AdlsScannerService {

    private static final int DAYS_TO_KEEP = 3;

    private final ADLSConnection adlsConnection;
    private final FileMetadataRepository metadataRepository;
    private final ScanTargetRepository scanTargetRepository;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void scan() {
        log.info("ADLS scan started...");

        List<ScanTarget> targets = scanTargetRepository.findByIsActiveTrue();
        LocalDate cutoffDate = LocalDate.now().minusDays(DAYS_TO_KEEP);

        for (ScanTarget target : targets) {
            try {
                DataLakeFileSystemClient fsClient = adlsConnection.getClient()
                    .getFileSystemClient(target.getContainerName());

                DataLakeDirectoryClient dirClient = fsClient.getDirectoryClient(target.getDirectoryPath());

                for (PathItem item : dirClient.listPaths(true)) {
                    if (Boolean.TRUE.equals(item.isDirectory())) continue;

                    String fullPath = item.getName();
                    Optional<LocalDate> extractedDate = DateExtractor.extractFromPath(fullPath);

                    if (!extractedDate.isPresent() || extractedDate.get().isBefore(cutoffDate)) continue;

                    Timestamp lastModified = Timestamp.from(item.getLastModified().toInstant());

                    if (!metadataRepository.existsByFilePathAndLastModified(fullPath, lastModified)) {
                        FileMetadata meta = new FileMetadata();
                        meta.setFilePath(fullPath);
                        meta.setFileSize(item.getContentLength());
                        meta.setLastModified(lastModified);
                        meta.setTargetDirectoryPath(target.getTargetDirectoryPath());
                        meta.setPairingKey(target.getPairingKey());
                        meta.setStatus("NEW");

                        metadataRepository.save(meta);
                        log.info("New file added: {}", fullPath);
                    }
                }
            } catch (Exception e) {
                log.error("Error scanning container [{}] path [{}]: {}", 
                    target.getContainerName(), target.getDirectoryPath(), e.getMessage(), e);
            }
        }
    }
}
