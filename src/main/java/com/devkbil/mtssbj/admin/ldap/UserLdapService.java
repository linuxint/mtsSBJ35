package com.devkbil.mtssbj.admin.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import javax.naming.Name;
import org.springframework.ldap.core.DirContextAdapter;

@Service
public class UserLdapService {
    @Autowired(required = false)
    private LdapTemplate ldapTemplate;

    public void addUserToAD(UserDto userDto) {
        if (ldapTemplate == null) {
            // LDAP 서버가 없으면 아무 작업도 하지 않음
            return;
        }
        Name dn = LdapNameBuilder.newInstance()
                .add("OU", "Users")
                .add("CN", userDto.getUsername())
                .build();

        DirContextAdapter context = new DirContextAdapter(dn);
        context.setAttributeValues("objectClass", new String[] {"top", "person", "organizationalPerson", "user"});
        context.setAttributeValue("sAMAccountName", userDto.getUsername());
        context.setAttributeValue("userPassword", userDto.getPassword());
        context.setAttributeValue("displayName", userDto.getDisplayName());
        context.setAttributeValue("userid", userDto.getUserid());
        ldapTemplate.bind(context);
    }
}
