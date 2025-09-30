package org.example.mediaconnect.repository;

import org.example.mediaconnect.model.Helloworld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloworldRepository extends JpaRepository<Helloworld,Long> {
}
