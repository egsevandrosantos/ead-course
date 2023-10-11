package com.ead.course.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceResponse {
    @Builder.Default
    private boolean ok = true;

    @Builder.Default
    private boolean found = true;
    
    @Builder.Default
    private Map<String, List<String>> errors = new HashMap<>();

    @Builder.Default
    private UUID id = null;
}
