package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.City;
import com.school.teachermanage.entity.Province;
import com.school.teachermanage.repository.CityReposity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *城市服务类
 *
 * @author wudc
 * @Date 2017/11/16.
 */
@Service
public class CityService {
    @Resource
    private CityReposity cityReposity;

    public City save(City city){return cityReposity.save(city);}

    public DataResult findCitiesByProvinceId(Long provinceId, DataResult result){
        Iterable<City> cityList = cityReposity.findCitiesByProvinceId(provinceId);
        JSONArray array = generateCityJsonArray(cityList);
        JSONObject data = result.getData();
        data.put("cities",array);
        result.setMsg(MsgConstants.QUERY_SUCCESS);
        result.setSuccess(true);
        return result;
    }

    private JSONArray generateCityJsonArray(Iterable<City> cityList){
        JSONArray array = new JSONArray();
        for(City city : cityList){
            JSONObject json = new JSONObject();
            json.put("id",city.getId());
            json.put("name",city.getName());
            array.add(json);
        }
        return array;
    }
}
