package com.example.PdfReader.modelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DocumentData {

    private final String filename;

    private final List<String> pages;

    private final long uploadTimestamp;

}