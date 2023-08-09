package kuit.project.beering.configuration;

import feign.codec.Encoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class OpenFeignConfig {

    @Bean
    public Encoder formEncoder() {
        return new feign.form.FormEncoder();
    }
}
