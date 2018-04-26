package com.school.teachermanage.service;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.*;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.repository.AddressReposity;
import com.school.teachermanage.repository.DistrictReposity;
import com.school.teachermanage.repository.SchoolReposity;
import com.school.teachermanage.util.CommonUtil;
import com.school.teachermanage.util.QueryUtil;
import com.school.teachermanage.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yaoyang
 * @Description:
 * @date 2018/4/25
 */
@Service
public class SchoolService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DistrictReposity districtReposity;
    @Resource
    private AddressReposity addressReposity;
    @Resource
    private SchoolReposity schoolReposity;

    public DataResult  createSchool(JSONObject json,DataResult result){

        logger.info(json.toString());
        Long districtId = json.getLong("districtId");
        String name = json.getString("name");
        if(StringUtil.isNullOrEmpty(name)){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.ILLEGAL_PARAMETER);
            return result;
        }
        District district = districtReposity.findOne(districtId);
        City city = district.getCity();
        Province province = city.getProvince();
        Address address = new Address();
        address.setDistrict(district);
        address.setCity(city);
        address.setProvince(province);
        addressReposity.save(address);
        School school = new School();
        school.setAddress(address);
        school.setName(name);
        schoolReposity.save(school);
        result.setMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    public DataResult getAllSchool(int page,int size,DataResult result){
        PageRequest pageRequest = QueryUtil.buildPageRequest(page,size,"id",true);
        Page<School> schoolPage = schoolReposity.findSchools(pageRequest);
        Iterable<School> schoolList = schoolPage.getContent();
        JSONArray array = generateSchoolJsonArray(schoolList);
        JSONObject data = result.getData();
        data.put("schools",array);
        data.put("page", CommonUtil.generatePageJSON(schoolPage));
        result.setSuccess(true);
        result.setMsg(MsgConstants.QUERY_SUCCESS);
        return result;
    }

    private JSONArray generateSchoolJsonArray(Iterable<School> schoolList){
        JSONArray array = new JSONArray();
        for(School school : schoolList){
            JSONObject json = new JSONObject();
            json.put("id",school.getId());
            json.put("name",school.getName());
            array.add(json);
        }
        return array;
    }
}
