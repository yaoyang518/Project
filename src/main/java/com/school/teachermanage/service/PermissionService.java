package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Permission;
import com.school.teachermanage.enumeration.DateStyle;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.PermissionRepository;
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
import java.util.List;

/**
 * 权限数据
 *
 * @author wudc
 * @date 2017-11-06
 */
@Service
public class PermissionService {
    @Resource
    private PermissionRepository permissionRepository;

    public List<Permission> findByRoleId(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    public DataResult findById(Long id) {
        DataResult result = new DataResult();
        JSONObject data = result.getData();
        Permission permission = permissionRepository.findOne(id);
        if (permission == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NOT_EXIST);
            return result;
        }
        JSONObject json = new JSONObject();
        json.put("id", permission.getId());
        json.put("resourceType", permission.getResourceType());
        json.put("url", permission.getUrl());
        json.put("name", permission.getName());
        json.put("permission", permission.getPermission());
        json.put("description", permission.getDescription());
        data.put("permission", json);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    public DataResult edit(Permission edit) {
        DataResult result = new DataResult();
        JSONObject data = result.getData();
        Permission permission = permissionRepository.findOne(edit.getId());
        if (permission == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NOT_EXIST);
            return result;
        }
        if (edit.getParentId() != null) {
            Permission parent = permissionRepository.findOne(edit.getParentId());
            if (parent == null) {
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.PERMISSION_NOT_EXIST);
                return result;
            }
            permission.setParentId(edit.getParentId());
        }
        if (StringUtil.isNotEmpty(edit.getResourceType())) {
            if (!"menu".equals(edit.getResourceType())) {
                if (!"button".equals(edit.getResourceType())) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.PERMISSION_IDENTIFYING);
                    return result;
                }
            }
            permission.setResourceType(edit.getResourceType());
        }

        if (StringUtil.isNotEmpty(edit.getUrl())) {
            permission.setUrl(edit.getUrl());
        }
        if (StringUtil.isNotEmpty(edit.getPermission())) {
            Permission byPermission = permissionRepository.findByPermission(edit.getPermission());

            if(byPermission != null && !byPermission.getId().equals(permission.getId())){
                result.setMsg(MsgConstants.OPT_FAIL);
                result.setDataMsg(ErrorCode.PERMISSION_NAME_EXIST);
                return result;
            }
            permission.setPermission(edit.getPermission());
        }
        if (StringUtil.isNotEmpty(edit.getName())) {
            permission.setName(edit.getName());
        }
        if (StringUtil.isNotEmpty(edit.getDescription())) {
            permission.setDescription(edit.getDescription());
        }
        permission.setUpdateDate(new Date());
        permissionRepository.save(permission);
        JSONObject json = new JSONObject();
        json.put("id", permission.getId());
        json.put("name",permission.getName());
        json.put("resourceType", permission.getResourceType());
        json.put("url", permission.getUrl());
        json.put("permission", permission.getPermission());
        json.put("description", permission.getDescription());
        data.put("permission", json);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }
    public DataResult save(Permission save, DataResult result) {
        if (StringUtil.isEmpty(save.getName())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.NAME_NULL);
            return result;
        }
        if (StringUtil.isEmpty(save.getResourceType())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_IDENTIFYING);
            return result;
        } else {
            if (!"menu".equals(save.getResourceType())) {
                if (!"button".equals(save.getResourceType())) {
                    result.setMsg(MsgConstants.OPT_FAIL);
                    result.setDataMsg(ErrorCode.PERMISSION_IDENTIFYING);
                    return result;
                }
            }
        }
        if (StringUtil.isEmpty(save.getPermission())) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NAME_NULL);
            return result;
        }
        Permission permission = permissionRepository.findByPermission(save.getPermission());
        if (null != permission) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NAME_EXIST);
            return result;
        }
        save.setId(null);
        save.setCreateDate(new Date());
        save.setUpdateDate(new Date());
        permissionRepository.save(save);
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        JSONObject json = new JSONObject();
        json.put("id", save.getId());
        json.put("name",save.getName());
        json.put("resourceType", save.getResourceType());
        json.put("url", save.getUrl());
        json.put("permission", save.getPermission());
        json.put("description", save.getDescription());
        result.getData().put("permission", json);
        return result;
    }

    public DataResult list(DataResult result, int page, int size) {
        PageRequest pageRequest = QueryUtil.buildPageRequest(page, size, "id", true);
        Page<Permission> permissionPage = permissionRepository.findAll(pageRequest);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        JSONObject data = result.getData();
        data.put("page", CommonUtil.generatePageJSON(permissionPage));
        JSONArray array = new JSONArray();
        for (Permission permission : permissionPage.getContent()) {
            JSONObject json = new JSONObject();
            json.put("id", permission.getId());
            json.put("name", permission.getName());
            json.put("permission", permission.getPermission());
            json.put("description", permission.getDescription());
            json.put("resourceType", permission.getResourceType());
            json.put("available", permission.getAvailable());
            json.put("createDate", DateUtil.dateToString(permission.getUpdateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            json.put("updateDate", DateUtil.dateToString(permission.getUpdateDate(), DateStyle.YYYY_MM_DD_HH_MM_SS));
            array.add(json);
        }
        data.put("permissions", array);
        return result;
    }

    public DataResult permissionIsavailableOpt(Long id, Boolean isavailable, DataResult result) {
        Permission permission = permissionRepository.findOne(id);

        if (permission == null) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_NOT_EXIST);
            return result;
        }
        if(isavailable){
            permission.setAvailable(true);
        }else{
            permission.setAvailable(false);
        }

        permissionRepository.save(permission);
        result.setSuccessMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }
}
