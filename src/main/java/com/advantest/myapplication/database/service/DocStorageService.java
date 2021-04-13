package com.advantest.myapplication.database.service;

import com.advantest.myapplication.database.model.Doc;
import com.advantest.myapplication.database.repository.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * DocStorageService.class saves the data to the database,
 * It is also is used to fetch the data from the database.
 *
 * @author darshan.rudresh
 */
@Service
public class DocStorageService {
    @Autowired
    public DocRepository docRepository;


    public Doc saveFile(String key) {

        try {

            Doc doc = new Doc(key);
            return docRepository.save(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Doc> getAllStudent() {
        List<Doc> keyname = new ArrayList<Doc>();
        docRepository.findAll().forEach(kname -> keyname.add(kname));
        return keyname;
    }
}

