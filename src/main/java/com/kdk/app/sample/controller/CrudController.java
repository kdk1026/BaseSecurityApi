package com.kdk.app.sample.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdk.app.common.vo.CommonResVo;
import com.kdk.app.common.vo.ResponseCodeEnum;
import com.kdk.app.sample.service.CrudService;
import com.kdk.app.sample.vo.PersonVo;

import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
@RestController
@RequestMapping("/sample/persons")
public class CrudController {

	private final CrudService crudService;

	@GetMapping
	public ResponseEntity<List<PersonVo>> persons() {
		List<PersonVo> list = crudService.getPersonList();

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@GetMapping("/{seq}")
	public ResponseEntity<PersonVo> person(@PathVariable int seq) {
		PersonVo personVo = crudService.getPerson(seq);

		return ResponseEntity.status(HttpStatus.OK).body(personVo);
	}

	@PostMapping
	public ResponseEntity<CommonResVo> registerPerson(@RequestBody PersonVo vo) {
		CommonResVo resVo = new CommonResVo();

		crudService.registerPerson(vo);

		resVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		resVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(resVo);
	}

	@PutMapping
	public ResponseEntity<CommonResVo> modifyPerson(@RequestBody PersonVo vo) {
		CommonResVo resVo = new CommonResVo();

		crudService.modifyPerson(vo);

		resVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		resVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(resVo);
	}

	@DeleteMapping("/{seq}")
	public ResponseEntity<CommonResVo> removePerson(@PathVariable int seq) {
		CommonResVo resVo = new CommonResVo();

		crudService.removePerson(seq);

		resVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		resVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(resVo);
	}

}
