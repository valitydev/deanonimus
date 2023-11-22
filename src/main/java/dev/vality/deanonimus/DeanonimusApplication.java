package dev.vality.deanonimus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class DeanonimusApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeanonimusApplication.class, args);
    }

}
