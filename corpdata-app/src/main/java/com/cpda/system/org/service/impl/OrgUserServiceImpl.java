package com.cpda.system.org.service.impl;

import com.cpda.common.base.AbstractBaseService;
import com.cpda.common.result.Result;
import com.cpda.common.result.util.ResultUtil;
import com.cpda.common.utils.PageConvertUtil;
import com.cpda.system.menu.dao.SysMenuMapper;
import com.cpda.system.org.dao.OrgDeptMapper;
import com.cpda.system.org.dao.OrgRoleMapper;
import com.cpda.system.org.dao.OrgUserMapper;
import com.cpda.system.org.dao.OrgUserRoleMapper;
import com.cpda.system.org.entity.OrgUser;
import com.cpda.system.org.service.OrgUserService;
import com.cpda.system.security.shiro.util.ShiroUserPwdUtil;
import com.cpda.system.security.shiro.util.UserUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 用户服务
 * @author zealon
 * @date 2018年3月1日
 */
@Service("userService")
public class OrgUserServiceImpl extends AbstractBaseService<OrgUser> implements OrgUserService {

	protected final Logger logger = LoggerFactory.getLogger(OrgUserServiceImpl.class);
	
	@Autowired
	private OrgUserMapper orgUserMapper;
	
	@Autowired
	private OrgDeptMapper orgDeptMapper;
	
	@Autowired
	private OrgRoleMapper orgRoleMapper;

	@Autowired
	private OrgUserRoleMapper userRoleMapper;

	@Autowired
	private SysMenuMapper menuMapper;
	
	/**
	 * 获取当前用户所有角色
	 */
	public Set<String> getRolesByUser(String userId) {
		List<String> list =orgRoleMapper.getRolesByUser(userId);
		Set<String> set = new HashSet<String>();
		for(String str : list){
			set.add(str);
		}
		return set;
	}

	/**
	 * 获取当前用户所有权限
	 * @param userId
	 * @return
	 */
	public Set<String> getPermissionsByUser(String userId) {
		List<String> list =menuMapper.getPermissionsByUser(userId);
		Set<String> set = new HashSet<String>();
		for(String str : list){
			set.add(str);
		}
		return set;
	}

	@Override
	public String findByPage(int page,int rows,Long deptId,String keyWord) {
		if(deptId!=null && deptId==0){
			deptId = null;
		}
		PageHelper.startPage(page, rows);
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("deptId", deptId);
		map.put("keyword", keyWord);
		Page<OrgUser> list = (Page<OrgUser>) orgUserMapper.selectAll(map);
		return PageConvertUtil.getGridJson(list);
	}
	
	/***
	 * 根据用户id获取用户信息
	 */
	public OrgUser getUserInfoByUserid(String userId){
		OrgUser user = orgUserMapper.selectByUserId(userId);
		return user;
	}
	/**
	 * 根据id获取下级员工列表
	 */
	public List<OrgUser> selectByIds(String ids){
		List<OrgUser> ouList=orgUserMapper.selectByIds(ids);
		return ouList;
	}
	
	public Result insert(OrgUser record) {
		//查询id是否重复
		OrgUser user=orgUserMapper.selectByUserId(record.getUserid());
		if(user!=null){
			Result result=new Result();
			result.setCode(400);
			result.setMsg("添加失败,用户id重复");
			return result;
		}
		Date date = new Date();
		record.setCreater(UserUtil.getCurrentUserid());
		record.setCreated(date);
		record.setModified(date);
		record.setEnabledState(true);
		String newPwd = ShiroUserPwdUtil.generateEncryptPwd(record.getUserid(), record.getUserPwd());
		record.setUserPwd(newPwd);
		record.setPhoneNumber("");
		/*if(deptids!=null){
			userDeptService.insert(record.getUserid(), deptids);
		}*/
		
		return super.save(record);
	}

	@Override
	public Result deleteByUserid(String userId){
		Result r = ResultUtil.success();
		if(userId.equals("admin") || userId.equals("designer")){
			r = ResultUtil.fail("不能删除系统管理员哦！");
		}else{
			try{
				// 删除用户角色
				userRoleMapper.deleteByUserid(userId);

				// 删除用户
				orgUserMapper.deleteByUserid(userId);
			}catch (Exception ex){
				ex.printStackTrace();
				r = ResultUtil.fail();
			}
		}
		return r;
	}
	
	/**
	 * 修改用户密码
	 * @param id
	 * @param newPassword
	 * @return
	 */
	public Result modPassword(Long id, String newPassword) {
		OrgUser user = findById(id);
		String password = ShiroUserPwdUtil.generateEncryptPwd(user.getUserid(), newPassword);//密码加密
		user.setUserPwd(password);
		return super.update(user);
	}

	public Result update(OrgUser record){
		record.setModified(new Date());
		if(record.getUserPwd()!=null && record.getUserPwd().equals("****")){
			record.setUserPwd(null);
		}else{
			String pwd=record.getUserPwd();
			record.setUserPwd(ShiroUserPwdUtil.generateEncryptPwd(record.getUserid(), pwd));
		}
		return super.update(record);
	}
	/**
	 * 获取下拉json数据
	 * @return
	 */
	public String findByCombox(){
		//String json = CorpdataUtil.getComboxJson(orgUserMapper.selectAllByCombox());
		String json = "";
		return json;
	}
}
