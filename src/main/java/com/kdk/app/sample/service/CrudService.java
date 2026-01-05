package com.kdk.app.sample.service;

import java.util.List;

import com.kdk.app.sample.vo.PersonVo;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 5. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public interface CrudService {

	public List<PersonVo> getPersonList();

	public PersonVo getPerson(int seq);

	public void registerPerson(PersonVo vo);

	public void modifyPerson(PersonVo vo);

	public void removePerson(int seq);

}
