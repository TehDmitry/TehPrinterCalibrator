package ru.tehdmitry.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CommonSingleConfiguration {
    protected int layersPerTest = 10;
    protected int numTests = 6;
}
