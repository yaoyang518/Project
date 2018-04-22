package com.school.teachermanage.bean;

import com.school.teachermanage.enumeration.ErrorCode;
import net.sf.json.JSONObject;

/**
 * 接口返回对象
 *
 * @author zhangsl
 * @date 2017-11-03
 */
public class DataResult {

    private boolean success;
    private String msg;
    private JSONObject data = new JSONObject();

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void setSuccessMsg(String msg){
        this.setSuccess(true);
        this.setMsg(msg);
        this.data.put("code", ErrorCode.SUCCESS.getCode());
        this.data.put("msg", ErrorCode.SUCCESS.getName());
    }
    public void setDataMsg(ErrorCode error) {
        this.data.put("code", error.getCode());
        this.data.put("msg", error.getName());
    }
}
