package com.ead.course.clients;

import com.ead.course.dtos.UserDTO;
import com.ead.course.enums.UserStatus;
import com.ead.course.exceptions.UserBlockedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class AuthUserClient {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment env;
    @Value("${ead.api.url.authuser}")
    private String authUserURI;
    @Value("${ead.api.url.course}")
    private String courseURI;

    public Page<UserDTO> findAllUsers(Pageable pageable, UUID courseId) {
        List<String> queryParams = new ArrayList<>();
        if (courseId != null) {
            queryParams.add("courseId=" + courseId);
        }
        queryParams.add("page=" + pageable.getPageNumber());
        queryParams.add("size=" + pageable.getPageSize());
        queryParams.add("sort=" + pageable.getSort().toString().replaceAll(": ", ","));
        String requestUrl = authUserURI + "/users?" + String.join("&", queryParams);

        log.info("Request URL: {}", requestUrl);
        try {
            ResponseEntity<String> responseEntity = restTemplate
                .exchange(requestUrl, HttpMethod.GET, null, String.class);
            JSONObject json = new JSONObject(responseEntity.getBody());
            TypeReference<List<UserDTO>> users = new TypeReference<>() {};
            return new PageImpl<>(
                objectMapper.readValue(json.get("content").toString(), users),
                PageRequest.of(Integer.parseInt(json.get("number").toString()), Integer.parseInt(json.get("size").toString())),
                objectMapper.readValue(json.get("totalElements").toString(), Long.class)
            );
        } catch (Exception ex) {
            log.error("Error in request to URL: {}", requestUrl, ex);
        } finally {
            log.info("Completed request to URL: {}", requestUrl);
        }
        return null;
    }

    public UserDTO findUserById(UUID userId) throws UserBlockedException, HttpStatusCodeException {
        String requestUrl = authUserURI + "/users/" + userId;
        log.info("Request URL: {}", requestUrl);
        try {
            ResponseEntity<UserDTO> responseEntity = restTemplate
                .exchange(requestUrl, HttpMethod.GET, null, UserDTO.class);
            UserDTO userDTO = null;
            if (responseEntity.getStatusCode() == HttpStatus.OK && (userDTO = responseEntity.getBody()) != null) {
                if (userDTO.getStatus() == UserStatus.BLOCKED) {
                    throw new UserBlockedException();
                }
            }
            return userDTO;
        } catch (UserBlockedException ex) {
            log.error("Error in request to URL: {}", requestUrl, ex);
            throw ex;
        } catch (HttpStatusCodeException ex) {
            log.error("Error in request to URL: {}", requestUrl, ex);
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw ex;
            }
        } finally {
            log.info("Completed request to URL: {}", requestUrl);
        }
        return null;
    }

    public void createUserCourseRelationship(UUID courseId, UUID userId) {
        String requestUrl = authUserURI + "/users/" + userId + "/courses";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("courseId", courseId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Requested-By", Base64.getEncoder().encodeToString(courseURI.getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObject.toString(), headers);

        log.info("Request URL: {}", requestUrl);
        try {
            restTemplate
                .exchange(requestUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Error in request to URL: {}", requestUrl, ex);
            throw ex;
        } finally {
            log.info("Completed request to URL: {}", requestUrl);
        }
    }
}
