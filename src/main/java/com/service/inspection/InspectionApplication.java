package com.service.inspection;

import com.service.inspection.utils.VaultConfigReaderUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

@SpringBootApplication
public class InspectionApplication {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.putAll(VaultConfigReaderUtil.read());

        SpringApplicationBuilder springApplication = new SpringApplicationBuilder(InspectionApplication.class);
        springApplication.properties(properties).run(args);
    }

}
