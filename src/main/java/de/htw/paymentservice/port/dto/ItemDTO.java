package de.htw.paymentservice.port.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemDTO {
    @NotNull
    @JsonProperty("plantId")
    private UUID plantId;
    @NotNull
    private String name;
    @NotNull
    private BigDecimal itemPrice;
}
