package pro.gamely.license.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.gamely.license.model.LicenseKey;
import java.util.*;
public interface LicenseKeyRepository extends JpaRepository<LicenseKey, Long> {
    Optional<LicenseKey> findByLicenseKey(String licenseKey);
    List<LicenseKey> findByIdIn(List<Long> ids);
}
