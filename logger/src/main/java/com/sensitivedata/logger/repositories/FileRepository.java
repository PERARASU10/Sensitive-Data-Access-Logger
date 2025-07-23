package com.sensitivedata.logger.repositories;

import com.sensitivedata.logger.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * Finds all distinct files for which a given user has at least one permission.
     * The DISTINCT keyword prevents duplicate files from being returned.
     * @param username The username of the user.
     * @return A list of distinct files the user has permissions for.
     */
    @Query("SELECT DISTINCT p.file FROM Permission p WHERE p.user.username = :username")
    List<File> findFilesByUsernameWithPermission(@Param("username") String username);
}
