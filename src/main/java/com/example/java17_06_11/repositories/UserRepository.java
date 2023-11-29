package com.example.java17_06_11.repositories;

import com.example.java17_06_11.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {
    User findByUsername(String username);
}
