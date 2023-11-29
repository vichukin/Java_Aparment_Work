package com.example.java17_06_11.repositories;

import com.example.java17_06_11.models.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image,Long> {
}
