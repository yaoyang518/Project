package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Admin;
import com.school.teachermanage.entity.Permission;
import com.school.teachermanage.entity.Role;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.AdminRepository;
import com.school.teachermanage.repository.PermissionRepository;
import com.school.teachermanage.repository.RoleRepository;
import com.school.teachermanage.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 管理员数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Service
public class AdminService {
    @Resource
    private AdminRepository adminRepository;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private PermissionRepository permissionRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;

    public Long getAdminId(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.get("id").toString());
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Admin findById(Long id) {
        return adminRepository.findOne(id);
    }


    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    public DataResult getAdminInfo(Long id, DataResult result) {
        Admin admin = findById(id);
        if (admin == null) {
            result.setMsg(MsgConstants.QUERY_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
        } else {
            result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
            JSONObject data = result.getData();
            data.put("id", admin.getId());
            data.put("name", admin.getName());
            data.put("state", admin.getState());
            data.put("username", admin.getUsername());
            data.put("description", admin.getDescription());
            List<Role> adminRoles = admin.getRoles();
            JSONArray array = new JSONArray();
            Iterator<Role> iterator = roleRepository.findAll().iterator();
            while (iterator.hasNext()) {
                Role role = iterator.next();
                JSONObject roleJSON = new JSONObject();
                roleJSON.put("id", role.getId());
                roleJSON.put("role", role.getRole());
                roleJSON.put("checked", adminRoles.contains(role));
                array.add(roleJSON);
            }
            data.put("roles", array);
        }
        return result;
    }

    public DataResult save(Admin save, DataResult result) {
        if (StringUtil.isEmpty(save.getUsername())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NAME_NULL);
            return result;
        }
        if (StringUtil.isEmpty(save.getPassword())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PASSWORD_NULL);
            return result;
        }
        Admin admin = findByUsername(save.getUsername());
        if (admin != null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_EXIST);
            return result;
        }
        List<Role> roles = save.getRoles();
        if (null != roles) {
            for (Role role : roles) {
                Role dbRole = roleRepository.findOne(role.getId());
                if (dbRole == null) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.ROLE_NOT_EXIST);
                    return result;
                }
            }
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        save.setId(null);
        save.setCreateDate(new Date());
        save.setUpdateDate(new Date());
        save.setSalt(UUIDUtil.createRandomString(32));
        save.setPassword(Md5Util.doubleMD5(save.getPassword() + save.getSalt()));
        save.setState((byte) 1);
        save(save);
        JSONObject data = result.getData();
        generateAdminJson(save, data);
        return result;
    }

    public DataResult update(Admin update, DataResult result) {
        if (StringUtil.isEmpty(update.getId() + "")) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ID_NULL);
            return result;
        }
        if (StringUtil.isEmpty(update.getUsername())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NAME_NULL);
            return result;
        }
        Admin admin = adminRepository.findOne(update.getId());
        if (admin == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }
        Admin adminUsername = adminRepository.findByUsername(update.getUsername());
        if (adminUsername != null && !adminUsername.getId().equals(admin.getId())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USERNAME_EXIST);
            return result;
        }
        admin.setRoles(null);
        adminRepository.save(admin);
        List<Role> roles = update.getRoles();
        if (null != roles) {
            for (Role role : roles) {
                Role dbRole = roleRepository.findOne(role.getId());
                if (dbRole == null) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.ROLE_NOT_EXIST);
                    return result;
                }
            }
        }
        admin.setRoles(roles);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        admin.setUsername(update.getUsername());
        if (StringUtil.isNotEmpty(update.getPassword())) {
            admin.setPassword(Md5Util.doubleMD5(update.getPassword() + admin.getSalt()));
        }
        if (StringUtil.isNotEmpty(update.getName())) {
            admin.setName(update.getName());
        }
        if (StringUtil.isNotEmpty(update.getDescription())) {
            admin.setDescription(update.getDescription());
        }
        admin.setUpdateDate(new Date());
        adminRepository.save(admin);
        JSONObject data = result.getData();
        generateAdminJson(update, data);
        return result;
    }

    private void generateAdminJson(Admin update, JSONObject data) {
        data.put("id", update.getId());
        data.put("name", update.getName());
        data.put("username", update.getUsername());
        data.put("state", update.getState());
        data.put("createDate", DateUtil.dateToString(update.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
        data.put("updateDate", DateUtil.dateToString(update.getUpdateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
    }

    public DataResult getAdminList(String username, int page, int size, DataResult result) {

        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<Admin> adminPage;
        if (StringUtil.isNotEmpty(username)) {
            adminPage = adminRepository.findByUsernameContains(username, pageRequest);
        } else {
            adminPage = adminRepository.findAll(pageRequest);
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(adminPage));
        List<Admin> admins = adminPage.getContent();
        JSONArray array = new JSONArray();
        for (Admin admin : admins) {
            List<Role> list = admin.getRoles();
            StringBuffer roleName = new StringBuffer();
            for (Role role : list) {
                roleName.append(role.getRole() + ";");
            }
            JSONObject json = new JSONObject();
            json.put("id", admin.getId());
            json.put("name", admin.getName());
            json.put("username", admin.getUsername());
            json.put("state", admin.getState());
            if (StringUtil.isNotEmpty(roleName.toString())) {
                json.put("roleName", roleName.substring(0, roleName.length() - 1));
            } else {
                json.put("roleName", "未配置");
            }
            json.put("createDate", DateUtil.dateToString(admin.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("updateDate", DateUtil.dateToString(admin.getUpdateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            array.add(json);
        }
        data.put("admins", array);
        return result;
    }


    public DataResult adminIsavailableOpt(Long id, Boolean isavailable, DataResult result) {
        if (id == 1){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ADMIN_DEFAULT);
            return result;
        }
        Admin admin = adminRepository.findOne(id);
        if (admin == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.USER_NOT_EXIST);
            return result;
        }

        if (isavailable) {
            admin.setState((byte) 1);
        } else {
            admin.setState((byte) 0);
        }
        adminRepository.save(admin);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    /**
     * 校验管理员是否有权限操作
     *
     * @param permission
     * @return 是否拥有权限
     */

    public boolean isAuthorizationPermission(String token, String permission) {
        Long adminId = getAdminId(token);
        if (adminId == 1){
            //超级管理员信息
            return true;
        }
        Permission permissionObject = permissionRepository.findByPermission(permission);
        if (null == permissionObject) {
            return false;
        }
        if (!permissionObject.getAvailable()) {
            return false;
        }
        Admin admin = findById(adminId);
        if (admin.getState() != 1) {
            return false;
        }
        Role adminRole = null;
        List<Role> roles = admin.getRoles();
        for (Role role : roles) {
            List<Permission> permissions = role.getPermissions();
            for (Permission p : permissions) {
                if (p.getId().equals(permissionObject.getId())) {
                    adminRole = role;
                    break;
                }
            }
            if (null != adminRole) {
                break;
            }
        }
        if (null != adminRole) {
            if (!adminRole.getAvailable()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}