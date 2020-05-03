package com.cambridge.converter.services;

import com.cambridge.converter.constants.ColumnNames;
import com.cambridge.converter.constants.MimeTypes;
import com.cambridge.converter.constants.RowConstants;
import com.cambridge.converter.entities.Word;
import com.cambridge.converter.entities.WordList;
import com.cambridge.converter.exceptions.WrongFormatException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.*;
import java.util.List;

@Service
public class TransformCambridgeWordListService {

    private final WordListXLSXService wordListXLSXService;

    @Autowired
    public TransformCambridgeWordListService(WordListXLSXService wordListXLSXService) {
        this.wordListXLSXService = wordListXLSXService;
    }

    public WordList mapToJson(MultipartFile file) throws IOException, WrongFormatException {

        String mimeType = new Tika().detect(file.getBytes());
        System.out.println("provided extension: " + mimeType);
        if (!mimeType.equals(MimeTypes.XLSX))
            throw new WrongFormatException("Expected .xlsx file");

        InputStream fileInputStream = file.getInputStream();
        WordList wordList = wordListXLSXService.xlsxProcess(fileInputStream);
        fileInputStream.close();
        return wordList;
    }

    public ByteArrayOutputStream mapToPdf(MultipartFile file) throws IOException, WrongFormatException {

        WordList wordList = mapToJson(file);
        List<Word> listOfWords = wordList.getWords();

        PDDocument document = new PDDocument();
        Table myTable;

        File fontFile = new ClassPathResource(
                "NotoSansBlack1.ttf").getFile();
        PDType0Font font = PDType0Font.load(document, fontFile);

        // Build the table
        Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(135, 135, 135, 135)
                .addRow(Row.builder()
                        .add(TextCell.builder().text(ColumnNames.WORD_NAME).borderWidth(1).backgroundColor(Color.LIGHT_GRAY).build())
                        .add(TextCell.builder().text(ColumnNames.WORD_TYPE).borderWidth(1).backgroundColor(Color.LIGHT_GRAY).build())
                        .add(TextCell.builder().text(ColumnNames.WORD_MEANING).borderWidth(1).backgroundColor(Color.LIGHT_GRAY).build())
                        .add(TextCell.builder().text(ColumnNames.WORD_TRANSLATION).borderWidth(1).backgroundColor(Color.LIGHT_GRAY).build())
                        .font(font)
                        .build());

        for (Word word: listOfWords) {
            tableBuilder.addRow(
                Row.builder()
                    .add(TextCell.builder().text(transformIfNullStr(word.getName())).borderWidth(1).backgroundColor(Color.WHITE).build())
                    .add(TextCell.builder().text(transformIfNullStr(word.getType())).borderWidth(1).backgroundColor(Color.WHITE).build())
                    .add(TextCell.builder().text(transformIfNullStr(word.getMeaning())).borderWidth(1).backgroundColor(Color.WHITE).build())
                    .add(TextCell.builder().text(transformIfNullStr(word.getTranslation())).borderWidth(1).backgroundColor(Color.WHITE).build())
                    .font(font)
                    .build()
            );
        }
        myTable = tableBuilder.build();
        TableDrawer.builder()
                .table(myTable)
                .startX(25)
                .startY(770)
                .endY(25F) // <-- If the table is bigger, a new page is started
                .build()
                .draw(() -> document, () -> new PDPage(PDRectangle.A4), 25F);

        PDPage page = document.getPage(0);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, false);

        contentStream.beginText();
        contentStream.setFont(font, 22);
        contentStream.newLineAtOffset(210, 800);
        contentStream.showText(wordList.getName());
        contentStream.endText();

        // Closing the content stream
        contentStream.close();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream;
    }

    public ByteArrayOutputStream mapToDocx (MultipartFile file) throws IOException, WrongFormatException {

        WordList wordList = mapToJson(file);
        List<Word> listOfWords = wordList.getWords();
        final int numberOfWords = listOfWords.size();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        XWPFDocument document = new XWPFDocument();

        //create paragraph
        XWPFParagraph paragraph = document.createParagraph();

        XWPFRun paragraphOneRunOne = paragraph.createRun();
        paragraphOneRunOne.setBold(true);
        paragraphOneRunOne.setItalic(true);
        paragraphOneRunOne.setText(wordList.getName());
        paragraphOneRunOne.addBreak();

        // Build table
        XWPFTable table = document.createTable( numberOfWords + 1, RowConstants.LENGTH );
        XWPFTableRow firstRow = table.getRow(0);
        firstRow.getCell(RowConstants.WORD_NAME).setText(ColumnNames.WORD_NAME);
        firstRow.getCell(RowConstants.WORD_TYPE).setText(ColumnNames.WORD_TYPE);
        firstRow.getCell(RowConstants.WORD_MEANING).setText(ColumnNames.WORD_MEANING);
        firstRow.getCell(RowConstants.WORD_TRANSLATION).setText(ColumnNames.WORD_TRANSLATION);
        for (int i = 1; i <= numberOfWords; i++) {
            Word word = listOfWords.get(i-1);
            XWPFTableRow row = table.getRow(i );
            row.getCell(RowConstants.WORD_NAME).setText(word.getName());
            row.getCell(RowConstants.WORD_TYPE).setText(word.getType());
            row.getCell(RowConstants.WORD_MEANING).setText(word.getMeaning());
            row.getCell(RowConstants.WORD_TRANSLATION).setText(word.getTranslation());
        }
        document.write(outputStream);

        return outputStream;
    }

    private static String transformIfNullStr(String str) {
        if (str == null)
            return "";
        return str.replace("\n","<br>");
    }

}
