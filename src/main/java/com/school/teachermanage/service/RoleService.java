package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Permission;
import com.school.teachermanage.entity.Role;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.PermissionRepository;
import com.school.teachermanage.repository.RoleRepository;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.DateUtil;
import com.school.teachermanage.util.QueryUtil;
import com.school.teachermanage.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 角色数据
 *
 * @author wudc
 * @date 2017-11-06
 */
@Service
public class RoleService {
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private PermissionRepository permissionRepository;

    public DataResult findById(Long id) {
        DataResult result = new DataResult();
        JSONObject data = result.getData();
        Role role = roleRepository.findOne(id);
        if (role == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ROLE_NOT_EXIST);
            return result;
        }
        JSONObject json = new JSONObject();
        json.put("id", role.getId());
        json.put("role", role.getRole());
        json.put("description", role.getDescription());
        json.put("role", role.getRole());
        JSONArray array = new JSONArray();
        List<Permission> rolePermissions = role.getPermissions();
        Iterator<Permission> iterator = permissionRepository.findAll().iterator();
        while (iterator.hasNext()) {
            Permission permission = iterator.next();
            JSONObject permissionJSON = new JSONObject();
            permissionJSON.put("id", permission.getId());
            permissionJSON.put("name", permission.getName());
            permissionJSON.put("checked", rolePermissions.contains(permission));
            array.add(permissionJSON);
        }
        data.put("role", json);
        data.put("permissions", array);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult findByUserId(DataResult result, Long adminId) {
        JSONObject data = result.getData();
        List<Role> roles = roleRepository.findByAdminId(adminId);
        JSONArray array = new JSONArray();
        if (roles != null && roles.size() > 0) {
            for (Role role : roles) {
                JSONObject json = new JSONObject();
                json.put("id", role.getId());
                json.put("description", role.getDescription());
                json.put("role", role.getRole());
                array.add(json);
            }
            data.put("roles", array);
        }
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }


    public DataResult edit(Role edit) {
        DataResult result = new DataResult();
        Role role = roleRepository.findOne(edit.getId());
        try {
            if (role == null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.ROLE_NOT_EXIST);
                return result;
            }
            if (StringUtil.isNotEmpty(edit.getRole())) {
                Role dbRole = roleRepository.findByRole(edit.getRole());
                if (dbRole != null && !dbRole.getRole().equalsIgnoreCase(role.getRole())) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.ROLE_NAME_EXIST);
                    return result;
                }
                role.setRole(edit.getRole());
            }
            if (StringUtil.isNotEmpty(edit.getDescription())) {
                role.setDescription(edit.getDescription());
            }
            role.setPermissions(null);
            roleRepository.save(role);
            List<Permission> permissions = edit.getPermissions();
            if(permissions!=null && permissions.size()>0){
                role.setPermissions(permissions);
            }
            role.setUpdateDate(new Date());
            roleRepository.save(role);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NOT_EXIST);
            return result;
        }
        JSONObject data = result.getData();
        data.put("id", role.getId());
        data.put("role", role.getRole());
        data.put("description", role.getDescription());
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult list(DataResult result, int page, int size) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<Role> rolePage = roleRepository.list(pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(rolePage));
        List<Role> roles = rolePage.getContent();
        JSONArray array = new JSONArray();
        if (roles != null && roles.size() > 0) {
            for (Role role : roles) {
                JSONObject json = new JSONObject();
                JSONObject permissionsJson = new JSONObject();
                json.put("id", role.getId());
                json.put("role", role.getRole());
                json.put("description", role.getDescription());
                json.put("available", role.getAvailable());
                json.put("createDate", DateUtil.dateToString(role.getCreateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
                json.put("permissionsJson", permissionsJson);
                array.add(json);
            }
        }
        data.put("roles", array);
        return result;
    }

    public DataResult save(Role role, DataResult result) {
        try {
            if (role == null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.ROLE_NOT_EXIST);
                return result;
            }
            if (StringUtil.isNotEmpty(role.getRole())) {
                if (roleRepository.findByRole(role.getRole()) != null) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.ROLE_NAME_EXIST);
                    return result;
                }
                role.setRole(role.getRole());
            }

            if (StringUtil.isNotEmpty(role.getDescription())) {
                role.setDescription(role.getDescription());
            }
            role.setCreateDate(new Date());
            role.setUpdateDate(new Date());
            roleRepository.save(role);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NOT_EXIST);
            return result;
        }
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult roleIsavailableOpt(Long id, Boolean isavailable, DataResult result) {
        Role role = roleRepository.findOne(id);

        if (role == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ROLE_NOT_EXIST);
            return result;
        }
        if (isavailable) {
            role.setAvailable(true);
        } else {
            role.setAvailable(false);
        }

        roleRepository.save(role);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }
}
