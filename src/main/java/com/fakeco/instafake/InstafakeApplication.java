package com.fakeco.instafake;

import com.fakeco.instafake.utils.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class InstafakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstafakeApplication.class, args);
	}

}
