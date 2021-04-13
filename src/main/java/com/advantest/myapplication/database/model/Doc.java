package com.advantest.myapplication.database.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This is a POJO class referring the Database.
 *
 * @author darshan.rudresh
 */
@Entity
public class Doc {

    @Id
    private String keys;


    public Doc() {
    }

    public Doc(String keys) {
        this.keys = keys;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String key) {
        this.keys = key;
    }

}
