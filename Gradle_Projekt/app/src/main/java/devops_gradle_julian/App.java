package devops_gradle_julian;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.PseudoText;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new App().getGreeting());

        // ASCII Render Beispiel
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(120).height(20);
        builder.element(new PseudoText("DevOps"));
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();
        System.out.println(s);

        // PDF Beispiel
        PDDocument helloPdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        helloPdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(helloPdf, page);

        // Text für die Überschrift
        String title = "Die Seen in der Schweiz";
        String bodenseeText = "Der Bodensee ist ein 63 km langer See in den Alpen, "
                            + "der von Deutschland, Österreich und der Schweiz geteilt wird. "
                            + "Er ist der drittgrößte See Mitteleuropas.";
        String zurichseeText = "Der Zürichsee ist ein See in der Schweiz, "
                            + "der durch die Stadt Zürich im Norden und die Kantone Zürich, St. Gallen und Schwyz im Süden begrenzt wird. "
                            + "Er ist bekannt für seine malerische Landschaft und touristischen Attraktionen.";

        // Überschrift
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText(title);
        contentStream.endText();

        // Funktion zum Schreiben von Text mit automatischem Zeilenumbruch
        writeTextWithLineBreaks(contentStream, bodenseeText, 50, 700, 12, 500);
        writeTextWithLineBreaks(contentStream, zurichseeText, 50, 650, 12, 500);

        contentStream.close();

        helloPdf.save(new File("beispiel_pdf.pdf"));
        helloPdf.close();

        System.out.println("PDF erstellt: beispiel_pdf.pdf");

        // JFreeChart Beispiel
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JFreeChart Beispiel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(createChartPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static ChartPanel createChartPanel() {
        String chartTitle = "Programming Languages Usage";
        String categoryAxisLabel = "Language";
        String valueAxisLabel = "Usage";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(81, "Usage", "Java");
        dataset.addValue(78, "Usage", "Python");
        dataset.addValue(69, "Usage", "JavaScript");
        dataset.addValue(50, "Usage", "C++");

        JFreeChart chart = ChartFactory.createBarChart(chartTitle, categoryAxisLabel, valueAxisLabel, dataset, PlotOrientation.VERTICAL, false, true, false);

        return new ChartPanel(chart);
    }

    private static void writeTextWithLineBreaks(PDPageContentStream contentStream, String text, float x, float y, int fontSize, float width) throws IOException {
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        contentStream.setFont(font, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
            if (font.getStringWidth(line.toString()) / 1000 * fontSize > width) {
                contentStream.showText(line.toString());
                contentStream.newLineAtOffset(0, -fontSize * 1.2f);
                line = new StringBuilder();
            }
        }
        contentStream.showText(line.toString());
        contentStream.endText();
    }
}
