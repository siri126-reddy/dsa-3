import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

// Class representing an individual Article
class Article {
    private int articleId;
    private String title;
    private String content;
    private int wordCount;

    public Article(int articleId, String title, String content) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.wordCount = calculateWordCount(content);
    }

    // Calculates word count from the content string
    private int calculateWordCount(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        // Split by whitespace to accurately count words
        String[] words = text.trim().split("\\s+");
        return words.length;
    }

    // Getters
    public int getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getWordCount() {
        return wordCount;
    }
}

// In-Memory Repository to store Article objects
class ArticleRepository {
    // LinkedHashMap maintains insertion order for predictable output
    private Map<Integer, Article> repository;

    public ArticleRepository() {
        this.repository = new LinkedHashMap<>();
    }

    public void addArticle(Article article) {
        repository.put(article.getArticleId(), article);
    }

    public void displayRepository() {
        System.out.println("======================================");
        System.out.println("      TEXTHACK ARTICLE REPOSITORY     ");
        System.out.println("======================================");

        int totalWords = 0;

        for (Article article : repository.values()) {
            System.out.println("-------------------------------------------");
            System.out.println("Article ID : " + article.getArticleId());
            System.out.println("Title      : " + article.getTitle());
            System.out.println("Word Count : " + article.getWordCount());
            System.out.println("Content :");
            System.out.println(article.getContent());
            System.out.println("-------------------------------------------");

            totalWords += article.getWordCount();
        }

        System.out.println("\nRepository Statistics");
        System.out.println("----------------------");
        System.out.println("Total Articles Loaded : " + repository.size());
        System.out.println("Total Words           : " + totalWords);
    }
}

// Loads articles from text files in a given folder
class CorpusLoader {
    private String folderPath;

    public CorpusLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    public void loadCorpus(ArticleRepository repository, int startId) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Error: Directory '" + folderPath + "' not found.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No text files found in directory: " + folderPath);
            return;
        }

        // Sort files alphabetically so they are processed in order (e.g., a1.txt, a2.txt)
        Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));

        int currentId = startId;

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String title = "";
                StringBuilder contentBuilder = new StringBuilder();

                // Read line by line, ignoring initial empty lines
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        if (title.isEmpty()) {
                            // First non-empty line is treated as the Title
                            title = line.trim();
                        } else {
                            // Subsequent non-empty lines are treated as Content
                            if (contentBuilder.length() > 0) {
                                contentBuilder.append("\n");
                            }
                            contentBuilder.append(line.trim());
                        }
                    }
                }

                if (!title.isEmpty()) {
                    // Create object and add to repository
                    Article article = new Article(currentId, title, contentBuilder.toString());
                    repository.addArticle(article);
                    currentId++;
                }

            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName());
            }
        }
    }
}

// Main Driver Program
public class Main {
    public static void main(String[] args) {
        // Initialize the Article Repository
        ArticleRepository repository = new ArticleRepository();

        // Path to your corpus directory
        String corpusFolderPath = "Corpus";

        // Load files starting from ID 101
        CorpusLoader loader = new CorpusLoader(corpusFolderPath);
        loader.loadCorpus(repository, 101);

        // Display contents and statistics
        repository.displayRepository();
    }
}