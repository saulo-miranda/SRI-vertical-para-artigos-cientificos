package ufv.gradi;

    import java.io.File;
    import java.io.FileInputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Paths;
    import java.util.Date;

    import org.apache.commons.io.FileUtils;
    import org.apache.lucene.analysis.Analyzer;
    import org.apache.lucene.analysis.standard.StandardAnalyzer;
    import org.apache.lucene.document.DateTools;
    import org.apache.lucene.document.DateTools.Resolution;
    import org.apache.lucene.document.Document;
    import org.apache.lucene.document.Field.Store;
    import org.apache.lucene.document.LongPoint;
    import org.apache.lucene.document.StringField;
    import org.apache.lucene.document.TextField;
    import org.apache.lucene.index.DirectoryReader;
    import org.apache.lucene.index.IndexWriter;
    import org.apache.lucene.index.IndexWriterConfig;
    import org.apache.lucene.index.Term;
    import org.apache.lucene.search.IndexSearcher;
    import org.apache.lucene.search.TermQuery;
    import org.apache.lucene.search.TopDocs;
    import org.apache.lucene.store.Directory;
    import org.apache.lucene.store.FSDirectory;
    import org.apache.tika.Tika;
    import org.apache.tika.exception.TikaException;

    public class Indexador {

        protected IndexWriter writer;
        private Directory diretorio;
        private Directory secundario;
        protected Tika extrator = new Tika();
        private boolean recursivo;
        private String diretorioIndice;
        private String diretorioDocumentos;
        private String diretorioAdicao;
        protected long totalArquivosIndexados;
        protected long totalBytesIndexados;
        private boolean apagarIndice;
        private ExtratorPDF extratorPDF = new ExtratorPDF();

        public void inicializar() throws IOException {
            if (apagarIndice) {
                FileUtils.deleteDirectory(new File(diretorioIndice));
            }
            Analyzer analyzer = new StandardAnalyzer();
            diretorio = FSDirectory.open(Paths.get(diretorioIndice));
            IndexWriterConfig conf = new IndexWriterConfig(analyzer);
            conf.setUseCompoundFile(false);
            writer = new IndexWriter(diretorio, conf);
        }

        public void finalizar() {
            try {
                writer.close();
                diretorio.close();

            } catch (IOException e) {

            }
        }

        public void indexar() throws IOException, TikaException {
            indexarDiretorio(new File(diretorioDocumentos));
        }

        public void indexarDiretorio(File diretorio)
                throws IOException, TikaException {
            File[] arquivosParaIndexar = diretorio.listFiles();
            for (File arquivo : arquivosParaIndexar) {
                if (arquivo.isDirectory()) {
                    if (isRecursivo()) {
                        indexarDiretorio(arquivo);
                    }
                } else {
                    indexarArquivo(arquivo);
                }
            }
        }

        private boolean isRecursivo() {
            return recursivo;
        }

        public void indexarArquivo(File arquivo) {
            try {
                Document doc = new Document();
                String extensao = consultarExtensaoArquivo(arquivo.getName());
                String textoArquivo = "";
                InputStream is = new FileInputStream(arquivo);
                try {
                    textoArquivo = extrator.parseToString(is);
                } catch (Throwable e) {

                } finally {
                    is.close();
                }
                doc.add(new TextField("conteudo", textoArquivo, Store.YES));
                doc.add(new TextField("resumo", textoArquivo.substring(0, 2000), Store.YES));
                doc.add(new TextField("tamanho", String.valueOf(arquivo.length()), Store.YES));
                doc.add(new StringField("caminho", arquivo.getAbsolutePath(), Store.YES));
                doc.add(new StringField("extensao", extensao, Store.YES));
                writer.addDocument(doc);
                totalArquivosIndexados++;
                totalBytesIndexados += arquivo.length();
            } catch (Exception e) {

            }
        }

        protected String consultarExtensaoArquivo(String nome) {
            int posicaoDoPonto = nome.lastIndexOf('.');
            if (posicaoDoPonto > 1) {
                return nome
                        .substring(posicaoDoPonto + 1, nome.length())
                        .toLowerCase();
            }
            return "";
        }

        public void setApagarIndice(boolean apagarIndice) {
            this.apagarIndice = apagarIndice;
        }

        public void setDiretorioIndice(String diretorioIndice) {
            this.diretorioIndice = diretorioIndice;
        }

        public void setRecursivo(boolean recursivo) {
            this.recursivo = recursivo;
        }

        public void setDiretorioDocumentos(String diretorioDocumentos) {
            this.diretorioDocumentos = diretorioDocumentos;
        }

        public void setDiretorioAdicao(String diretorioAdicao) {
            this.diretorioAdicao = diretorioAdicao;
        }



        public void adicionarIndice() {

            try {
                File secundario = new File(diretorioAdicao);
                File[] novosDocumentos = secundario.listFiles();
                String caminhoArtigos = "/home/slo/gradi/artigos/";
                String caminhoTxts = "/home/slo/gradi/txts/";
                ExtratorPDF.extrairAdicao();
                setApagarIndice(false);
                inicializar();

                for (File f : novosDocumentos){
                    indexarArquivo(new File(caminhoTxts + f.getName() + ".txt"));
                    f.renameTo(new File(caminhoArtigos, f.getName()));
                    System.out.println("ADICIONADO -> " + f.getName());
                }
                finalizar();

            } catch (Exception e) {

            }
        }



}
