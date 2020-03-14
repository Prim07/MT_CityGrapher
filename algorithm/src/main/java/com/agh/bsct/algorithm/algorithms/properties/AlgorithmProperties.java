package com.agh.bsct.algorithm.algorithms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "algorithm-app")
@PropertySource("classpath:algorithm-app.properties")
public class AlgorithmProperties {

    /**
     * If set to true, saving some calculation values
     * to file will be possible
     */
    @NotNull
    private Boolean isWritingValuesToFileEnabled;

    @NotNull
    private Boolean isWritingTemperatureEnabled;

    @NotNull
    private Boolean isWritingDeltaEnabled;

    @NotNull
    private Boolean isWritingLocalFunctionValueEnabled;

    @NotNull
    private Boolean isWritingAcceptedFunctionValueEnabled;

    @NotNull
    private Boolean isWritingBestFunctionValueEnabled;

}
