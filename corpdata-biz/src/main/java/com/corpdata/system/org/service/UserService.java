package com.corpdata.system.org.service;

import java.util.Set;

import com.corpdata.common.domain.DataGridRequestDTO;
import com.corpdata.common.result.Result;
import com.corpdata.system.org.entity.OrgUser;


/**
 * 用户服务接口
 * @author zealon
 * @date 2018年3月1日
 */
public interface UserService {
	public Set<String> getRolesByUser(String userId);
	public Set<String> getPermissionsByUser(String userId);
	public String findByPage(DataGridRequestDTO dgRequest);
	public Result insert(OrgUser user,String orgDeptId);
	public Result update(OrgUser user,String orgDeptId);
	public Result delete(String id);
	public OrgUser getUserInfoByUserid(String userId);
	OrgUser findById(String id);
	OrgUser selectByUserId(String userid);
}
