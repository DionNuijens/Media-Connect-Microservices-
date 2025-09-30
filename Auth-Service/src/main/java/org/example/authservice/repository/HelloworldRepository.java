package org.example.authservice.repository;

import org.example.authservice.model.Helloworld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloworldRepository extends JpaRepository<Helloworld,Long> {
}
