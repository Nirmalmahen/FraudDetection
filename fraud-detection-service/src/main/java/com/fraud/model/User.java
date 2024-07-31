package com.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String userId;
    private String email;
    private String phoneNumber;
    private String accountId;
    private String username;
    private String password;
}
