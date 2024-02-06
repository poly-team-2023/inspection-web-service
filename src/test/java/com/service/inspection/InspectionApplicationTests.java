package com.service.inspection;

import com.service.inspection.service.AbstractTestContainerStartUp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class InspectionApplicationTests extends AbstractTestContainerStartUp {

    @Test
    void contextLoads() {
    }

}
