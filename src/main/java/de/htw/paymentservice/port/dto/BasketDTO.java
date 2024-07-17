package de.htw.paymentservice.port.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BasketDTO {
    @NotNull
    private List<ItemDTO> items;
    @NotNull
    @JsonProperty("totalPrice")
    private BigDecimal totalAmount;
}
