package com.springboot.views.repository;

import com.springboot.views.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewsRepository extends JpaRepository<Views, Long> {
}
