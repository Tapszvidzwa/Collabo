package com.example.tapiwa.collegebuddy.classContents.DOCS;

/**
 * Created by tapiwa on 11/4/17.
 */

public class DOC {

    private String doc_name;
    private String doc_date_created;
    private String doc_uri;
    private String doc_type;
    private String doc_key;


    public DOC() {
        //required empty constructor
    }

    public DOC(String doc_name, String doc_date_created, String doc_type, String doc_uri, String doc_key) {
        this.doc_name = doc_name;
        this.doc_date_created = doc_date_created;
        this.doc_type = doc_type;
        this.doc_uri = doc_uri;
        this.doc_key = doc_key;

    }

    public String getDoc_name() {
        return doc_name;
    }

    public String getDoc_date_created() {
        return doc_date_created;
    }

    public String getDoc_uri() {
        return doc_uri;
    }

    public String getDoc_key() { return doc_key;}
    public String getDoc_type() {
        return doc_type;
    }

}
