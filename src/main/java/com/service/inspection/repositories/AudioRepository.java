package com.service.inspection.repositories;

import com.service.inspection.entities.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Integer> {

}
