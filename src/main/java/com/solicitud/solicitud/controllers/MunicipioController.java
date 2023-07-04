package com.solicitud.solicitud.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.models.Municipio;
import com.solicitud.solicitud.repository.MunicipioRepository;

@RestController
public class MunicipioController {
	@Autowired
	MunicipioRepository municipioRepository;
	
	@GetMapping("/municipios")
	public List<Municipio> getMunicipios(){
		return municipioRepository.findAll();
	}
	
	@GetMapping("/municipios/{entidad}")
	public List<Municipio> getEntidadId(@PathVariable(value = "entidad") Long entidadId) {
		return municipioRepository.findByEntidad_Id(entidadId);
	}
	
	
}
