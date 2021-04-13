package com.advantest.myapplication;

import com.advantest.myapplication.file.TestFile;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
class MyapplicationApplicationTests {


    void contextLoads() {

    }

    @Test
    public void myTest() throws IOException {
        TestFile testFile = new TestFile();
        testFile.fileCreator();
    }

}
