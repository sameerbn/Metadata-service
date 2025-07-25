public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    boolean existsByFilePathAndLastModified(String filePath, Timestamp lastModified);
}
