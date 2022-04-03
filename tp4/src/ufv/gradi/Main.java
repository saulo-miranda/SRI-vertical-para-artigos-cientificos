package ufv.gradi;

import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.awt.Desktop;

public class Main {

    public static void menu(Indexador indexador, Buscador buscador) throws IOException {
        int modo = -1;
        Scanner scanner = new Scanner(System.in);
        String query = new String();


        List<String> resultado;

        while (modo != 0){

            limparTela();
            barra();
            System.out.println("MENU INICIAL");
            barra();
            System.out.print("[1] Realizar uma busca:\n" +
                    "[2] Realizar busca avançada (query no formato do lucene):\n" +
                    "[3] Indexar novos arquivos:\n" +
                    "[0] Para encerrar a aplicação\n\n" +
                    "Opção desejada: ");

            try{
                modo = Integer.parseInt(scanner.next());
            }
            catch (NumberFormatException e){

                System.out.println("Deve ser digitado um valor inteiro!");
                modo = scanner.nextInt();
            }
            //Navegação entre os modos
            switch (modo){
                case 1:
                    query = montarQuery();
                    resultado = buscador.buscar(query);
                    abrirPDF(resultado);
                    break;
                case 2:
                    query = leituraQuery();
                    resultado = buscador.buscar(query);
                    abrirPDF(resultado);
                    break;
                case 3:
                    indexador.adicionarIndice();
                    break;
                default:
                    System.out.println("Opção Inválida!");
            }

        }


    }
    private static void barra(){
        System.out.println("------------------------------");
    }

    public static void limparTela() {
        System.out.print("\n\n\n");
        System.out.flush();
    }

    public static void printQuery(String query){
        System.out.println("\nQUERY -> " + query);
    }

    public static String montarQuery(){
        String busca = new String();
        String query = new String();
        Scanner scanner = new Scanner(System.in);

        barra();
        System.out.println("REALIZAR BUSCA");
        barra();
        System.out.print("Buscar: ");
        busca = scanner.nextLine();
        query = "conteudo:("+ busca + ")";

        printQuery(query);

        return query;
    }

    public static String leituraQuery(){
        String query = new String();
        Scanner scanner = new Scanner(System.in);

        barra();
        System.out.println("REALIZAR BUSCA AVANÇADA");
        barra();
        System.out.println("A consulta deve ser no formato campo:termo\n" +
                "Os campos disponíveis são conteudo, resumo, extensao e tamanho\n" +
                "E essas consultas podem ser combinadas com os operadores AND, OR e NOT");
        System.out.print("Buscar: ");
        query = scanner.nextLine();

        printQuery(query);

        return query;
    }

    public static void abrirPDF(List<String> resultado) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int posicao;
        Desktop desktop = Desktop.getDesktop();
        String caminho = new String();

        if(resultado.size() == 0){
            return;
        }
        else{
            System.out.println("Se desejar abrir algum documento digite a posição do mesmo:\n" +
                    "Para sair digite 0");
            try{
                posicao = Integer.parseInt(scanner.next());
            }
            catch (NumberFormatException e){
                System.out.println("Deve ser digitado um valor inteiro!");
                posicao = scanner.nextInt();
            }
            if (posicao == 0){
                return;
            }
            caminho = resultado.get(posicao-1);
            caminho = caminho.substring(0,16) + "artigos" + caminho.substring(20,caminho.length()-4);
            desktop.open(new File(caminho));

        }
    }

    public static void main(String[] args) throws IOException, TikaException {
        String termo = new String();
        ExtratorPDF extratorPDF = new ExtratorPDF();
        Buscador buscador = new Buscador();
        Indexador indexador = new Indexador();

        extratorPDF.extrair();
        indexador.setDiretorioDocumentos("/home/slo/gradi/txts/");
        indexador.setDiretorioIndice("/home/slo/gradi/indices/");
        indexador.setDiretorioAdicao("/home/slo/gradi/adicao/");
        buscador.setDiretorioIndice("/home/slo/gradi/indices/");
        indexador.setApagarIndice(true);
        indexador.inicializar();
        indexador.indexar();
        indexador.finalizar();

        buscador.buscar(termo);
        menu(indexador, buscador);


    }
}
