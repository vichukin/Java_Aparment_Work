package com.example.java17_06_11.repositories;

 import com.example.java17_06_11.models.Rent;
import org.springframework.data.repository.CrudRepository;

public interface RentRepository extends CrudRepository<Rent,Long> {
}
