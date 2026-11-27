package tn.esprit.autoloc.autolocapi.service;

import tn.esprit.autoloc.autolocapi.dto.EmployeDTO;

import java.util.List;

public interface IEmployeService {
    EmployeDTO create(EmployeDTO employeDTO);
    EmployeDTO findById(Long id);
    List<EmployeDTO> findAll();
    EmployeDTO update(Long id, EmployeDTO employeDTO);
    void deleteById(Long id);
}
