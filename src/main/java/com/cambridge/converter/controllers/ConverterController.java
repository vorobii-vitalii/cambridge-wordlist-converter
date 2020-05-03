package com.cambridge.converter.controllers;

import com.cambridge.converter.entities.WordList;
import com.cambridge.converter.exceptions.WrongFormatException;
import com.cambridge.converter.services.TransformCambridgeWordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;

@RestController
@RequestMapping(value = "/api")
public class ConverterController {

    private final TransformCambridgeWordListService transformCambridgeWordListService;

    @Autowired
    public ConverterController(TransformCambridgeWordListService transformCambridgeWordListService) {
        this.transformCambridgeWordListService = transformCambridgeWordListService;
    }

    @PostMapping(value = "/json")
    @ResponseStatus(value = HttpStatus.OK)
    public WordList handleConvertJSON(@RequestParam("file") MultipartFile file) throws IOException, WrongFormatException {
        return transformCambridgeWordListService.mapToJson(file);
    }

    @PostMapping(value = "/docx")
    @ResponseStatus(value = HttpStatus.OK)
    public byte[] handleConvertDOCX(@RequestParam("file") MultipartFile file) throws IOException, WrongFormatException {
        ByteArrayOutputStream outputStream = transformCambridgeWordListService.mapToDocx(file);
        final byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        return bytes;
    }

    @PostMapping(value = "/pdf",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> handleConvertPDF(@RequestParam("file") MultipartFile file) throws IOException, WrongFormatException {
        ByteArrayOutputStream outputStream = transformCambridgeWordListService.mapToPdf(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
