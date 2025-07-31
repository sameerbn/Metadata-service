import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "scan_targets")
@Data
public class ScanTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String containerName;
    private String directoryPath;
    private Boolean isActive;
}
