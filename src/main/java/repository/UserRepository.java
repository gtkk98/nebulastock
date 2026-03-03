package repository;

import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Marks this as a Spring-managed data access component
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA MAGIC: just by naming the method correctly,
    // it automatically generates: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // Similarly, this generates: SELECT COUNT(*) > 0 FROM users WHERE username = ?
    boolean existsByUsername(String username);

}
