package org.example.socialservice.repository;

import org.example.socialservice.model.Helloworld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloworldRepository extends JpaRepository<Helloworld,Long> {
}
