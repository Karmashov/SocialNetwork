package com.skillbox.socialnetwork.main.repo;

import com.skillbox.socialnetwork.main.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);
}