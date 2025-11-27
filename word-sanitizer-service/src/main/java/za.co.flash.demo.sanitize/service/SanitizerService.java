package za.co.flash.demo.sanitize.service;

import za.co.flash.demo.sanitize.dto.SensitiveWordResponseDTO;

import java.util.List;

public interface SanitizerService {

    SensitiveWordResponseDTO sanitize(String input);

    SensitiveWordResponseDTO findAllWords();
}
