public interface ScanTargetRepository extends JpaRepository<ScanTarget, Long> {
    List<ScanTarget> findByIsActiveTrue();
}
