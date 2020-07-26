package com.client;

import com.dao.h2.ToyDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);

    public static void main(String[] args){
        ToyDatabase database = new ToyDatabase();
        List<String> list= database.showWithId("dhyg");
        log.info("List: " + list);
    }
}
