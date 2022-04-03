package ufv.gradi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class ExtratorPDF {
    private Tika extrator = new Tika();
    String diretorio = "/home/slo/gradi/txts/";

    public ExtratorPDF() {
        new File(diretorio).mkdirs();
    }

    public void analisarDiretorio(File diretorio) throws IOException, TikaException {
        File[] arquivosParaIndexar = diretorio.listFiles();
        for (File arquivo : arquivosParaIndexar) {
            if (arquivo.isDirectory()) {
                analisarDiretorio(arquivo);
            } else {
                converterArquivo(arquivo);
            }
        }
    }

    private void converterArquivo(File arquivo) throws FileNotFoundException, IOException, TikaException {
        String textoArquivo = "";
        try {
            textoArquivo = extrator.parseToString(new FileInputStream(arquivo));
        } catch (Throwable e) {
            return;
        }
        if (textoArquivo.trim().length() == 0) {
            return;
        }
        String fileName = diretorio + arquivo.getName() + ".txt";
        FileWriter writer = new FileWriter(fileName);
        writer.write(textoArquivo);
        writer.close();
    }

    public static void extrair(){
        try {
            new ExtratorPDF().analisarDiretorio(
                    new File("/home/slo/gradi/artigos/"));
        } catch (IOException | TikaException e) {
            e.printStackTrace();
        }
    }
    public static void extrairAdicao(){
        try {
            new ExtratorPDF().analisarDiretorio( new File("/home/slo/gradi/adicao/"));
        } catch (IOException | TikaException e) {
            e.printStackTrace();
        }
    }
}