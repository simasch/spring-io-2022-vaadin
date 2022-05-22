package ch.martinelli.vaadin.demo.data.service;

import ch.martinelli.vaadin.demo.data.entity.SamplePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

}
