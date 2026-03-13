package OtterStudio.backend_api.domain.compatibility.repository;

import OtterStudio.backend_api.domain.compatibility.entity.CompatibilityTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompatibilityTestRepository extends JpaRepository<CompatibilityTest, UUID> {
}
