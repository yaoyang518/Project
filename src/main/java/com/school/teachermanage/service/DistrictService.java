package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.City;
import com.school.teachermanage.entity.District;
import com.school.teachermanage.repository.DistrictReposity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 地址服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class DistrictService {
    @Resource
    private DistrictReposity districtReposity;

    public District save(District district){ return districtReposity.save(district); }

    public DataResult findDistrictsByCityId(Long cityId,DataResult result){
        List<District> districtList = districtReposity.findDistrictsByProvinceId(cityId);
        JSONArray array = generateDistrictJsonArray(districtList);
        JSONObject data = result.getData();
        data.put("districts",array);
        result.setMsg(MsgConstants.QUERY_SUCCESS);
        result.setSuccess(true);
        return result;
    }

    private JSONArray generateDistrictJsonArray(Iterable<District> districtList){
        JSONArray array = new JSONArray();
        for(District district : districtList){
            JSONObject json = new JSONObject();
            json.put("id",district.getId());
            json.put("name",district.getName());
            array.add(json);
        }
        return array;
    }
}
