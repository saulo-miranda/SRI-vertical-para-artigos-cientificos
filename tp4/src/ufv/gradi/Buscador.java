package ufv.gradi;

    import java.nio.file.Paths;
    import java.util.*;

    import org.apache.lucene.analysis.standard.StandardAnalyzer;
    import org.apache.lucene.document.Document;
    import org.apache.lucene.index.DirectoryReader;
    import org.apache.lucene.index.IndexReader;
    import org.apache.lucene.queryparser.classic.QueryParser;
    import org.apache.lucene.search.IndexSearcher;
    import org.apache.lucene.search.Query;
    import org.apache.lucene.search.ScoreDoc;
    import org.apache.lucene.search.TopDocs;
    import org.apache.lucene.store.Directory;
    import org.apache.lucene.store.FSDirectory;
    import org.jetbrains.annotations.NotNull;

public class Buscador {

        private String diretorioIndice = new String();
        private static final int quantidadeItensRetornados = 20;

        public List<String> buscar(String consulta) {
            List<String> resultado = new ArrayList<>();
            int posicoes = 1;
            try {
                Directory diretorio = FSDirectory.open(Paths.get(diretorioIndice));
                IndexReader reader = DirectoryReader.open(diretorio);
                IndexSearcher searcher = new IndexSearcher(reader);
                QueryParser parser = new QueryParser("background check", new StandardAnalyzer());
                 parser.setAllowLeadingWildcard(true);
                Query query = parser.parse(consulta);
                TopDocs docs = searcher.search(query, quantidadeItensRetornados);
                System.out.println("TOTAL DE RELEVANTES ->" + docs.totalHits);
                for (ScoreDoc sd : docs.scoreDocs) {
                    Document doc = searcher.doc(sd.doc);
                    System.out.println("[" + posicoes + "]" + " " + doc.get("caminho"));
                    System.out.println("Tamanho: " + doc.get("tamanho") + " Bytes");
                    resultado.add(doc.get("caminho"));
                    posicoes++;
                }
                reader.close();
                diretorio.close();
            } catch (Exception e) {

            }
            return resultado;
        }

        public void setDiretorioIndice(String diretorioInd){
            diretorioIndice = diretorioInd;
        }
}
