package kuit.project.beering.configuration;

import feign.form.FormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;

@Configuration
@EnableFeignClients
public class OpenFeignConfig {

    @Bean
    public FormEncoder feignEncoder() {
        return new FormEncoder(new SpringEncoder(() -> new HttpMessageConverters(new FormHttpMessageConverter())));
    }
}
