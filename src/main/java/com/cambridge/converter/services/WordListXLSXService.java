package com.cambridge.converter.services;

import com.cambridge.converter.entities.Word;
import com.cambridge.converter.entities.WordList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import static com.cambridge.converter.constants.RowConstants.*;
import static com.cambridge.converter.constants.RowConstants.WORD_TRANSLATION;

@Service
public class WordListXLSXService {

    private static Word fromRow(ArrayList<String> row) {
        Word word = new Word();
        for (int i = 0; i < row.size(); i++) {
            String str = row.get(i);
            if (str.equals(""))
                continue;
            switch (i) {
                case WORD_NAME:
                    word.setName(str);
                    break;
                case WORD_TYPE:
                    word.setType(str);
                    break;
                case WORD_MEANING:
                    word.setMeaning(str);
                    break;
                case WORD_TRANSLATION:
                    word.setTranslation(str);
                    break;
            }
        }
        return word;
    }

    public WordList xlsxProcess(InputStream stream) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(stream);

        // We get first sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        // Count line
        int nRow = 0;

        WordList wordList = new WordList();

        Iterator<Row> rowIt = sheet.iterator();
        while(rowIt.hasNext()) {
            Row row = rowIt.next();
            if (nRow == 1) {
                String title = row.getCell(0).toString();
                int indexOfSemiDot = title.indexOf(':');
                wordList.setName( title.substring(indexOfSemiDot + 1).trim() );
            }
            else if (nRow > 1) {
                Iterator<Cell> cellIterator = row.cellIterator();
                ArrayList<String> wordRow = new ArrayList<>();
                cellIterator.forEachRemaining(cell -> {
                    wordRow.add(cell.toString());
                });
                Word word = fromRow(wordRow);
                wordList.addWord(word);
            }
            nRow++;
        }

        workbook.close();
        return wordList;
    }

}
