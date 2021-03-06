package com.corpdata.system.generate.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.corpdata.common.domain.DataGridRequestDTO;
import com.corpdata.system.generate.service.GeneratorService;

@Controller
@RequestMapping("/system/generator")
public class GeneratorController{
	
	@Autowired
	private GeneratorService generatorService;

	//生成代码
	@RequestMapping("/genCode")
	public void batchCode(HttpServletRequest request, HttpServletResponse response, String tables,String packageName,String packageName2,String subject,String dbType,String dbEnum) throws IOException {
		String[] tableNames = new String[] {};
		tableNames = tables.split(",");
		byte[] data = generatorService.generatorCode(tableNames,packageName,packageName2,subject,dbType,dbEnum);
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"codes.zip\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream; charset=UTF-8");
		IOUtils.write(data, response.getOutputStream());
	}
	
	//数据库表列表
	@GetMapping(value = "/list")
	public String list() {
		return "system/generate/table_list";
	}
	
	@RequestMapping("/listdata")
	@ResponseBody
	public String getList(DataGridRequestDTO dgRequest){
		return generatorService.findByPage(dgRequest);
	}
	
	//生成选项
	@GetMapping(value = "/option")
	public String tableGenerate(String tables,String dbType,String dbEnum,ModelMap map){
		map.addAttribute("tables", tables);
		map.addAttribute("dbType", dbType);
		map.addAttribute("dbEnum", dbEnum);
		return "system/generate/option";
	}
}
