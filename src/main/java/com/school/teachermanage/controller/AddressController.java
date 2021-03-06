package com.school.teachermanage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.entity.City;
import com.school.teachermanage.entity.District;
import com.school.teachermanage.entity.Province;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.service.CityService;
import com.school.teachermanage.service.DistrictService;
import com.school.teachermanage.service.ProvinceService;
import com.school.teachermanage.util.JsonUtil;
import com.school.teachermanage.util.PinYinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author yaoyang
 * @Description:
 * @date 2018/4/24
 */
@RestController
@RequestMapping("/addressApi")
@Api(description = "地址接口")
public class AddressController {

    @Resource
    private ProvinceService provinceService;
    @Resource
    private CityService cityService;
    @Resource
    private DistrictService districtService;

    @PostMapping("/init")
    @ApiOperation(value = "初始化省市区-后台")
    public DataResult save(@RequestHeader String token) throws Exception {

        DataResult result = new DataResult();
        long count = provinceService.getCount();
        if(count>0){
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.UNKNOW_ERROR);
            return result;
        }
        String context = JsonUtil.readJsonFile(ResourceUtils.getFile("classpath:province.json").getAbsolutePath());
        JSONArray provinces = JSON.parseArray(context);
        for (Object province : provinces) {
            JSONObject provinceJson = (JSONObject) province;
            String provinceName = (String) provinceJson.get("name");
            //保存省份
            Province provinceEntity = new Province();
            provinceEntity.setName(provinceName);
            provinceEntity.setLetter(PinYinUtil.getFirstLetter(provinceName));
            provinceService.save(provinceEntity);
            JSONArray cities = provinceJson.getJSONArray("city");
            for (Object city : cities) {
                JSONObject cityJson = (JSONObject) city;
                String cityName = (String) cityJson.get("name");
                City cityEntity = new City();
                cityEntity.setProvince(provinceEntity);
                cityEntity.setName(cityName);
                cityEntity.setLetter(PinYinUtil.getFirstLetter(cityName));
                cityService.save(cityEntity);
                JSONArray districtes = cityJson.getJSONArray("area");
                for (Object district : districtes) {
                    String districtName = (String) district;
                    District districtEntity = new District();
                    districtEntity.setCity(cityEntity);
                    districtEntity.setName(districtName);
                    districtEntity.setLetter(PinYinUtil.getFirstLetter(districtName));
                    districtService.save(districtEntity);
                }
            }
        }
        result.setSuccessMsg(MsgConstants.OPT_SUCCESS);
        return result;
    }

    @GetMapping("/listProvince")
    @ApiOperation(value = "获取所有省份")
    @ApiImplicitParams(@ApiImplicitParam(name="token", value="请求token", required = true, paramType = "header", dataType = "String"))
    public DataResult listProvince(@RequestHeader String token){
        DataResult result = new DataResult();
        return provinceService.findAllProvinces(result);
    }

    @GetMapping("/listCity")
    @ApiOperation(value = "根据省Id查城市")
    @ApiImplicitParams({@ApiImplicitParam(name="token", value="请求token", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name="provinceId", value = "省Id", required = true, paramType = "query", dataType = "Long")})
    public DataResult listCity(@RequestHeader String token, @RequestParam Long provinceId){
        DataResult result = new DataResult();
        return cityService.findCitiesByProvinceId(provinceId, result);
    }

    @GetMapping("/listDistrict")
    @ApiOperation(value = "根据城市Id查县区")
    @ApiImplicitParams({@ApiImplicitParam(name="token",value = "请求token",required = true,paramType = "header",dataType = "String"),
            @ApiImplicitParam(name="cityId",value = "市Id",required = true,paramType = "query",dataType = "Long")})
    public DataResult listDistrict(@RequestHeader String token, @RequestParam Long cityId){
        DataResult result = new DataResult();
        return districtService.findDistrictsByCityId(cityId,result);
    }

}
