package com.example.java17_06_11.repositories;

import com.example.java17_06_11.models.Apartment;
import org.springframework.data.repository.CrudRepository;

public interface ApartmentRepository extends CrudRepository<Apartment,Long> {
}
