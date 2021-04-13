package com.advantest.myapplication.testcontroller;

import com.advantest.myapplication.service.FileServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class TestUpload {

    @Autowired
    private MockMvc mockMvc;


    @Mock
    private FileServices fileServices;


//    @Test
    @DisplayName("test to upload single file")
    void shouldUploadSingleFile() throws Exception {
        /*FileInfo fileInfo = new FileInfo();
        fileInfo.setResult(true);
        Instant instant = Instant.now();
        long epoch=instant.getEpochSecond();
        fileInfo.setCreated(epoch);
        fileInfo.setTarget("hnh");
        fileInfo.setName("tanb.txt");
        fileInfo.setBuildId("245675710");*/

        MockMultipartFile mmf = new MockMultipartFile("file", "test-file.txt",
                "text/plain", "Advantest Gmbh is in Germany".getBytes());

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(mmf).param("FileInfo", "{\"result\":true,\"buildId\":\"4245675710\",\"name\":\"tanb.txt\",\"created\":12102002,\"target\":\"hnh\"}\n"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json("{\"result\":true,\"buildId\":\"4245675710\",\"name\":\"tanb.txt\",\"created\":12102002,\"target\":\"hnh\"}"));


        /*doThrow(new IOException())
                .when(fileServices).uploadFile(mmf, fileInfo);*/
    }


}
