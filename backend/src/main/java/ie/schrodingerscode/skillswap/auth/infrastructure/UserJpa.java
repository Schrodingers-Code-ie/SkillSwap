package ie.schrodingerscode.skillswap.auth.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// This is a Spring Data tool, it talks to Postgres using UserEntity
// Basically a list of database operations we want. Spring will build the code behind them
public interface UserJpa extends JpaRepository<UserEntity, Long> {
    // With JpaRepository we automatically get save, findById, findAll, delete and other methods from JpaRepository

    boolean existsByEmail(String email);

    boolean existsByUsernameIgnoreCase(String username); // Called by JpaUserRepository to reach into the database to check if we have a user with a passed username

    Optional<UserEntity> findByEmail(String email);
}
