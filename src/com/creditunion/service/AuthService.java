package com.creditunion.service;

import com.creditunion.dao.MemberDAO;
import com.creditunion.dao.StaffDAO;
import com.creditunion.model.Member;
import com.creditunion.model.Staff;

import java.sql.SQLException;

public class AuthService {
    private MemberDAO memberDAO;
    private StaffDAO staffDAO;

    public AuthService() {
        this.memberDAO = new MemberDAO();
        this.staffDAO = new StaffDAO();
    }

    public Object login(String username, String password, String userType) throws SQLException {
        if ("member".equals(userType)) {
            boolean isValid = memberDAO.validateMemberCredentials(username, password);
            if (isValid) {
                return memberDAO.getMemberByUsername(username);
            }
        } else if ("staff".equals(userType)) {
            boolean isValid = staffDAO.validateStaffCredentials(username, password);
            if (isValid) {
                return staffDAO.getStaffByUsername(username);
            }
        }
        return null;
    }
}