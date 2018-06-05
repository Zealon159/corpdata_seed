package com.corpdata.system.dic.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.corpdata.system.dic.entity.SysDicType;
import com.corpdata.system.dic.service.SysDicTypeService;
import com.corpdata.common.result.Result;
import com.corpdata.common.utils.CorpdataUtil;

@Controller
@RequestMapping("system/data-dic-type")
public class SysDicTypeController {
	
	@Autowired
	private SysDicTypeService sysDicTypeService;
	

	@RequestMapping("/add")
	public String add(ModelMap map){
		map.put("id", CorpdataUtil.getUUID());
		return "system/org/role/add";
	}
	
	@ResponseBody
	@RequestMapping("/save")
	public Result save(SysDicType record){
		return sysDicTypeService.insert(record);
	}

	@RequestMapping("/edit")
	public String edit(ModelMap map){
		//map.put("id", projectAlbumService.selectByPrimaryKey(id));
		return "system/org/role/add";
	}
	
	@ResponseBody
	@RequestMapping("/update")
	public Result update(SysDicType record){
		return sysDicTypeService.update(record);
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public Result delete(String id){
		return sysDicTypeService.delete(id);
	}
	
	@ResponseBody
	@RequestMapping("/find/{pageNo}/{pageSize}")
	public String findByPage(@PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize){
		
		String jsonData = sysDicTypeService.findByPage(pageNo, pageSize);
		return jsonData;	
	}
	
	@ResponseBody
	@RequestMapping("/selectjson")
	public String getSelectJsonData(){
		return sysDicTypeService.findByCombox();
	}
	
	@ResponseBody
	@RequestMapping("/getSysDicTypeNavJson")
	public Map<String,Object> getSysDicTypeNavJson(){
		return sysDicTypeService.getSysDicTypeNav();
	}
}
