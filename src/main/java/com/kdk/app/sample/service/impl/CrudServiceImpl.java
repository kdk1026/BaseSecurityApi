package com.kdk.app.sample.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.kdk.app.sample.service.CrudService;
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
@Service
public class CrudServiceImpl implements CrudService {

	private final List<PersonVo> personList = new CopyOnWriteArrayList<>();

	private CrudServiceImpl() {
		personList.add(new PersonVo(1, "홍길동", 20));
		personList.add(new PersonVo(2, "임꺽정", 25));
	}

	@Override
	public List<PersonVo> getPersonList() {
		return personList;
	}

	@Override
	public PersonVo getPerson(int seq) {
		PersonVo retVo = null;

		for (PersonVo personVo : personList) {
			if ( personVo.getSeq() == seq ) {
				retVo = personVo;
				break;
			}
		}

		return retVo;
	}

	@Override
	public void registerPerson(PersonVo vo) {
		int seq = 1;
		if ( !personList.isEmpty() ) {
			seq = personList.get(personList.size() -1).getSeq() + 1;
		}
		vo.setSeq(seq);

		personList.add(vo);
	}

	@Override
	public PersonVo modifyPerson(PersonVo vo) {
		PersonVo retVo = null;

		for (PersonVo personVo : personList) {
			if ( Objects.equals(personVo.getSeq(), vo.getSeq()) ) {
				personList.set(personVo.getSeq(), vo);
				retVo = personVo;
				break;
			}
		}

		return retVo;
	}

	@Override
	public PersonVo removePerson(int seq) {
		PersonVo retVo = null;

		for (PersonVo personVo : personList) {
			if ( personVo.getSeq() == seq ) {
				personList.remove(personVo);
				retVo = personVo;
				break;
			}
		}

		return retVo;
	}

}
