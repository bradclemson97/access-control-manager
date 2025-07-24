package com.example.accesscontrolmanager.controller.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class UserRoleListRequest {

    private List<UserRoleRequest> userRoleRequestList;
}
