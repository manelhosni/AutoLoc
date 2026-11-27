package tn.esprit.autoloc.autolocapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {
    private int statut;
    private int status;
    private String message;
    private LocalDateTime horodatage;
    private LocalDateTime timestamp;
    private Map<String, String> erreurs;
    private Map<String, String> errors;
}
