package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByEmail(String email);
        Optional<User> findByRegistration(Integer registration);
    }

