package com.advantest.myapplication.database.repository;

import com.advantest.myapplication.database.model.Doc;
import org.springframework.data.repository.CrudRepository;

/**
 * DocRepository.class is a interface for Database.
 *
 * @author darshan.rudresh
 */
public interface DocRepository extends CrudRepository<Doc, String> {

}
