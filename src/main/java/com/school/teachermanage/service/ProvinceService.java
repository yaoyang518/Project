package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.Province;
import com.school.teachermanage.repository.ProvinceReposity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 省份服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class ProvinceService {
    @Resource
    private ProvinceReposity provinceReposity;

    public Province save(Province province){ return provinceReposity.save(province); }

    public long getCount(){ return provinceReposity.count();}

    public DataResult findAllProvinces(DataResult result){
        Iterable<Province> provinceList = provinceReposity.findAll();
        JSONArray array = generateProvinceJsonArray(provinceList);
        JSONObject data = result.getData();
        data.put("provinces",array);
        result.setMsg(MsgConstants.QUERY_SUCCESS);
        result.setSuccess(true);
        return result;
    }

    private JSONArray generateProvinceJsonArray(Iterable<Province> provinceList){
        JSONArray array = new JSONArray();
        for(Province province : provinceList){
            JSONObject json = new JSONObject();
            json.put("id",province.getId());
            json.put("name",province.getName());
            array.add(json);
        }
        return array;
    }
}
