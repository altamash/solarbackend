package com.solar.api.saas.repository;

import com.solar.api.saas.model.ImageLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageLibraryRepository extends JpaRepository<ImageLibrary, Long> {
}
