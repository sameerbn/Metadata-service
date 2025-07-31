import com.azure.storage.file.datalake.*;
import com.azure.storage.file.datalake.models.PathItem;
import com.example.model.FileMetadata;
import com.example.model.ScanTarget;
import com.example.repository.FileMetadataRepository;
import com.example.repository.ScanTargetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdlsScannerService {

    private static final Logger logger = LoggerFactory.getLogger(AdlsScannerService.class);

    private final DataLakeServiceClient dataLakeServiceClient;
    private final FileMetadataRepository metadataRepository;
    private final ScanTargetRepository scanTargetRepository;

    private static final int DAYS_TO_KEEP = 3;

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void scan() {
        logger.info("▶ ADLS scan started...");

        List<ScanTarget> targets = scanTargetRepository.findByIsActiveTrue();
        LocalDate cutoffDate = LocalDate.now().minusDays(DAYS_TO_KEEP);

        for (ScanTarget target : targets) {
            try {
                DataLakeFileSystemClient fsClient = dataLakeServiceClient.getFileSystemClient(target.getContainerName());
                DataLakeDirectoryClient dirClient = fsClient.getDirectoryClient(target.getDirectoryPath());

                for (PathItem item : dirClient.listPaths(true)) {
                    if (Boolean.TRUE.equals(item.isDirectory())) continue;

                    String fullPath = item.getName();
                    Optional<LocalDate> extractedDate = DateExtractor.extractFromPath(fullPath);

                    if (!extractedDate.isPresent() || extractedDate.get().isBefore(cutoffDate)) {
                        continue;
                    }

                    Timestamp lastModified = Timestamp.from(item.getLastModified().toInstant());

                    if (!metadataRepository.existsByFilePathAndLastModified(fullPath, lastModified)) {
                        FileMetadata meta = new FileMetadata();
                        meta.setFilePath(fullPath);
                        meta.setFileSize(item.getContentLength());
                        meta.setLastModified(lastModified);
                        metadataRepository.save(meta);
                        logger.info("✅ New file added: {}", fullPath);
                    }
                }

            } catch (Exception e) {
                logger.error("❌ Error scanning container [{}] path [{}]: {}", target.getContainerName(), target.getDirectoryPath(), e.getMessage(), e);
            }
        }
        logger.info("✅ ADLS scan completed.");
    }
}
