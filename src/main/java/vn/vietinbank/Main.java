package vn.vietinbank;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import vn.vietinbank.io.ReadExcel;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileName = "export2.xls";
        Resource resource = loadTransactionWithClassPathResource(fileName);
        String [] listColumnDisplay = new String []{
                "tranResponseData.JOURNAL_ENTRY_ID","tranName","overrideData.TRANS_CODE",
                "tranResponseData.HOST_REF_NUM","overrideData.BENICIARY_ACCT_NBR","overrideData.BENICIARY_BANK_NAME",
                "overrideData.TO_BANK_NBR","overrideData.ACCT_NBR_FROM","overrideData.FROM_ACCT_TYPE","overrideData.TOTAL_VALUE","overrideData.TRANS_AMT"
        };
        ReadExcel.readExcel(resource.getFile().getPath(),listColumnDisplay);

    }
    public static Resource loadTransactionWithClassPathResource(String fileName) {
        return new ClassPathResource(fileName);
    }
}
