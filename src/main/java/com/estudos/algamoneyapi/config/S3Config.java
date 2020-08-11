package com.estudos.algamoneyapi.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.estudos.algamoneyapi.config.property.AlgamoneyApiProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Autowired
    private AlgamoneyApiProperty property;

    @Bean
    public AmazonS3 amazonS3(){
        AWSCredentials credentials = new BasicAWSCredentials(property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());

        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }
}
