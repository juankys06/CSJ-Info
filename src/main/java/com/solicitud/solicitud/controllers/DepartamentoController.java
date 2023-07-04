package com.solicitud.solicitud.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.models.Departamento;
import com.solicitud.solicitud.repository.DepartamentoReposytory;
import com.solicitud.solicitud.repository.EntidadDepartamentoMunicipioRepository;

@RestController
public class DepartamentoController {
	
	@Autowired
	DepartamentoReposytory departamentoReposytory;

	
	@GetMapping("/departamentos")
	public List<Departamento> getDepartamentos(){
		return departamentoReposytory.findAll();
	}
	
	@GetMapping("/departamento/{entidad}")
	public List<Departamento> getEntidadId(@PathVariable(value = "entidad") Long entidadId) {
		return departamentoReposytory.findByEntidad_Id(entidadId);
	}
	
	
	

}
