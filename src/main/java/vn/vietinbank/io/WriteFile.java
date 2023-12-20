package vn.vietinbank.io;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;

public class WriteFile {
    public static void writeToTextFile(String data)
            throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File("data_export.txt"));
            os.write(data.getBytes(), 0, data.length());
        } finally {
            try {
                assert os != null;
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
