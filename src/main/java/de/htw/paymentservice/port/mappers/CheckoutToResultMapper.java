package de.htw.paymentservice.port.mappers;

import de.htw.paymentservice.port.dto.ResultDTO;
import org.springframework.stereotype.Service;

@Service
public class CheckoutToResultMapper {
    public ResultDTO getResultDTO(String username, String status) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setUsername(username);
        resultDTO.setStatus(status);
        return resultDTO;
    }
}
