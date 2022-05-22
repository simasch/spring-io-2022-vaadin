package ch.martinelli.vaadin.demo.data.service;

import ch.martinelli.vaadin.demo.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}
