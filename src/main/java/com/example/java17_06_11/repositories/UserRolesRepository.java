package com.example.java17_06_11.repositories;

import com.example.java17_06_11.models.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRolesRepository  extends CrudRepository<UserRole,Long> {
}
