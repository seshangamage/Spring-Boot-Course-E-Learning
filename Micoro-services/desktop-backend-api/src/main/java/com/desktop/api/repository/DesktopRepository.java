package com.desktop.api.repository;

import com.desktop.api.entity.Desktop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesktopRepository extends JpaRepository<Desktop, Long> {
}
