package com.cpda.common.base;

import java.util.List;

/**
 * MyBatis Mapper基础接口，其他Mapper接口 请继承该接口
 * 
 */
public interface BaseMapper<T>{
	
	int insert(T record);								//新增记录
	int insertList(List<T> recordList);					//批量新增
	int update(T record);								//修改记录
	int deleteById(Long id);							//按id删除记录
	int deleteByIds(Long[] ids);						//按ids删除记录
	T selectById(Long id);								//按id查询实体
	List<T> selectByIds(Long ids);						//按id查询集合
	int selectCount();									//按条件查询总数
	List<T> selectAll();								//查询全部集合
	boolean existsWithKeyName(Object key);				//按keyname判断记录是否已存在
}
