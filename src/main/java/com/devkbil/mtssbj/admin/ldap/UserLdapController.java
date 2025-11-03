package com.devkbil.mtssbj.admin.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ldap")
public class UserLdapController {
    @Autowired
    private UserLdapService userLdapService;

    @PostMapping("/add")
    public String addUserToAD(@RequestBody UserDto userDto) {
        userLdapService.addUserToAD(userDto);
        return "사용자가 AD에 추가되었습니다.";
    }
}
