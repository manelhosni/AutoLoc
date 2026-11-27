package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.autolocapi.domain.Client;
import tn.esprit.autoloc.autolocapi.dto.ClientDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.ClientMapper;
import tn.esprit.autoloc.autolocapi.repository.IClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientDTO create(ClientDTO clientDTO) {
        if (clientDTO.getIdClient() != null) {
            throw new IllegalArgumentException("Un nouveau client ne doit pas avoir d'identifiant");
        }
        validerClient(clientDTO);
        Client client = clientMapper.toEntity(clientDTO);
        Client saved = clientRepository.save(client);
        return clientMapper.toDTO(saved);
    }

    @Override
    public ClientDTO findById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Override
    public List<ClientDTO> findAll() {
        return clientMapper.toDTOList(clientRepository.findAll());
    }

    @Override
    public ClientDTO update(Long id, ClientDTO clientDTO) {
        Client existant = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        validerClient(clientDTO);
        existant.setNom(clientDTO.getNom());
        existant.setPrenom(clientDTO.getPrenom());
        existant.setEmail(clientDTO.getEmail());
        existant.setTelephone(clientDTO.getTelephone());
        existant.setNumPermis(clientDTO.getNumPermis());
        existant.setDateInscription(clientDTO.getDateInscription());
        Client updated = clientRepository.save(existant);
        return clientMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }

    private void validerClient(ClientDTO clientDTO) {
        if (clientDTO.getNom() == null || clientDTO.getNom().trim().isEmpty() ||
                clientDTO.getEmail() == null || clientDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom et l'email du client ne peuvent pas être vides");
        }
    }
}
