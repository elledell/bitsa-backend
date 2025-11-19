package com.website.bitsa.repository;


import com.website.bitsa.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name (e.g., "STUDENT", "ADMIN")
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active roles
     */
    List<Role> findByIsActiveTrue();

    /**
     * Find roles with admin privileges
     */
    List<Role> findByCanManageUsersTrue();
}