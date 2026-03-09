package com.auth.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestDto {
    private String uri;    // Ej. "/suarios/123"
    private String method; // Ej. "GET", "POST"
}
