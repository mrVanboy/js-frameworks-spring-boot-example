package com.etnetera.hr.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;

import com.etnetera.hr.data.JavaScriptFramework;

/**
 * Spring data repository interface used for accessing the data in database.
 * 
 * @author Etnetera
 *
 */

public interface JavaScriptFrameworkRepository extends CrudRepository<JavaScriptFramework, Long> {

}
