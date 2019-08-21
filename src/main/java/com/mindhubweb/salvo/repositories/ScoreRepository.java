package com.mindhubweb.salvo.repositories;

import java.util.List;

import com.mindhubweb.salvo.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ScoreRepository extends JpaRepository<Score, Long> {
}

